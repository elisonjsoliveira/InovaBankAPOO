package services;

import entities.CurrentAccount;
import entities.SavingsAccount; // Importar SavingsAccount para a transferência
import interfaces.ICurrentAccountService;
import jakarta.persistence.EntityManager; // Importar EntityManager para gerenciamento de transação
import jakarta.persistence.LockModeType; // Importar LockModeType
import jakarta.persistence.NoResultException; // Importar NoResultException
import repository.CurrentAccountRepository;
import repository.SavingsAccountRepository;
import util.JPAUtil; // Importar JPAUtil

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CurrentAccountService implements ICurrentAccountService<CurrentAccount> {

    private final CurrentAccountRepository currentAccountRepository;
    private final SavingsAccountRepository savingsAccountRepository;
    private final ClientService clientService;

    public CurrentAccountService(CurrentAccountRepository currentAccountRepository,
                                 ClientService clientService,
                                 SavingsAccountRepository savingsAccountRepository) {
        this.currentAccountRepository = currentAccountRepository;
        this.clientService = clientService;
        this.savingsAccountRepository = savingsAccountRepository;
    }

    @Override
    public void create(CurrentAccount account) {
        if (account == null) {
            System.out.println("Current account can't be null");
            return;
        }
        if (currentAccountRepository.getByAccountNumber(account.getAccountNumber()).isPresent()) {
            System.out.println("Current account with number " + account.getAccountNumber() + " already exists.");
            return;
        }
        if (account.getClient() == null || clientService.getByCPF(account.getClient().getCpf()).isEmpty()) {
            System.out.println("Associated client not found or is null. Current account not created.");
            return;
        }
        try {
            this.currentAccountRepository.create(account);
            System.out.println("Current Account created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to create Current Account: " + e.getMessage());
        }
    }

    @Override
    public Optional<CurrentAccount> getByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Account number cannot be null or empty for search.");
            return Optional.empty();
        }
        return currentAccountRepository.getByAccountNumber(accountNumber);
    }

    @Override
    public void update(CurrentAccount account) {
        if (account == null) {
            System.out.println("Current account to update can't be null");
            return;
        }
        Optional<CurrentAccount> existingAccount = currentAccountRepository.getByAccountNumber(account.getAccountNumber());
        if (existingAccount.isEmpty()) {
            System.out.println("Current account " + account.getAccountNumber() + " not found for update.");
            return;
        }
        try {
            this.currentAccountRepository.update(account);
            System.out.println("Current Account updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to update Current Account: " + e.getMessage());
        }
    }

    @Override
    public void delete(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            System.out.println("Account number cannot be null or empty for deletion.");
            return;
        }
        try {
            currentAccountRepository.delete(accountNumber);
            System.out.println("Attempted deletion for Current Account number: " + accountNumber + ".");
        } catch (RuntimeException e) {
            System.out.println("Failed to delete Current Account: " + e.getMessage());
        }
    }

    @Override
    public void transferToSavings(String currentAccountNumber, String savingsAccountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid transfer amount");
            return;
        }

        EntityManager em = null; // Declarar o EM aqui para usá-lo no finally
        try {
            em = JPAUtil.getEntityManager(); // Obter o EM
            em.getTransaction().begin(); // INICIAR A TRANSAÇÃO AQUI NO SERVIÇO

            // Buscar as contas DENTRO da mesma transação, com bloqueio pessimista para concorrência
            CurrentAccount current = em.createQuery(
                            "SELECT c FROM CurrentAccount c WHERE c.accountNumber = :accountNumber", CurrentAccount.class)
                    .setParameter("accountNumber", currentAccountNumber)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();

            SavingsAccount savings = em.createQuery(
                            "SELECT s FROM SavingsAccount s WHERE s.accountNumber = :accountNumber", SavingsAccount.class)
                    .setParameter("accountNumber", savingsAccountNumber)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult();

            // Validações
            if (current.getBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient balance in current account.");
                em.getTransaction().rollback(); // Reverter a transação em caso de saldo insuficiente
                return;
            }

            // Lógica de débito e crédito
            current.setBalance(current.getBalance().subtract(amount));
            savings.setBalance(savings.getBalance().add(amount));

            // Persistir as mudanças no MESMO EntityManager da transação
            em.merge(current); // Usar merge para reanexar ou atualizar as entidades
            em.merge(savings);

            em.getTransaction().commit(); // COMITAR A TRANSAÇÃO AQUI NO SERVIÇO

            System.out.println("Transferred " + amount + " from Current Account to Savings Account.");

        } catch (NoResultException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Reverter se alguma conta não for encontrada
            }
            System.out.println("One or both accounts not found.");
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Reverter em caso de qualquer outro erro
            }
            e.printStackTrace(); // Para depuração, substituir por logging em produção
            System.out.println("Error during transfer: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close(); // Fechar o EntityManager no finally
            }
        }
    }

    @Override
    public List<CurrentAccount> getAll() {
        return currentAccountRepository.getAll();
    }
}