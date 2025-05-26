package services;

import entities.Account;
import interfaces.IAccountService;
import repository.AccountRepository;

import java.util.List;
import java.util.Optional;

public class AccountService implements IAccountService<Account> { // O <Account> aqui é correto

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> getByAccountNumber(String accountNumber) {
        // Validação básica de entrada
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Account number cannot be null or empty for search.");
            return Optional.empty();
        }
        return accountRepository.getByAccountNumber(accountNumber);
    }

    @Override
    public List<Account> getAll() {
        return accountRepository.getAll();
    }
}