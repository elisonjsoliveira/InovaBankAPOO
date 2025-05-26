package interfaces;

import entities.PixTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IPixTransactionService {

    // Método para iniciar uma transação Pix completa (será implementado no serviço)
    void performPixTransfer(String originAccountNumber, String pixKeyUsed, String keyTypeUsed, BigDecimal value);

    void create(PixTransaction pixTransaction); // Renomeado de createPixTransaction
    Optional<PixTransaction> findById(Long id);
    List<PixTransaction> findAllByAccount(Long accountId);
}