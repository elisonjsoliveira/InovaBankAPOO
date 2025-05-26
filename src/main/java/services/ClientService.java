package services;

import entities.Client;
import interfaces.IClientService;
import repository.ClientRepository;

import java.util.List;
import java.util.Optional;

public class ClientService implements IClientService<Client> {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // NOVO MÉTODO PARA AUTENTICAÇÃO
    @Override
    public Optional<Client> authenticate(String cpf, String password) {
        if (cpf == null || cpf.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("CPF and password cannot be null or empty for authentication.");
            return Optional.empty();
        }
        // Em um sistema real, aqui você faria a comparação do hash da senha
        return clientRepository.authenticate(cpf, password);
    }

    @Override
    public void create(Client client) {
        if (client == null) {
            System.out.println("Client can't be null.");
            return;
        }
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            System.out.println("Password cannot be null or empty for new client.");
            return;
        }
        if (clientRepository.getByCPF(client.getCpf()).isPresent()) {
            System.out.println("Client with CPF " + client.getCpf() + " already exists.");
            return;
        }
        this.clientRepository.create(client);
        System.out.println("Client created successfully: " + client.getName());
    }

    @Override
    public Optional<Client> getByCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            System.out.println("CPF cannot be null or empty.");
            return Optional.empty();
        }
        return clientRepository.getByCPF(cpf);
    }

    @Override
    public List<Client> getAll() {
        return clientRepository.getAll();
    }

    @Override
    public void update(Client client) {
        if (client == null) {
            System.out.println("Client to update can't be null.");
            return;
        }
        Optional<Client> existingClient = clientRepository.getByCPF(client.getCpf());
        if (existingClient.isEmpty()) {
            System.out.println("Client with CPF " + client.getCpf() + " not found for update.");
            return;
        }
        // Validações adicionais para campos específicos se necessário (ex: password não nula)
        if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
            System.out.println("Password cannot be empty during client update.");
            return;
        }

        this.clientRepository.update(client);
        System.out.println("Client updated successfully: " + client.getName());
    }

    @Override
    public void delete(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            System.out.println("CPF cannot be null or empty for deletion.");
            return;
        }
        clientRepository.delete(cpf);
        System.out.println("Attempted to delete client with CPF: " + cpf);
    }
}