package interfaces;

import entities.PixKey;
import java.util.List;
import java.util.Optional;

public interface IPixKeyRepository {
    void create(PixKey pixKey);
    Optional<PixKey> findByKey(String key);
    List<PixKey> getAll();
    void update(PixKey pixKey);
    void delete(String key);
}
