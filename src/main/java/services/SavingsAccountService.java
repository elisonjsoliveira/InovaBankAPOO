package services;

import entities.SavingsAccount;
import interfaces.ISavingsAccountService;
import repository.CurrentAccountRepository;
import repository.SavingsAccountRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class SavingsAccountService implements ISavingsAccountService<SavingsAccount> {

    private final SavingsAccountRepository savingsAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository,
                                 CurrentAccountRepository currentAccountRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.currentAccountRepository = currentAccountRepository;
    }

    @Override
    public void create(SavingsAccount account) {
        if (account != null) {
            savingsAccountRepository.create(account);
        } else {
            System.out.println("Savings account can't be null");
        }
    }

    @Override
    public Optional<SavingsAccount> getByAccountNumber(String accountNumber) {
        return savingsAccountRepository.getByAccountNumber(accountNumber);
    }

    @Override
    public void update(SavingsAccount account) {
        if (account != null) {
            savingsAccountRepository.update(account);
        } else {
            System.out.println("Savings account does not exist");
        }
    }

    @Override
    public void delete(String accountNumber) {
        savingsAccountRepository.delete(accountNumber);
    }

    @Override
    public void applyInterest(String accountNumber) {
        Optional<SavingsAccount> savingsOpt = savingsAccountRepository.getByAccountNumber(accountNumber);

        if (savingsOpt.isEmpty()) {
            System.out.println("Savings account not found.");
            return;
        }

        SavingsAccount savings = savingsOpt.get();

        // Aplicar juros (supondo que tenha um método getInterestRate() que retorna BigDecimal)
        BigDecimal interest = savings.getBalance().multiply(savings.getInterestRate());
        savings.setBalance(savings.getBalance().add(interest));

        savingsAccountRepository.update(savings);
        System.out.println("Interest applied: " + interest);
    }

    @Override
    public void transferToCurrent(String savingsAccountNumber, String currentAccountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid transfer amount");
            return;
        }

        Optional<SavingsAccount> savingsOpt = savingsAccountRepository.getByAccountNumber(savingsAccountNumber);
        Optional<entities.CurrentAccount> currentOpt = currentAccountRepository.getByAccountNumber(currentAccountNumber);

        if (savingsOpt.isEmpty()) {
            System.out.println("Savings account not found.");
            return;
        }
        if (currentOpt.isEmpty()) {
            System.out.println("Current account not found.");
            return;
        }

        SavingsAccount savings = savingsOpt.get();
        entities.CurrentAccount current = currentOpt.get();

        if (savings.getBalance().compareTo(amount) < 0) {
            System.out.println("Insufficient balance in savings account.");
            return;
        }

        // Debit savings account
        savings.setBalance(savings.getBalance().subtract(amount));
        savingsAccountRepository.update(savings);

        // Credit current account
        current.setBalance(current.getBalance().add(amount));
        currentAccountRepository.update(current);

        System.out.println("Transferred " + amount + " from Savings Account to Current Account.");
    }

    @Override
    public List<SavingsAccount> getAll() {
        return savingsAccountRepository.getAll();
    }
}
