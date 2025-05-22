package interfaces;

import EstruturaDeDadosListaEncadeada.TransactionHistory;

import java.util.Optional;

public interface ITransactionService<Transaction> {

    void create(Transaction transaction);
    Optional<Transaction> getById(long id);
    TransactionHistory getAll();
    void update(Transaction transaction);
    void delete(long id);
}
