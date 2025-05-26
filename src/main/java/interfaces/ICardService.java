package interfaces;

import java.util.List;
import java.util.Optional;

public interface ICardService<Card> {

    void create(Card card);
    Optional<Card> getByCardNumber(String cardNumber); // Alterado de long para String
    List<Card> getAll();
    void update(Card card);
    void delete(String cardNumber); // Alterado de long para String

}