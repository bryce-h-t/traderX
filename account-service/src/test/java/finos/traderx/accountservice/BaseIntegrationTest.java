package finos.traderx.accountservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import finos.traderx.accountservice.model.Account;
import finos.traderx.accountservice.service.AccountService;
import finos.traderx.accountservice.service.AccountUserService;

@TestPropertySource(locations = "/test-application.properties")
public abstract class BaseIntegrationTest {
    @Autowired
    protected AccountService accountService;
    
    @Autowired
    protected AccountUserService accountUserService;
    
    @MockBean
    protected RestTemplate restTemplate;
    
    protected Account createTestAccount(String displayName) {
        Account account = new Account();
        account.setDisplayName(displayName);
        return accountService.upsertAccount(account);
    }
}
