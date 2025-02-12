package finos.traderx.accountservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import finos.traderx.accountservice.model.Account;
import finos.traderx.accountservice.exceptions.ResourceNotFoundException;

@SpringBootTest(classes = TestAccountServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountServiceIntegrationTest extends BaseIntegrationTest {
    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateAccount() {
        Account account = createTestAccount("test account");
        assertNotNull(account.getId());
        assertEquals("test account", account.getDisplayName());
    }
    
    @Test
    void shouldGetAccountById() {
        Account created = createTestAccount("test account");
        Account retrieved = accountService.getAccountById(created.getId());
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getDisplayName(), retrieved.getDisplayName());
    }
    
    @Test
    void shouldThrowNotFoundForInvalidId() {
        assertThrows(ResourceNotFoundException.class, () -> 
            accountService.getAccountById(99999));
    }
    
    @Test
    void shouldListAllAccounts() {
        createTestAccount("account 1");
        createTestAccount("account 2");
        List<Account> accounts = accountService.getAllAccount();
        assertTrue(accounts.size() >= 2);
    }
}
