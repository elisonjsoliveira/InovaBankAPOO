package interfaces;

import entities.PixKey;
import java.util.List;
import java.util.Optional;

public interface IPixKeyService {

    void createPixKey(PixKey pixKey);

    Optional<PixKey> findByKeyValue(String keyValue);

    List<PixKey> findAllByAccount(Long accountId);
}
