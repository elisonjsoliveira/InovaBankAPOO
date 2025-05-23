package repository;

import entities.PixKey;
import interfaces.IPixKeyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class PixKeyRepository implements IPixKeyRepository {

    @Override
    public void create(PixKey pixKey) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(pixKey);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Optional<PixKey> findByKey(String key) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            PixKey result = em.createQuery("SELECT p FROM PixKey p WHERE p.keyValue = :key", PixKey.class)
                    .setParameter("key", key)
                    .getSingleResult();
            return Optional.of(result);
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<PixKey> getAll() {
        EntityManager em = JPAUtil.getEntityManager();
        List<PixKey> keys = em.createQuery("SELECT p FROM PixKey p", PixKey.class).getResultList();
        em.close();
        return keys;
    }

    @Override
    public void update(PixKey pixKey) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(pixKey);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(String key) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PixKey result = em.createQuery("SELECT p FROM PixKey p WHERE p.keyValue = :key", PixKey.class)
                    .setParameter("key", key)
                    .getSingleResult();
            em.remove(result);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("Pix key not found.");
            em.getTransaction().rollback();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}
