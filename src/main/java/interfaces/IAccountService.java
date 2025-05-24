package interfaces;

import entities.Account;
import java.util.List;
import java.util.Optional;

public interface IAccountService<T extends Account> {
    Optional<T> getByAccountNumber(String accountNumber);
    List<T> getAll();
}
