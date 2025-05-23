package repository;

import entities.Account;
import interfaces.IAccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class AccountRepository implements IAccountRepository {

    @Override
    public Optional<Account> getByAccountNumber(String accountNumber) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Account account = em.createQuery(
                            "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber", Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Account> getAll() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Account> accounts = em.createQuery("SELECT a FROM Account a", Account.class).getResultList();
        em.close();
        return accounts;
    }
}
