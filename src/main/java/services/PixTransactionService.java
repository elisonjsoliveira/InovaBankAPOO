package services;

import entities.PixTransaction;
import interfaces.IPixTransactionService;
import repository.PixTransactionRepository;

import java.util.List;
import java.util.Optional;

public class PixTransactionService implements IPixTransactionService {

    private final PixTransactionRepository pixTransactionRepository;

    public PixTransactionService(PixTransactionRepository pixTransactionRepository) {
        this.pixTransactionRepository = pixTransactionRepository;
    }

    @Override
    public void createPixTransaction(PixTransaction pixTransaction) {
        pixTransactionRepository.create(pixTransaction);
    }

    @Override
    public Optional<PixTransaction> findById(Long id) {
        return pixTransactionRepository.findById(id);
    }

    @Override
    public List<PixTransaction> findAllByAccount(Long accountId) {
        return pixTransactionRepository.findAllByAccount(accountId);
    }
}
