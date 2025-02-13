using FluentValidation;
using Moq;
using PeopleService.Core.DirectoryService;
using PeopleService.Core.Queries;
using FluentAssertions;

namespace PeopleService.Core.Tests.Queries;

public class ValidatePersonTests : TestFixture
{
    private readonly ValidatePerson.RequestHandler _handler;
    private readonly ValidatePerson.RequestValidator _validator;
    
    public ValidatePersonTests()
    {
        _handler = new ValidatePerson.RequestHandler(DirectoryServiceMock.Object);
        _validator = new ValidatePerson.RequestValidator();
    }

    [Fact]
    public async Task Handle_WithValidPerson_ReturnsTrue()
    {
        // Arrange
        var request = new ValidatePerson.Request { LogonId = "user1" };
        DirectoryServiceMock.Setup(x => x.ValidatePerson(request.LogonId, null))
            .ReturnsAsync(true);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.IsValid.Should().BeTrue();
    }

    [Fact]
    public async Task Handle_WithInvalidPerson_ReturnsFalse()
    {
        // Arrange
        var request = new ValidatePerson.Request { LogonId = "nonexistent" };
        DirectoryServiceMock.Setup(x => x.ValidatePerson(request.LogonId, null))
            .ReturnsAsync(false);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.IsValid.Should().BeFalse();
    }

    [Fact]
    public async Task Handle_WhenServiceThrows_PropagatesException()
    {
        // Arrange
        var request = new ValidatePerson.Request { LogonId = "error" };
        DirectoryServiceMock.Setup(x => x.ValidatePerson(request.LogonId, null))
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
        var request = new ValidatePerson.Request();

        // Act
        var result = _validator.Validate(new ValidationContext<ValidatePerson.Request>(request));

        // Assert
        result.IsValid.Should().BeFalse();
        result.Errors.Should().ContainSingle()
            .Which.ErrorMessage.Should().Contain("Either LogonId or EmployeeId must be provided");
    }

    [Fact]
    public async Task Handle_WithBothIds_ValidatesBoth()
    {
        // Arrange
        var request = new ValidatePerson.Request 
        { 
            LogonId = "user1",
            EmployeeId = "emp1"
        };
        DirectoryServiceMock.Setup(x => x.ValidatePerson(request.LogonId, request.EmployeeId))
            .ReturnsAsync(true);

        // Act
        var result = await _handler.Handle(request, CancellationToken.None);

        // Assert
        result.IsValid.Should().BeTrue();
        DirectoryServiceMock.Verify(x => x.ValidatePerson(request.LogonId, request.EmployeeId), Times.Once);
    }
}
