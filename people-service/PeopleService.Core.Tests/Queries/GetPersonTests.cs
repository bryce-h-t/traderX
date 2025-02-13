using FluentValidation;
using Moq;
using PeopleService.Core.DirectoryService;
using PeopleService.Core.Queries;
using FluentAssertions;

namespace PeopleService.Core.Tests.Queries;

public class GetPersonTests : TestFixture
{
    private readonly GetPerson.RequestHandler _handler;
    private readonly GetPerson.RequestValidator _validator;
    
    public GetPersonTests()
    {
        _handler = new GetPerson.RequestHandler(DirectoryServiceMock.Object);
        _validator = new GetPerson.RequestValidator();
    }

    [Fact]
    public async Task Handle_WithValidLogonId_ReturnsPerson()
    {
        // Arrange
        var request = new GetPerson.Request { LogonId = "user1" };
        var expected = new Person { LogonId = "user1", FullName = "Test User" };
        DirectoryServiceMock.Setup(x => x.GetPerson(request.LogonId, null))
            .ReturnsAsync(expected);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(expected);
    }

    [Fact]
    public async Task Handle_WithValidEmployeeId_ReturnsPerson()
    {
        // Arrange
        var request = new GetPerson.Request { EmployeeId = "emp1" };
        var expected = new Person { EmployeeId = "emp1", FullName = "Test Employee" };
        DirectoryServiceMock.Setup(x => x.GetPerson(null, request.EmployeeId))
            .ReturnsAsync(expected);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(expected);
    }

    [Fact]
    public async Task Handle_WithBothIds_ReturnsPerson()
    {
        // Arrange
        var request = new GetPerson.Request { LogonId = "user1", EmployeeId = "emp1" };
        var expected = new Person { LogonId = "user1", EmployeeId = "emp1", FullName = "Test User" };
        DirectoryServiceMock.Setup(x => x.GetPerson(request.LogonId, request.EmployeeId))
            .ReturnsAsync(expected);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(expected);
    }

    [Fact]
    public async Task Handle_WhenServiceReturnsNull_ReturnsNull()
    {
        // Arrange
        var request = new GetPerson.Request { LogonId = "nonexistent" };
        DirectoryServiceMock.Setup(x => x.GetPerson(request.LogonId, null))
            .ReturnsAsync((Person?)null);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.Should().BeNull();
    }

    [Fact]
    public async Task Handle_WhenServiceThrows_PropagatesException()
    {
        // Arrange
        var request = new GetPerson.Request { LogonId = "error" };
        DirectoryServiceMock.Setup(x => x.GetPerson(request.LogonId, null))
            .ThrowsAsync(new Exception("Service error"));

        // Act
        var act = () => _handler.Handle(request, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<Exception>().WithMessage("Service error");
    }

    [Fact]
    public void Validate_WithNoIds_ReturnsValidationError()
    {
        // Arrange
        var request = new GetPerson.Request();

        // Act
        var result = _validator.Validate(new ValidationContext<GetPerson.Request>(request));

        // Assert
        result.IsValid.Should().BeFalse();
        result.Errors.Should().ContainSingle()
            .Which.ErrorMessage.Should().Contain("Either LogonId or EmployeeId must be provided");
    }
}
