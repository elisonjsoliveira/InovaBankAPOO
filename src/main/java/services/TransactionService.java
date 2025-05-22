package services;

import EstruturaDeDadosListaEncadeada.TransactionHistory;
import entities.Transaction;
import interfaces.ITransactionService;
import repository.TransactionRepository;

import java.util.Optional;

public class TransactionService implements ITransactionService<Transaction> {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void create(Transaction transaction) {
        if (transaction != null) {
            this.transactionRepository.create(transaction);
        } else {
            System.out.println("Transaction can't be null");
        }
    }

    @Override
    public Optional<Transaction> getById(long id) {
        return transactionRepository.getById(id);
    }

    @Override
    public TransactionHistory getAll() {
        return transactionRepository.getAll();
    }

    @Override
    public void update(Transaction transaction) {
        if (transaction != null) {
            this.transactionRepository.update(transaction);
        } else {
            System.out.println("Transaction does not exist");
        }
    }

    @Override
    public void delete(long id) {
        transactionRepository.delete(id);
    }




}
