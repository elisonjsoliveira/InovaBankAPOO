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
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Optional<SavingsAccount> getByAccountNumber(String accountNumber) {
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
        EntityManager em = JPAUtil.getEntityManager();
        List<SavingsAccount> accounts = em.createQuery("SELECT c FROM SavingsAccount c", SavingsAccount.class).getResultList();
        em.close();
        return accounts;
    }

    @Override
    public void update(SavingsAccount account) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(account);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(String accountNumber) {
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
