package interfaces;

// Importar java.util.List em vez de TransactionHistory
import entities.Transaction;

import java.util.List;
import java.util.Optional;

public interface ITransactionRepository { // Removido <T>

    void create(Transaction entity); // A entidade Transaction é concreta
    Optional<Transaction> getById(long id);
    List<Transaction> getAll(); // Alterado de TransactionHistory para List<Transaction>
    void update(Transaction entity);
    void delete(long id);
}