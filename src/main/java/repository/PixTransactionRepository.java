package repository;

import entities.PixTransaction;
import interfaces.IPixTransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class PixTransactionRepository implements IPixTransactionRepository {

    private final EntityManager entityManager;

    public PixTransactionRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(PixTransaction pixTransaction) {
        entityManager.getTransaction().begin();
        entityManager.persist(pixTransaction);
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<PixTransaction> findById(Long id) {
        PixTransaction pt = entityManager.find(PixTransaction.class, id);
        return Optional.ofNullable(pt);
    }

    @Override
    public List<PixTransaction> findAllByAccount(Long accountId) {
        TypedQuery<PixTransaction> query = entityManager.createQuery(
                "SELECT pt FROM PixTransaction pt WHERE pt.account.id = :accountId", PixTransaction.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }
}
