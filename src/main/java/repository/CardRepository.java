package repository;

import entities.Card;
import interfaces.ICardRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class CardRepository implements ICardRepository<Card> {

    @Override
    public void create(Card card) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.persist(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creating card: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Card> getByCardNumber(String cardNumber) { // Alterado de long para String
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Card card = em.createQuery("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber", Card.class)
                    .setParameter("cardNumber", cardNumber)
                    .getSingleResult();
            return Optional.of(card);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error getting card by number: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Card> getAll() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            List<Card> cards = em.createQuery("SELECT c FROM Card c", Card.class).getResultList();
            return cards;
        } catch (Exception e) {
            System.err.println("Error getting all cards: " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void update(Card card) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.merge(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error updating card: " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void delete(String cardNumber) { // Alterado de long para String
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            Card card = em.createQuery("SELECT c FROM Card c WHERE c.cardNumber = :cardNumber", Card.class)
                    .setParameter("cardNumber", cardNumber)
                    .getSingleResult();
            em.remove(card);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("Card not found for deletion (number: " + cardNumber + ").");
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            System.err.println("Error deleting card (number: " + cardNumber + "): " + e.getMessage());
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}