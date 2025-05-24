package services;

import entities.CurrentAccount;
import interfaces.ICurrentAccountService;
import repository.CurrentAccountRepository;
import repository.SavingsAccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CurrentAccountService implements ICurrentAccountService<CurrentAccount> {

    private final CurrentAccountRepository currentAccountRepository;
    private final SavingsAccountRepository savingsAccountRepository;

    public CurrentAccountService(CurrentAccountRepository currentAccountRepository,
                                 SavingsAccountRepository savingsAccountRepository) {
        this.currentAccountRepository = currentAccountRepository;
        this.savingsAccountRepository = savingsAccountRepository;
    }

    @Override
    public void create(CurrentAccount account) {
        if (account != null) {
            currentAccountRepository.create(account);
        } else {
            System.out.println("Current account can't be null");
        }
    }

    @Override
    public Optional<CurrentAccount> getByAccountNumber(String accountNumber) {
        return currentAccountRepository.getByAccountNumber(accountNumber);
    }

    @Override
    public void update(CurrentAccount account) {
        if (account != null) {
            currentAccountRepository.update(account);
        } else {
            System.out.println("Current account does not exist");
        }
    }

    @Override
    public void delete(String accountNumber) {
        currentAccountRepository.delete(accountNumber);
    }

    @Override
    public void transferToSavings(String currentAccountNumber, String savingsAccountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid transfer amount");
            return;
        }

        Optional<CurrentAccount> currentOpt = currentAccountRepository.getByAccountNumber(currentAccountNumber);
        Optional<entities.SavingsAccount> savingsOpt = savingsAccountRepository.getByAccountNumber(savingsAccountNumber);

        if (currentOpt.isEmpty()) {
            System.out.println("Current account not found.");
            return;
        }
        if (savingsOpt.isEmpty()) {
            System.out.println("Savings account not found.");
            return;
        }

        CurrentAccount current = currentOpt.get();
        entities.SavingsAccount savings = savingsOpt.get();

        if (current.getBalance().compareTo(amount) < 0) {
            System.out.println("Insufficient balance in current account.");
            return;
        }

        // Debit current account
        current.setBalance(current.getBalance().subtract(amount));
        currentAccountRepository.update(current);

        // Credit savings account
        savings.setBalance(savings.getBalance().add(amount));
        savingsAccountRepository.update(savings);

        System.out.println("Transferred " + amount + " from Current Account to Savings Account.");
    }

    @Override
    public List<CurrentAccount> getAll() {
        return currentAccountRepository.getAll();
    }
}
