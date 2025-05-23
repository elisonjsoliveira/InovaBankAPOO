package repository;

import entities.PixTransaction;
import interfaces.IPixTransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class PixTransactionRepository implements IPixTransactionRepository {

    @Override
    public void create(PixTransaction transaction) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.persist(transaction);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Optional<PixTransaction> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            PixTransaction tx = em.find(PixTransaction.class, id);
            return Optional.ofNullable(tx);
        } finally {
            em.close();
        }
    }

    @Override
    public List<PixTransaction> getAll() {
        EntityManager em = JPAUtil.getEntityManager();
        List<PixTransaction> list = em.createQuery("SELECT t FROM PixTransaction t", PixTransaction.class).getResultList();
        em.close();
        return list;
    }

    @Override
    public void update(PixTransaction transaction) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(transaction);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            PixTransaction tx = em.find(PixTransaction.class, id);
            if (tx != null) {
                em.remove(tx);
                em.getTransaction().commit();
            } else {
                System.out.println("Pix transaction not found.");
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}
