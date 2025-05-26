package interfaces;

import entities.Account;
import java.util.List;
import java.util.Optional;

public interface IAccountService<T extends Account> { // O <T extends Account> está correto
    Optional<T> getByAccountNumber(String accountNumber);
    List<T> getAll();
    // NÃO ADICIONAR create/update/delete aqui, pois a entidade é abstrata.
}