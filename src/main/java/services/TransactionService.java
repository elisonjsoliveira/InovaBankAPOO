package services;

// Removida importação de EstruturaDeDadosListaEncadeada.TransactionHistory
import entities.Transaction;
import interfaces.ITransactionService;
import repository.TransactionRepository;

import java.util.List; // Adicionada importação de List
import java.util.Optional;

public class TransactionService implements ITransactionService { // Removido <Transaction> aqui

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void create(Transaction transaction) {
        if (transaction == null) {
            System.out.println("Transaction can't be null.");
            // Em produção: throw new IllegalArgumentException("Transaction cannot be null.");
            return;
        }
        // A lógica de saldo e validação já foi movida para o TransactionRepository.create()
        // que garante a atomicidade via transação JPA.
        // Aqui, apenas delegamos. Qualquer erro será propagado do repositório.
        try {
            this.transactionRepository.create(transaction);
            System.out.println("Transaction created successfully.");
        } catch (RuntimeException e) { // Captura exceções relançadas do repositório
            System.out.println("Failed to create transaction: " + e.getMessage());
            // Em produção, você relançaria uma exceção de domínio aqui.
        }
    }

    @Override
    public Optional<Transaction> getById(long id) {
        if (id <= 0) {
            System.out.println("Invalid transaction ID.");
            return Optional.empty();
        }
        return transactionRepository.getById(id);
    }

    @Override
    public List<Transaction> getAll() { // Alterado de TransactionHistory para List<Transaction>
        List<Transaction> transactions = transactionRepository.getAll();
        // Se TransactionHistory é uma estrutura de dados customizada para relatórios,
        // você poderia instanciá-la e preenchê-la aqui, antes de retornar.
        // Por enquanto, apenas retorna a lista.
        return transactions;
    }

    @Override
    public void update(Transaction transaction) {
        if (transaction == null) {
            System.out.println("Transaction to update can't be null.");
            // Em produção: throw new IllegalArgumentException("Transaction cannot be null.");
            return;
        }
        // Opcional: verificar se a transação existe antes de atualizar
        // Optional<Transaction> existingTransaction = transactionRepository.getById(transaction.getId());
        // if (existingTransaction.isEmpty()) {
        //     System.out.println("Transaction not found for update.");
        //     return;
        // }
        try {
            this.transactionRepository.update(transaction);
            System.out.println("Transaction updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to update transaction: " + e.getMessage());
        }
    }

    @Override
    public void delete(long id) {
        if (id <= 0) {
            System.out.println("Invalid transaction ID for deletion.");
            return;
        }
        try {
            transactionRepository.delete(id);
            System.out.println("Attempted to delete transaction with ID: " + id + ".");
        } catch (RuntimeException e) {
            System.out.println("Failed to delete transaction: " + e.getMessage());
        }
    }
}