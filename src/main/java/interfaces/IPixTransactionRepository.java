package interfaces;

import entities.PixTransaction;
import java.util.List;
import java.util.Optional;

public interface IPixTransactionRepository {

    void create(PixTransaction pixTransaction);
    Optional<PixTransaction> findById(Long id);
    List<PixTransaction> findAllByAccount(Long accountId);

    // Sugestão: Métodos para gerenciamento (se necessário, transações geralmente são imutáveis)
    // void update(PixTransaction pixTransaction);
    // void delete(Long id);
}