package repository;

import entities.PixKey;
import interfaces.IPixKeyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException; // Para getSingleResult
import jakarta.persistence.TypedQuery;
import util.JPAUtil; // Importar JPAUtil

import java.util.List;
import java.util.Optional;

public class PixKeyRepository implements IPixKeyRepository {

    // Remover o EntityManager injetado no construtor.
    // private final EntityManager entityManager;
    // public PixKeyRepository(EntityManager entityManager) { this.entityManager = entityManager; }

    // Construtor vazio ou padrão, se não precisar de outras dependências.
    public PixKeyRepository() {}

    @Override
    public void create(PixKey pixKey) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager(); // Obter EM para cada operação
            em.getTransaction().begin();
            em.persist(pixKey);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creating PixKey: " + e.getMessage());
            throw new RuntimeException("Failed to create PixKey", e); // Relançar para o serviço
        } finally {
            if (em != null) {
                em.close(); // Fechar EM
            }
        }
    }

    @Override
    public Optional<PixKey> findByKeyValue(String keyValue) {
        // Usar try-with-resources para garantir o fechamento do EM
        try (EntityManager em = JPAUtil.getEntityManager()) {
            TypedQuery<PixKey> query = em.createQuery( // Usar 'em' obtido
                    "SELECT p FROM PixKey p WHERE p.keyValue = :keyValue", PixKey.class);
            query.setParameter("keyValue", keyValue);
            // Usar getSingleResult para unicidade e tratar NoResultException
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty(); // Chave não encontrada
        } catch (Exception e) {
            System.err.println("Error finding PixKey by value: " + e.getMessage());
            return Optional.empty(); // Outros erros também resultam em Optional.empty()
        }
    }

    @Override
    public List<PixKey> findAllByAccount(Long accountId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager(); // Obter EM
            TypedQuery<PixKey> query = em.createQuery(
                    "SELECT p FROM PixKey p WHERE p.account.id = :accountId", PixKey.class);
            query.setParameter("accountId", accountId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error finding PixKeys by account: " + e.getMessage());
            return List.of(); // Retornar lista vazia em caso de erro
        } finally {
            if (em != null) {
                em.close(); // Fechar EM
            }
        }
    }

    // Implementação dos métodos sugeridos (update e delete)
    @Override
    public void update(PixKey pixKey) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.merge(pixKey); // Merge para atualizar uma entidade
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error updating PixKey: " + e.getMessage());
            throw new RuntimeException("Failed to update PixKey", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void delete(String keyValue) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            PixKey pixKey = em.createQuery("SELECT p FROM PixKey p WHERE p.keyValue = :keyValue", PixKey.class)
                    .setParameter("keyValue", keyValue)
                    .getSingleResult(); // Buscar a chave para deletar
            em.remove(pixKey);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("PixKey with value " + keyValue + " not found for deletion.");
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error deleting PixKey (value: " + keyValue + "): " + e.getMessage());
            throw new RuntimeException("Failed to delete PixKey", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}