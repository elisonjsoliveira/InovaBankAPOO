package repository;

import entities.SavingsAccount;
import interfaces.ISavingsAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class SavingsAccountRepository implements ISavingsAccountRepository {

    @Override
    public void create(SavingsAccount account) {
        // Correção: Garantir que o EntityManager seja fechado no finally
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.persist(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creating SavingsAccount: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Optional<SavingsAccount> getByAccountNumber(String accountNumber) {
        // Já usa try-finally, está correto
        EntityManager em = JPAUtil.getEntityManager();
        try {
            SavingsAccount account = em.createQuery(
                            "SELECT c FROM SavingsAccount c WHERE c.accountNumber = :accountNumber", SavingsAccount.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<SavingsAccount> getAll() {
        // Correção: Garantir que o EntityManager seja fechado no finally
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            List<SavingsAccount> accounts = em.createQuery("SELECT c FROM SavingsAccount c", SavingsAccount.class).getResultList();
            return accounts;
        } catch (Exception e) {
            System.err.println("Error getting all SavingsAccounts: " + e.getMessage());
            return List.of(); // Retornar lista vazia em caso de erro
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void update(SavingsAccount account) {
        // Correção: Agora este update deve gerenciar sua própria transação e EM.
        // O serviço de transferência faz o merge diretamente.
        // Este update serve para outras operações que atualizem uma SavingsAccount isoladamente.
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.merge(account);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error updating SavingsAccount: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void delete(String accountNumber) {
        // Já usa try-catch-finally, está correto
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            SavingsAccount account = em.createQuery(
                            "SELECT c FROM SavingsAccount c WHERE c.accountNumber = :accountNumber", SavingsAccount.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
            em.remove(account);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("Savings account not found.");
            em.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}