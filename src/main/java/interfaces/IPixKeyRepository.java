package interfaces;

import entities.PixKey;
import java.util.List;
import java.util.Optional;

public interface IPixKeyRepository {

    void create(PixKey pixKey);

    Optional<PixKey> findByKeyValue(String keyValue);

    List<PixKey> findAllByAccount(Long accountId);

    // Sugestão: Adicionar métodos de update e delete
    void update(PixKey pixKey);
    void delete(String keyValue); // Deletar pela chave que é única
    // ou deleteById(Long id);
}