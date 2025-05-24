package repository;

import entities.PixKey;
import interfaces.IPixKeyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class PixKeyRepository implements IPixKeyRepository {

    private final EntityManager entityManager;

    public PixKeyRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(PixKey pixKey) {
        entityManager.getTransaction().begin();
        entityManager.persist(pixKey);
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<PixKey> findByKeyValue(String keyValue) {
        TypedQuery<PixKey> query = entityManager.createQuery(
                "SELECT p FROM PixKey p WHERE p.keyValue = :keyValue", PixKey.class);
        query.setParameter("keyValue", keyValue);
        List<PixKey> results = query.getResultList();
        if (results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    @Override
    public List<PixKey> findAllByAccount(Long accountId) {
        TypedQuery<PixKey> query = entityManager.createQuery(
                "SELECT p FROM PixKey p WHERE p.account.id = :accountId", PixKey.class);
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }
}
