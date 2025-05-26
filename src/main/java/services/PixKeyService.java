package services;

import entities.PixKey;
import entities.Account; // Para validação da conta
import interfaces.IPixKeyService;
import repository.PixKeyRepository;
import services.AccountService; // Para buscar a conta e validar

import java.util.List;
import java.util.Optional;

public class PixKeyService implements IPixKeyService {

    private final PixKeyRepository pixKeyRepository;
    private final AccountService accountService; // Injetar AccountService para validações

    public PixKeyService(PixKeyRepository pixKeyRepository, AccountService accountService) {
        this.pixKeyRepository = pixKeyRepository;
        this.accountService = accountService;
    }

    @Override
    public void create(PixKey pixKey) { // Renomeado de createPixKey
        if (pixKey == null) {
            System.out.println("PixKey cannot be null.");
            return;
        }
        if (pixKey.getKeyValue() == null || pixKey.getKeyValue().trim().isEmpty()) {
            System.out.println("PixKey value cannot be null or empty.");
            return;
        }
        if (pixKey.getKeyType() == null) { // Validação para o Enum
            System.out.println("PixKey type cannot be null.");
            return;
        }
        if (pixKey.getAccount() == null || pixKey.getAccount().getId() == null) {
            System.out.println("PixKey must be associated with an existing account.");
            return;
        }

        // 1. Verificar se a conta associada realmente existe
        Optional<Account> associatedAccount = accountService.getByAccountNumber(pixKey.getAccount().getAccountNumber());
        if (associatedAccount.isEmpty()) {
            System.out.println("Associated account not found for PixKey creation.");
            return;
        }
        // Assegurar que a PixKey tem a entidade Account gerenciada (importante para JPA)
        pixKey.setAccount(associatedAccount.get());


        // 2. Verificar se a chave Pix já existe
        if (pixKeyRepository.findByKeyValue(pixKey.getKeyValue()).isPresent()) {
            System.out.println("PixKey '" + pixKey.getKeyValue() + "' already exists.");
            return;
        }

        // 3. Validação de formato da chave (exemplo simplificado)
        if (!isValidPixKeyFormat(pixKey.getKeyValue(), pixKey.getKeyType())) {
            System.out.println("Invalid format for PixKey type " + pixKey.getKeyType() + " and value " + pixKey.getKeyValue());
            return;
        }

        try {
            pixKeyRepository.create(pixKey);
            System.out.println("PixKey '" + pixKey.getKeyValue() + "' created successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to create PixKey: " + e.getMessage());
        }
    }

    @Override
    public Optional<PixKey> findByKeyValue(String keyValue) {
        if (keyValue == null || keyValue.trim().isEmpty()) {
            System.out.println("PixKey value cannot be null or empty for search.");
            return Optional.empty();
        }
        return pixKeyRepository.findByKeyValue(keyValue);
    }

    @Override
    public List<PixKey> findAllByAccount(Long accountId) {
        if (accountId == null || accountId <= 0) {
            System.out.println("Invalid account ID for PixKey search.");
            return List.of();
        }
        return pixKeyRepository.findAllByAccount(accountId);
    }

    // Implementação dos métodos sugeridos (update e delete)
    @Override
    public void update(PixKey pixKey) {
        if (pixKey == null || pixKey.getId() == null) {
            System.out.println("PixKey to update cannot be null or have a null ID.");
            return;
        }
        // Opcional: verificar se a PixKey existe antes de atualizar
        Optional<PixKey> existingPixKey = pixKeyRepository.findByKeyValue(pixKey.getKeyValue()); // Ou getById
        if (existingPixKey.isEmpty()) {
            System.out.println("PixKey " + pixKey.getKeyValue() + " not found for update.");
            return;
        }

        // Adicionalmente, validar a nova chave se o valor ou tipo for alterado
        if (!isValidPixKeyFormat(pixKey.getKeyValue(), pixKey.getKeyType())) {
            System.out.println("Invalid format for updated PixKey.");
            return;
        }

        try {
            pixKeyRepository.update(pixKey);
            System.out.println("PixKey " + pixKey.getKeyValue() + " updated successfully.");
        } catch (RuntimeException e) {
            System.out.println("Failed to update PixKey: " + e.getMessage());
        }
    }

    @Override
    public void delete(String keyValue) {
        if (keyValue == null || keyValue.trim().isEmpty()) {
            System.out.println("PixKey value cannot be null or empty for deletion.");
            return;
        }
        try {
            pixKeyRepository.delete(keyValue);
            System.out.println("Attempted to delete PixKey with value: " + keyValue + ".");
        } catch (RuntimeException e) {
            System.out.println("Failed to delete PixKey: " + e.getMessage());
        }
    }

    // Método de validação de formato (exemplo simplificado)
    private boolean isValidPixKeyFormat(String keyValue, PixKey.PixKeyType keyType) {
        if (keyType == null) return false;

        switch (keyType) {
            case CPF:
                // Exemplo: CPF deve ter 11 dígitos e ser válido.
                // Implementação real seria mais complexa com validação de CPF.
                return keyValue.matches("\\d{11}");
            case EMAIL:
                // Exemplo: Formato de e-mail básico.
                return keyValue.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
            case PHONE:
                // Exemplo: Telefone deve ter 11 dígitos (incluindo DDD).
                return keyValue.matches("\\d{11}");
            case RANDOM:
                // Exemplo: Chave aleatória (UUID) geralmente tem 32 caracteres hexadecimais + hífens.
                // Aqui apenas verifica se não está vazia.
                return !keyValue.trim().isEmpty();
            default:
                return false;
        }
    }
}