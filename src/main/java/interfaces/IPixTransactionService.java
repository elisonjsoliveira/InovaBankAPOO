package interfaces;

import entities.PixTransaction;
import java.util.List;
import java.util.Optional;

public interface IPixTransactionService {

    void createPixTransaction(PixTransaction pixTransaction);

    Optional<PixTransaction> findById(Long id);

    List<PixTransaction> findAllByAccount(Long accountId);
}
