package services;

import entities.Account;
import interfaces.IAccountService;
import repository.AccountRepository;

import java.util.List;
import java.util.Optional;

public class AccountService implements IAccountService<Account> {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> getByAccountNumber(String accountNumber) {
        return accountRepository.getByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> getAll() {
        return accountRepository.getAll();
    }
}
