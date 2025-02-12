package finos.traderx.accountservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import finos.traderx.accountservice.exceptions.ResourceNotFoundException;
import finos.traderx.accountservice.model.Account;
import finos.traderx.accountservice.model.AccountUser;
import finos.traderx.accountservice.model.Person;
import finos.traderx.accountservice.repository.AccountRepository;
import finos.traderx.accountservice.repository.AccountUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountUserService {

	@Autowired
	AccountUserRepository accountUserRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${people.service.url}")
	private String peopleServiceAddress;

	public List<AccountUser> getAllAccountUsers() {
		List<AccountUser> accountUsers = new ArrayList<AccountUser>();
		this.accountUserRepository.findAll().forEach(accountUser -> accountUsers.add(accountUser));
		return accountUsers;
	}

	public AccountUser getAccountUserById(int id) throws ResourceNotFoundException {
		Optional<AccountUser> accountUser = this.accountUserRepository.findById(Integer.valueOf(id));
		if (accountUser.isEmpty()) {
			throw new ResourceNotFoundException("AccountUser with id " + id + "not found");
		}
		return accountUser.get();
	}

	public AccountUser upsertAccountUser(AccountUser accountUser) {
		Optional<Account> account = this.accountRepository.findById(accountUser.getAccountId());
		if (account.isEmpty()) {
			throw new ResourceNotFoundException("Account with id " + accountUser.getAccountId() + "not found");
		}

		String url = this.peopleServiceAddress + "/People/GetPerson?LogonId=" + accountUser.getUsername();
		try {
			restTemplate.getForEntity(url, Person.class);
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				throw new ResourceNotFoundException(accountUser.getUsername() + " not found in People service.");
			}
			throw ex;
		}

		return this.accountUserRepository.save(accountUser);
	}
}
