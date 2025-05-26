package interfaces;


import java.util.List;
import java.util.Optional;

public interface IClientRepository<T> {
    void create(T entity);
    Optional<T> getByCPF(String cpf);
    List<T> getAll();
    void update(T entity);
    void delete(String cpf);
}