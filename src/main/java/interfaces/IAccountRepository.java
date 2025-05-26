package interfaces;

import entities.Account;
import java.util.List;
import java.util.Optional;

public interface IAccountRepository {
    Optional<Account> getByAccountNumber(String accountNumber);
    List<Account> getAll();
    // NÃO ADICIONAR create/update/delete aqui, pois a entidade é abstrata.
    // Essas operações são para ICurrentAccountRepository e ISavingsAccountRepository.
}