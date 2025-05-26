package interfaces;

// Importar java.util.List em vez de TransactionHistory
import entities.Transaction;

import java.util.List;
import java.util.Optional;

public interface ITransactionService { // Removido <Transaction> aqui

    void create(Transaction transaction);
    Optional<Transaction> getById(long id);
    List<Transaction> getAll(); // Alterado de TransactionHistory para List<Transaction>
    void update(Transaction transaction);
    void delete(long id);
}