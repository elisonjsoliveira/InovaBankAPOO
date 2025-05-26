package interfaces;

import entities.Client; // Importar Client
import java.util.List;
import java.util.Optional;

public interface IClientService<Client> {

    void create(Client client);
    Optional<Client> getByCPF(String cpf);
    List<Client> getAll();
    void update(Client client);
    void delete(String cpf);

    // NOVO MÉTODO PARA AUTENTICAÇÃO
    Optional<Client> authenticate(String cpf, String password);
}