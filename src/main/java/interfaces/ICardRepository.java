package interfaces;

import entities.Card;
import java.util.List;
import java.util.Optional;

public interface ICardRepository<T> {
    void create(T entity);
    Optional<T> getByCardNumber(String cardNumber); // Alterado de long para String
    List<T> getAll();
    void update(T entity);
    void delete(String cardNumber); // Alterado de long para String
}