package repository;

import EstruturaDeDadosListaEncadeada.TransactionHistory;
import entities.Account;
import entities.Transaction;
import interfaces.ITransactionRepository;

import jakarta.persistence.EntityManager;
import util.JPAUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransactionRepository implements ITransactionRepository<Transaction> {

    @Override
    public void create(Transaction transaction) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Account originAccount = em.find(Account.class, transaction.getOriginAccount().getId());
            Account destinationAccount = em.find(Account.class, transaction.getDestinationAccount().getId());

            if (originAccount.getBalance() < transaction.getValue()) {
                throw new RuntimeException("Saldo insuficiente na conta de origem.");
            }

            originAccount.setBalance(originAccount.getBalance() - transaction.getValue());
            destinationAccount.setBalance(destinationAccount.getBalance() + transaction.getValue());

            em.merge(originAccount);
            em.merge(destinationAccount);
            em.persist(transaction);

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    @Override
    public Optional<Transaction> getById(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Transaction transaction = em.find(Transaction.class, id);
        em.close();
        return Optional.ofNullable(transaction);
    }

    @Override
    public TransactionHistory getAll() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        em.close();

        TransactionHistory history = new TransactionHistory();

        for (Transaction tx : transactions) {
            // Acessa os campos da transação
            Long id = tx.getId();
            String tipo = tx.getTypeTransaction();
            double valor = tx.getValue();
            LocalDate data = tx.getDate();
            Account origem = tx.getOriginAccount();
            Account destino = tx.getDestinationAccount();

            // Adiciona à lista encadeada
            history.add(id, tipo, valor, data, origem, destino);

            // Imprime os dados da transação
            System.out.println("ID: " + id);
            System.out.println("Tipo: " + tipo);
            System.out.printf("Valor: R$ %.2f\n", valor);
            System.out.println("Data: " + data);
            System.out.println("Conta de Origem: " + origem);
            System.out.println("Conta de Destino: " + destino);
            System.out.println("-------------------------");
        }

        return history;
    }


    @Override
    public void update(Transaction transaction) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        em.merge(transaction);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void delete(long id) {
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        Transaction transaction = em.find(Transaction.class, id);
        if (transaction != null) {
            em.remove(transaction);
        }
        em.getTransaction().commit();
        em.close();
    }
}
