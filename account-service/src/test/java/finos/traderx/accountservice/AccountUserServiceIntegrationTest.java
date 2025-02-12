package finos.traderx.accountservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import finos.traderx.accountservice.exceptions.ResourceNotFoundException;
import finos.traderx.accountservice.model.Account;
import finos.traderx.accountservice.model.AccountUser;
import finos.traderx.accountservice.model.Person;

@SpringBootTest(classes = TestAccountServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountUserServiceIntegrationTest extends BaseIntegrationTest {
    
    @Test
    void shouldCreateAccountUserMapping() {
        Account account = createTestAccount("team account");
        AccountUser user = new AccountUser();
        user.setAccountId(account.getId());
        user.setUsername("testuser");
        
        mockValidUser("testuser");
        AccountUser created = accountUserService.upsertAccountUser(user);
        
        assertEquals(account.getId(), created.getAccountId());
        assertEquals("testuser", created.getUsername());
    }
    
    @Test
    void shouldFailForInvalidUser() {
        Account account = createTestAccount("team account");
        AccountUser user = new AccountUser();
        user.setAccountId(account.getId());
        user.setUsername("invalid");
        
        mockInvalidUser("invalid");
        assertThrows(ResourceNotFoundException.class, () ->
            accountUserService.upsertAccountUser(user));
    }
    
    @Test
    void shouldFailForInvalidAccount() {
        AccountUser user = new AccountUser();
        user.setAccountId(99999);
        user.setUsername("testuser");
        
        mockValidUser("testuser");
        assertThrows(ResourceNotFoundException.class, () ->
            accountUserService.upsertAccountUser(user));
    }
    
    private void mockValidUser(String username) {
        when(restTemplate.getForEntity(
            contains("/People/GetPerson?LogonId=" + username),
            eq(Person.class)))
            .thenReturn(new ResponseEntity<>(new Person(), HttpStatus.OK));
    }
    
    private void mockInvalidUser(String username) {
        when(restTemplate.getForEntity(
            contains("/People/GetPerson?LogonId=" + username),
            eq(Person.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
