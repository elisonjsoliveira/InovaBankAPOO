package services;

import entities.Account;
import entities.PixKey;
import entities.PixTransaction;
import interfaces.IPixTransactionService;
import repository.PixTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PixTransactionService implements IPixTransactionService {

    private final PixTransactionRepository pixTransactionRepository;
    private final AccountService accountService; // Para buscar contas
    private final PixKeyService pixKeyService;   // Para buscar chaves Pix (se o recebedor for interno)

    public PixTransactionService(PixTransactionRepository pixTransactionRepository,
                                 AccountService accountService,
                                 PixKeyService pixKeyService) {
        this.pixTransactionRepository = pixTransactionRepository;
        this.accountService = accountService;
        this.pixKeyService = pixKeyService;
    }

    // NOVO MÉTODO: Orquestra a lógica completa de uma transferência Pix
    @Override
    public void performPixTransfer(String originAccountNumber, String pixKeyUsedValue, String keyTypeUsedString, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid transfer amount.");
            return;
        }

        // 1. Validar e obter o tipo da chave Pix
        PixKey.PixKeyType keyTypeUsed;
        try {
            keyTypeUsed = PixKey.PixKeyType.valueOf(keyTypeUsedString.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Pix Key Type provided: " + keyTypeUsedString);
            return;
        }

        // 2. Buscar a conta de origem
        Optional<Account> originAccountOpt = accountService.getByAccountNumber(originAccountNumber);
        if (originAccountOpt.isEmpty()) {
            System.out.println("Origin account not found for Pix transfer.");
            return;
        }
        Account originAccount = originAccountOpt.get();

        // 3. Validar saldo na conta de origem (a validação final ocorre no repositório na transação)
        if (originAccount.getBalance().compareTo(value) < 0) {
            System.out.println("Insufficient balance in origin account.");
            return;
        }

        // 4. Buscar a chave Pix de destino (se for uma chave interna ao sistema)
        Optional<PixKey> destinationPixKeyOpt = pixKeyService.findByKeyValue(pixKeyUsedValue);
        Account destinationAccount = null;

        if (destinationPixKeyOpt.isPresent()) {
            // A chave Pix existe no nosso sistema, então a conta de destino é interna
            destinationAccount = destinationPixKeyOpt.get().getAccount();
            if (destinationAccount == null) {
                System.out.println("Destination PixKey is linked to a non-existent account. Transfer cancelled.");
                return;
            }
            // Opcional: Verificar se a PixKey.keyTypeUsed coincide com o informado
            if (!destinationPixKeyOpt.get().getKeyType().equals(keyTypeUsed)) {
                System.out.println("Provided key type does not match found PixKey. Transfer cancelled.");
                return;
            }
        } else {
            // A chave Pix não foi encontrada internamente, assumir que é uma chave externa.
            // Neste caso, destinationAccount permanece null. Apenas registramos a chave usada.
            System.out.println("Destination PixKey not found internally. Assuming external transfer.");
            // Em um sistema real, aqui você integraria com um gateway Pix externo.
        }

        // 5. Criar o objeto PixTransaction
        PixTransaction pixTransaction = new PixTransaction(
                pixKeyUsedValue,
                keyTypeUsed,
                value,
                LocalDateTime.now(), // Data e hora atual da transação
                originAccount,
                destinationAccount, // Pode ser null se for externo
                PixTransaction.PixTransactionStatus.PENDING // Status inicial
        );

        // 6. Persistir a transação (o repositório fará o débito/crédito e commit/rollback)
        try {
            pixTransactionRepository.create(pixTransaction); // Esta chamada agora debita/credita e persiste
            System.out.println("Pix transfer initiated successfully: " + value + " to " + pixKeyUsedValue);
        } catch (RuntimeException e) {
            System.out.println("Pix transfer failed: " + e.getMessage());
        }
    }

    @Override
    public void create(PixTransaction pixTransaction) { // Renomeado de createPixTransaction
        // Este método serve para persistir o objeto PixTransaction APENAS.
        // A lógica de saldo DEVE ser feita no performPixTransfer ou em um método transacional.
        // Se este método for usado diretamente, ele não debitará saldos.
        if (pixTransaction == null) {
            System.out.println("PixTransaction cannot be null.");
            return;
        }
        try {
            pixTransactionRepository.create(pixTransaction);
            // NOTA: Se este método for chamado diretamente, ele não ajustará os saldos.
            // A lógica de débito/crédito está em pixTransactionRepository.create,
            // que é chamada por performPixTransfer.
        } catch (RuntimeException e) {
            System.out.println("Failed to create PixTransaction record: " + e.getMessage());
        }
    }


    @Override
    public Optional<PixTransaction> findById(Long id) {
        if (id == null || id <= 0) {
            System.out.println("Invalid PixTransaction ID.");
            return Optional.empty();
        }
        return pixTransactionRepository.findById(id);
    }

    @Override
    public List<PixTransaction> findAllByAccount(Long accountId) {
        if (accountId == null || accountId <= 0) {
            System.out.println("Invalid account ID for PixTransaction search.");
            return List.of();
        }
        return pixTransactionRepository.findAllByAccount(accountId);
    }
}