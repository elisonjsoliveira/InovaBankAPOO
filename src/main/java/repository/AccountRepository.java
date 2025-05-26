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
        try (EntityManager em = JPAUtil.getEntityManager()) { // Uso de try-with-resources é bom
            Account account = em.createQuery(
                            "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber", Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
            return Optional.of(account);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) { // Capturar exceções genéricas também
            System.err.println("Error getting account by number: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Account> getAll() {
        EntityManager em = null; // Declarar fora do try para o finally
        try {
            em = JPAUtil.getEntityManager();
            List<Account> accounts = em.createQuery("SELECT a FROM Account a", Account.class).getResultList();
            return accounts;
        } catch (Exception e) { // Capturar exceções genéricas
            System.err.println("Error getting all accounts: " + e.getMessage());
            return List.of(); // Retornar lista vazia em caso de erro
        } finally {
            if (em != null) {
                em.close(); // Fechar o EntityManager no finally
            }
        }
    }
}