using Moq;
using CacheManager.Core;
using PeopleService.Core.DirectoryService;
using PeopleService.Core.Queries;

namespace PeopleService.Core.Tests;

public class TestFixture
{
    protected Mock<IDirectoryService> DirectoryServiceMock { get; }
    protected Mock<ICacheManager<GetMatchingPeople.Response>> CacheManagerMock { get; }
    
    public TestFixture()
    {
        DirectoryServiceMock = new Mock<IDirectoryService>();
        CacheManagerMock = new Mock<ICacheManager<GetMatchingPeople.Response>>();
    }
}
