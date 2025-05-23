package interfaces;

import entities.PixTransaction;
import java.util.List;
import java.util.Optional;

public interface IPixTransactionRepository {
    void create(PixTransaction transaction);
    Optional<PixTransaction> findById(Long id);
    List<PixTransaction> getAll();
    void update(PixTransaction transaction);
    void delete(Long id);
}
