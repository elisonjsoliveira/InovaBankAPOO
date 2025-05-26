package repository;

import entities.Account;
import entities.Transaction;
import interfaces.ITransactionRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException; // Adicionado para getSingleResult
import util.JPAUtil;

import java.math.BigDecimal; // Importado BigDecimal
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Removida importação de EstruturaDeDadosListaEncadeada.TransactionHistory

public class TransactionRepository implements ITransactionRepository { // Removido <Transaction> aqui

    @Override
    public void create(Transaction transaction) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();

            // Buscar contas gerenciadas pelo EntityManager atual
            // Usar merge para garantir que as contas estejam no contexto de persistência
            Account originAccount = em.find(Account.class, transaction.getOriginAccount().getId());
            Account destinationAccount = em.find(Account.class, transaction.getDestinationAccount().getId());

            // Validações de nulidade para as contas buscadas (se não existirem, find retorna null)
            if (originAccount == null) {
                throw new IllegalArgumentException("Origin account not found.");
            }
            if (destinationAccount == null) {
                throw new IllegalArgumentException("Destination account not found.");
            }

            // A validação de saldo e a alteração de saldo são feitas aqui para garantir atomicidade
            // dentro da mesma transação com o persist da Transaction
            if (originAccount.getBalance().compareTo(transaction.getValue()) < 0) { // Usar compareTo para BigDecimal
                throw new IllegalArgumentException("Insufficient balance in origin account.");
            }

            originAccount.setBalance(originAccount.getBalance().subtract(transaction.getValue())); // Usar subtract
            destinationAccount.setBalance(destinationAccount.getBalance().add(transaction.getValue())); // Usar add

            em.merge(originAccount);      // Sincroniza as mudanças de saldo no EM
            em.merge(destinationAccount); // Sincroniza as mudanças de saldo no EM
            em.persist(transaction);      // Persiste a transação

            em.getTransaction().commit();
        } catch (RuntimeException e) { // Captura RuntimeException e IllegalArgumentException
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Transaction creation failed: " + e.getMessage());
            throw e; // Relançar para a camada de serviço
        } catch (Exception e) { // Captura outras exceções inesperadas
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Para depuração, substituir por logging
            System.err.println("An unexpected error occurred during transaction creation: " + e.getMessage());
            throw new RuntimeException("Failed to create transaction due to an unexpected error.", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Transaction> getById(long id) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            Transaction transaction = em.find(Transaction.class, id);
            return Optional.ofNullable(transaction);
        } catch (Exception e) {
            System.err.println("Error getting transaction by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Transaction> getAll() { // Alterado de TransactionHistory para List<Transaction>
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
            return transactions; // Retorna a lista diretamente
        } catch (Exception e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
            return List.of(); // Retorna lista vazia em caso de erro
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void update(Transaction transaction) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.merge(transaction);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error updating transaction: " + e.getMessage());
            throw new RuntimeException("Failed to update transaction", e); // Relançar exceção
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void delete(long id) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            Transaction transaction = em.find(Transaction.class, id);
            if (transaction != null) {
                em.remove(transaction);
            } else {
                System.out.println("Transaction with ID " + id + " not found for deletion.");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error deleting transaction (ID: " + id + "): " + e.getMessage());
            throw new RuntimeException("Failed to delete transaction", e); // Relançar exceção
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}