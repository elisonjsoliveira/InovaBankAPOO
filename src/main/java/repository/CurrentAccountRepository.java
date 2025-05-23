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
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(conta);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Optional<CurrentAccount> getByAccountNumber(String accountNumber) {
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
        EntityManager em = JPAUtil.getEntityManager();
        List<CurrentAccount> accounts = em.createQuery("SELECT c FROM CurrentAccount c", CurrentAccount.class).getResultList();
        em.close();
        return accounts;
    }

    @Override
    public void update(CurrentAccount account) {
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
