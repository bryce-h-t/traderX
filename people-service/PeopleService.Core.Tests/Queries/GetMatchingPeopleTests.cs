using FluentValidation;
using Moq;
using PeopleService.Core.DirectoryService;
using PeopleService.Core.Queries;
using FluentAssertions;
using CacheManager.Core;

namespace PeopleService.Core.Tests.Queries;

public class GetMatchingPeopleTests : TestFixture
{
    private readonly GetMatchingPeople.RequestHandler _handler;
    private readonly GetMatchingPeople.RequestValidator _validator;
    
    public GetMatchingPeopleTests()
    {
        _handler = new GetMatchingPeople.RequestHandler(
            DirectoryServiceMock.Object,
            CacheManagerMock.Object);
        _validator = new GetMatchingPeople.RequestValidator();
    }

    [Fact]
    public async Task Handle_WithCacheMiss_QueriesServiceAndCachesResult()
    {
        // Arrange
        var request = new GetMatchingPeople.Request { SearchText = "test", Take = 10 };
        var people = new List<Person> { new() { LogonId = "test1", FullName = "Test User" } };
        DirectoryServiceMock.Setup(x => x.GetMatchingPerson(request.SearchText, request.Take))
            .ReturnsAsync(people);
        CacheManagerMock.Setup(x => x.GetCacheItem(It.IsAny<string>()))
            .Returns((CacheItem<GetMatchingPeople.Response>)null);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.Should().NotBeNull();
        result!.People.Should().BeEquivalentTo(people);
        CacheManagerMock.Verify(x => x.Add(It.IsAny<string>(), It.Is<GetMatchingPeople.Response>(r => 
            r.People!.Single().LogonId == "test1")), Times.Once);
    }

    [Fact]
    public async Task Handle_WithCacheHit_ReturnsFromCache()
    {
        // Arrange
        var request = new GetMatchingPeople.Request { SearchText = "test", Take = 10 };
        var cachedResponse = new GetMatchingPeople.Response 
        { 
            People = new List<Person> { new() { LogonId = "cached", FullName = "Cached User" } }
        };
        CacheManagerMock.Setup(x => x.GetCacheItem(It.IsAny<string>()))
            .Returns(new CacheItem<GetMatchingPeople.Response>("key", cachedResponse));

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.Should().NotBeNull();
        result!.People.Should().BeEquivalentTo(cachedResponse.People);
        DirectoryServiceMock.Verify(x => x.GetMatchingPerson(It.IsAny<string>(), It.IsAny<int>()), Times.Never);
    }

    [Fact]
    public async Task Handle_WithNoResults_ReturnsNull()
    {
        // Arrange
        var request = new GetMatchingPeople.Request { SearchText = "nonexistent", Take = 10 };
        DirectoryServiceMock.Setup(x => x.GetMatchingPerson(request.SearchText, request.Take))
            .ReturnsAsync(new List<Person>());
        CacheManagerMock.Setup(x => x.GetCacheItem(It.IsAny<string>()))
            .Returns((CacheItem<GetMatchingPeople.Response>)null);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.Should().BeNull();
    }

    [Fact]
    public async Task Handle_WithCustomTakeParameter_UsesSpecifiedValue()
    {
        // Arrange
        var request = new GetMatchingPeople.Request { SearchText = "test", Take = 5 };
        var people = new List<Person> { new() { LogonId = "test1", FullName = "Test User" } };
        DirectoryServiceMock.Setup(x => x.GetMatchingPerson(request.SearchText, request.Take))
            .ReturnsAsync(people);
        CacheManagerMock.Setup(x => x.GetCacheItem(It.IsAny<string>()))
            .Returns((CacheItem<GetMatchingPeople.Response>)null);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        DirectoryServiceMock.Verify(x => x.GetMatchingPerson(request.SearchText, 5), Times.Once);
    }

    [Fact]
    public async Task Handle_WhenServiceThrows_PropagatesException()
    {
        // Arrange
        var request = new GetMatchingPeople.Request { SearchText = "error", Take = 10 };
        DirectoryServiceMock.Setup(x => x.GetMatchingPerson(request.SearchText, request.Take))
            .ThrowsAsync(new Exception("Service error"));
        CacheManagerMock.Setup(x => x.GetCacheItem(It.IsAny<string>()))
            .Returns((CacheItem<GetMatchingPeople.Response>)null);

        // Act
        var act = () => _handler.Handle(request, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<Exception>().WithMessage("Service error");
    }

    [Fact]
    public void Validate_WithEmptySearchText_ReturnsValidationError()
    {
        // Arrange
        var request = new GetMatchingPeople.Request { SearchText = "" };

        // Act
        var result = _validator.Validate(new ValidationContext<GetMatchingPeople.Request>(request));

        // Assert
        result.IsValid.Should().BeFalse();
        result.Errors.Should().ContainSingle()
            .Which.ErrorMessage.Should().Contain("SearchText must be provided");
    }

    [Fact]
    public void Validate_WithShortSearchText_ReturnsValidationError()
    {
        // Arrange
        var request = new GetMatchingPeople.Request { SearchText = "ab" };

        // Act
        var result = _validator.Validate(new ValidationContext<GetMatchingPeople.Request>(request));

        // Assert
        result.IsValid.Should().BeFalse();
        result.Errors.Should().ContainSingle()
            .Which.ErrorMessage.Should().Contain("must be at least 3 characters long");
    }
}
