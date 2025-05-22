package interfaces;

import EstruturaDeDadosListaEncadeada.TransactionHistory;

import java.util.Optional;

public interface ITransactionRepository<T> {

    void create(T entity);
    Optional<T> getById(long id);
    TransactionHistory getAll();
    void update(T entity);
    void delete(long id);
}
