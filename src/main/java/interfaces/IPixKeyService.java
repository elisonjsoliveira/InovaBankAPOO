package interfaces;

import entities.PixKey;
import java.util.List;
import java.util.Optional;

public interface IPixKeyService {

    void create(PixKey pixKey); // Renomeado de createPixKey
    Optional<PixKey> findByKeyValue(String keyValue);
    List<PixKey> findAllByAccount(Long accountId);

    // Sugestão: Adicionar métodos de update e delete
    void update(PixKey pixKey);
    void delete(String keyValue);
}