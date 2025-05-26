package repository;

import entities.Account; // Para manipular saldos das contas
import entities.PixTransaction;
import interfaces.IPixTransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import util.JPAUtil; // Importar JPAUtil

import java.math.BigDecimal; // Para manipular saldos com BigDecimal
import java.util.List;
import java.util.Optional;

public class PixTransactionRepository implements IPixTransactionRepository {

    // Remover o EntityManager injetado no construtor.
    // private final EntityManager entityManager;
    // public PixTransactionRepository(EntityManager entityManager) { this.entityManager = entityManager; }

    public PixTransactionRepository() {} // Construtor padrão

    @Override
    public void create(PixTransaction pixTransaction) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager(); // Obter EM para cada operação
            em.getTransaction().begin();

            // 1. Buscar as contas de origem e destino (gerenciadas pelo EM atual)
            // É crucial que estas contas sejam as entidades persistidas e não "detached"
            Account originAccount = em.find(Account.class, pixTransaction.getOriginAccount().getId());
            // A conta de destino pode não estar no nosso sistema (ex: outro banco),
            // mas se for uma conta interna, precisamos do objeto.
            Account destinationAccount = null;
            if (pixTransaction.getDestinationAccount() != null && pixTransaction.getDestinationAccount().getId() != null) {
                destinationAccount = em.find(Account.class, pixTransaction.getDestinationAccount().getId());
            }

            // 2. Validações (algumas podem estar no serviço, mas a de saldo é crítica aqui)
            if (originAccount == null) {
                throw new IllegalArgumentException("Origin account not found.");
            }
            // Se a transação for para uma conta interna, verificar se ela existe
            if (pixTransaction.getDestinationAccount() != null && destinationAccount == null) {
                throw new IllegalArgumentException("Internal destination account not found.");
            }

            // 3. Lógica de Débito e Crédito de Saldo (CRUCIAL para atomicidade)
            if (originAccount.getBalance().compareTo(pixTransaction.getValue()) < 0) {
                throw new IllegalArgumentException("Insufficient balance in origin account.");
            }

            originAccount.setBalance(originAccount.getBalance().subtract(pixTransaction.getValue())); // Débito
            if (destinationAccount != null) { // Apenas credita se a conta de destino for interna
                destinationAccount.setBalance(destinationAccount.getBalance().add(pixTransaction.getValue())); // Crédito
                em.merge(destinationAccount); // Sincroniza conta de destino
            }
            em.merge(originAccount); // Sincroniza conta de origem

            // 4. Persistir a PixTransaction (Registro da transação)
            em.persist(pixTransaction);

            // 5. Definir status da transação como COMPLETED se tudo ocorreu bem
            pixTransaction.setStatus(PixTransaction.PixTransactionStatus.COMPLETED);

            em.getTransaction().commit(); // Commit da transação (débito, crédito e registro)
        } catch (RuntimeException e) { // Captura IllegalArgumentException e outras RuntimeExceptions
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback em caso de erro
            }
            // Se houver um erro, a transação falhou
            if (pixTransaction != null) {
                pixTransaction.setStatus(PixTransaction.PixTransactionStatus.FAILED);
                // Opcional: tentar fazer um merge para persistir o status FAILED, mas isso criaria uma nova transação ou exigiria um novo EM.
                // Para este nível, o rollback já desfaz a criação do registro, mas o status FAILED seria útil se o registro fosse persistido antes do débito/crédito.
                // Por simplicidade, assumimos que o registro só é persistido se a transação for bem-sucedida.
            }
            System.err.println("PixTransaction creation failed: " + e.getMessage());
            throw e; // Relançar para a camada de serviço
        } catch (Exception e) { // Captura outras exceções inesperadas
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (pixTransaction != null) {
                pixTransaction.setStatus(PixTransaction.PixTransactionStatus.FAILED);
            }
            e.printStackTrace();
            System.err.println("An unexpected error occurred during PixTransaction creation: " + e.getMessage());
            throw new RuntimeException("Failed to create PixTransaction due to an unexpected error.", e);
        } finally {
            if (em != null) {
                em.close(); // Fechar EM
            }
        }
    }

    @Override
    public Optional<PixTransaction> findById(Long id) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            PixTransaction pt = em.find(PixTransaction.class, id);
            return Optional.ofNullable(pt);
        } catch (Exception e) {
            System.err.println("Error finding PixTransaction by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<PixTransaction> findAllByAccount(Long accountId) {
        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            TypedQuery<PixTransaction> query = em.createQuery(
                    "SELECT pt FROM PixTransaction pt WHERE pt.originAccount.id = :accountId", PixTransaction.class); // Alterado para originAccount
            query.setParameter("accountId", accountId);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error finding PixTransactions by account: " + e.getMessage());
            return List.of();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    // Métodos de update e delete não foram adicionados, pois transações são imutáveis.
    // Se realmente precisasse, seguiria o padrão de outros repositórios, com transação e fechamento de EM.
}