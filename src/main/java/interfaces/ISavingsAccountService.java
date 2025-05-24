package interfaces;

import entities.SavingsAccount;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface ISavingsAccountService<T extends SavingsAccount> {
    void create(T account);
    Optional<T> getByAccountNumber(String accountNumber);
    void update(T account);
    void delete(String accountNumber);

    void applyInterest(String accountNumber);
    void transferToCurrent(String savingsAccountNumber, String currentAccountNumber, BigDecimal amount);

    List<SavingsAccount> getAll();
}
