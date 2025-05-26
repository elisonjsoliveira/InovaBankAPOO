package interfaces;

import entities.CurrentAccount;
import java.util.List;
import java.util.Optional;

public interface ICurrentAccountRepository {
    void create(CurrentAccount account);
    Optional<CurrentAccount> getByAccountNumber(String accountNumber);
    List<CurrentAccount> getAll();
    void update(CurrentAccount account);
    void delete(String accountNumber);
}
