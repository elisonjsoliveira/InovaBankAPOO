package repository;

import entities.Client;
import interfaces.IClientRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery; // Importar TypedQuery
import util.JPAUtil;

import java.util.List;
import java.util.Optional;

public class ClientRepository implements IClientRepository<Client> {

    // Método para autenticação, específico do repositório
    public Optional<Client> authenticate(String cpf, String password) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<Client> query = em.createQuery(
                    "SELECT c FROM Client c WHERE c.cpf = :cpf AND c.password = :password", Client.class);
            query.setParameter("cpf", cpf);
            query.setParameter("password", password); // Senha em texto puro, cuidado!
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty(); // Cliente não encontrado ou senha incorreta
        } catch (Exception e) {
            System.err.println("Error during client authentication: " + e.getMessage());
            return Optional.empty();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    @Override
    public void create(Client client) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.persist(client); // O JPA persistirá o campo 'password' automaticamente
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error creating client: " + e.getMessage());
            throw new RuntimeException("Failed to create client", e); // Relançar para a camada de serviço
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public Optional<Client> getByCPF(String cpf) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Client client = em.createQuery("SELECT c FROM Client c WHERE c.cpf = :cpf", Client.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
            return Optional.of(client);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error getting client by CPF: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Client> getAll() {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            List<Client> clients = em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
            return clients;
        } catch (Exception e) {
            System.err.println("Error getting all clients: " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void update(Client client) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            em.merge(client); // O JPA persistirá o campo 'password' se alterado
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error updating client: " + e.getMessage());
            throw new RuntimeException("Failed to update client", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void delete(String cpf) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            em.getTransaction().begin();
            Client client = em.createQuery("SELECT c FROM Client c WHERE c.cpf = :cpf", Client.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
            em.remove(client);
            em.getTransaction().commit();
        } catch (NoResultException e) {
            System.out.println("Client not found for deletion (CPF: " + cpf + ").");
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error deleting client (CPF: " + cpf + "): " + e.getMessage());
            throw new RuntimeException("Failed to delete client", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}