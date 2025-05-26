package services;

import entities.CurrentAccount; // Importar CurrentAccount para a transferência
import entities.SavingsAccount;
import interfaces.ISavingsAccountService;
import jakarta.persistence.EntityManager; // Importar EntityManager para gerenciamento de transação
import jakarta.persistence.LockModeType; // Importar LockModeType
import jakarta.persistence.NoResultException; // Importar NoResultException
import repository.CurrentAccountRepository;
import repository.SavingsAccountRepository;
import util.JPAUtil; // Importar JPAUtil

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class SavingsAccountService implements ISavingsAccountService<SavingsAccount> {

    private final SavingsAccountRepository savingsAccountRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final ClientService clientService;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository,
                                 ClientService clientService,
                                 CurrentAccountRepository currentAccountRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.clientService = clientService;
        this.currentAccountRepository = currentAccountRepository;
    }

    @Override
    public void create(SavingsAccount account) {
        if (account == null) {
            System.out.println("Savings account can't be null");
            return;
        }
        if (savingsAccountRepository.getByAccountNumber(account.getAccountNumber()).isPresent()) {
            System.out.println("Savings account with number " + account.getAccountNumber() + " already exists.");
            return;
        }
        if (account.getClient() == null || clientService.getByCPF(account.getClient().getCpf()).isEmpty()) {
            System.out.println("Associated client not found or is null. Savings account not created.");
            return;
        }
        try {
            this.savingsAccountRepository.create(account);
            System.out.println("Savings Account created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to create Savings Account: " + e.getMessage());
        }
    }

    @Override
    public Optional<SavingsAccount> getByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Account number cannot be null or empty for search.");
            return Optional.empty();
        }
        return savingsAccountRepository.getByAccountNumber(accountNumber);
    }

    @Override
    public void update(SavingsAccount account) {
        if (account == null) {
            System.out.println("Savings account to update can't be null");
            return;
        }
        Optional<SavingsAccount> existingAccount = savingsAccountRepository.getByAccountNumber(account.getAccountNumber());
        if (existingAccount.isEmpty()) {
            System.out.println("Savings account " + account.getAccountNumber() + " not found for update.");
            return;
        }
        try {
            this.savingsAccountRepository.update(account);
            System.out.println("Savings Account updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to update Savings Account: " + e.getMessage());
        }
    }

    @Override
    public void delete(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Account number cannot be null or empty for deletion.");
            return;
        }
        try {
            savingsAccountRepository.delete(accountNumber);
            System.out.println("Attempted deletion for Savings Account number: " + accountNumber + ".");
        } catch (RuntimeException e) {
            System.out.println("Failed to delete Savings Account: " + e.getMessage());
        }
    }

    @Override
    public void applyInterest(String accountNumber) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            SavingsAccount savings = em.createQuery(
                            "SELECT s FROM SavingsAccount s WHERE s.accountNumber = :accountNumber", SavingsAccount.class)
                    .setParameter("accountNumber", accountNumber)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();

            BigDecimal interest = savings.getBalance().multiply(savings.getInterestRate());
            savings.setBalance(savings.getBalance().add(interest));

            em.merge(savings);

            em.getTransaction().commit();
            System.out.println("Interest applied: " + interest);

        } catch (NoResultException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Savings account not found.");
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            System.out.println("Error applying interest: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void transferToCurrent(String savingsAccountNumber, String currentAccountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid transfer amount");
            return;
        }

        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            SavingsAccount savings = em.createQuery(
                            "SELECT s FROM SavingsAccount s WHERE s.accountNumber = :accountNumber", SavingsAccount.class)
                    .setParameter("accountNumber", savingsAccountNumber)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();

            CurrentAccount current = em.createQuery(
                            "SELECT c FROM CurrentAccount c WHERE c.accountNumber = :accountNumber", CurrentAccount.class)
                    .setParameter("accountNumber", currentAccountNumber)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();

            if (savings.getBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient balance in savings account.");
                em.getTransaction().rollback();
                return;
            }

            savings.setBalance(savings.getBalance().subtract(amount));
            current.setBalance(current.getBalance().add(amount));

            em.merge(savings);
            em.merge(current);

            em.getTransaction().commit();

            System.out.println("Transferred " + amount + " from Savings Account to Current Account.");

        } catch (NoResultException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("One or both accounts not found.");
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            System.out.println("Error during transfer: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<SavingsAccount> getAll() {
        return savingsAccountRepository.getAll();
    }
}