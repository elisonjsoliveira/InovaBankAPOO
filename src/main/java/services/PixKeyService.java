package services;

import entities.PixKey;
import interfaces.IPixKeyService;
import repository.PixKeyRepository;

import java.util.List;
import java.util.Optional;

public class PixKeyService implements IPixKeyService {

    private final PixKeyRepository pixKeyRepository;

    public PixKeyService(PixKeyRepository pixKeyRepository) {
        this.pixKeyRepository = pixKeyRepository;
    }

    @Override
    public void createPixKey(PixKey pixKey) {
        pixKeyRepository.create(pixKey);
    }

    @Override
    public Optional<PixKey> findByKeyValue(String keyValue) {
        return pixKeyRepository.findByKeyValue(keyValue);
    }

    @Override
    public List<PixKey> findAllByAccount(Long accountId) {
        return pixKeyRepository.findAllByAccount(accountId);
    }
}
