package interfaces;

import entities.CurrentAccount;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface ICurrentAccountService<T extends CurrentAccount> {
    void create(T account);
    Optional<T> getByAccountNumber(String accountNumber);
    void update(T account);
    void delete(String accountNumber);

    void transferToSavings(String currentAccountNumber, String savingsAccountNumber, BigDecimal amount);

    List<CurrentAccount> getAll();
}
