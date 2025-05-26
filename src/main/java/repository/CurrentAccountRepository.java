package repository;

import entities.CurrentAccount;
import interfaces.ICurrentAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class CurrentAccountRepository implements ICurrentAccountRepository {

    @Override
    public void create(CurrentAccount conta) {
        // Correção: Garantir que o EntityManager seja fechado no finally
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.persist(conta);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // Para o contexto de "sem erros", apenas imprimir. Em produção, lançar ou logar.
            System.err.println("Error creating CurrentAccount: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Optional<CurrentAccount> getByAccountNumber(String accountNumber) {
        // Já usa try-finally, está correto
        EntityManager em = JPAUtil.getEntityManager();
        try {
            CurrentAccount account = em.createQuery(
                            "SELECT c FROM CurrentAccount c WHERE c.accountNumber = :accountNumber", CurrentAccount.class)
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
    public List<CurrentAccount> getAll() {
        // Correção: Garantir que o EntityManager seja fechado no finally
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            List<CurrentAccount> accounts = em.createQuery("SELECT c FROM CurrentAccount c", CurrentAccount.class).getResultList();
            return accounts;
        } catch (Exception e) {
            System.err.println("Error getting all CurrentAccounts: " + e.getMessage());
            return List.of(); // Retornar lista vazia em caso de erro
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void update(CurrentAccount account) {
        // Correção: Agora este update deve gerenciar sua própria transação e EM.
        // O serviço de transferência faz o merge diretamente.
        // Este update serve para outras operações que atualizem uma CurrentAccount isoladamente.
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
            System.err.println("Error updating CurrentAccount: " + e.getMessage());
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
            CurrentAccount account = em.createQuery(
                            "SELECT c FROM CurrentAccount c WHERE c.accountNumber = :accountNumber", CurrentAccount.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
            em.remove(account);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("Current account not found.");
            em.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}