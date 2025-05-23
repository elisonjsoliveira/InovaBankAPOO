package interfaces;

import entities.SavingsAccount;
import java.util.List;
import java.util.Optional;

public interface ISavingsAccountRepository {
    void create(SavingsAccount conta);
    Optional<SavingsAccount> getByAccountNumber(String accountNumber);
    List<SavingsAccount> getAll();
    void update(SavingsAccount conta);
    void delete(String accountNumber);
}
