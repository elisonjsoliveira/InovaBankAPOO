package entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100) // Nome não pode ser nulo e tem um limite de tamanho
    private String name;

    @Column(nullable = false, unique = true, length = 14) // CPF não pode ser nulo e deve ser único. Tamanho para "XXX.XXX.XXX-XX" ou apenas dígitos.
    private String cpf;

    @Column(length = 20) // Telefone pode ser nulo, mas tem limite de tamanho
    private String phone;

    @Column(unique = true, length = 100) // Email deve ser único, mas pode ser nulo. Limite de tamanho.
    private String email;

    @Column(name = "birth_date", nullable = false) // Data de nascimento não pode ser nula
    private LocalDate birthDate;

    // NOVO CAMPO: Senha do cliente
    // ATENÇÃO: Em um sistema real, a senha DEVE ser armazenada como um hash (ex: BCrypt),
    // e nunca em texto puro. Para este projeto, vamos armazenar texto puro para simplicidade.
    @Column(nullable = false, length = 255) // Senha não pode ser nula, um bom tamanho para um hash
    private String password;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;

    public Client() {
        // Construtor vazio exigido pelo JPA
    }

    // Construtor original - ainda útil para casos onde a senha não é inicialmente conhecida
    public Client(String name, String cpf, String phone, String email, LocalDate birthDate) {
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.email = email;
        this.birthDate = birthDate;
        // Senha não é definida aqui, pode exigir setPassword() depois ou usar o construtor completo
        this.password = null; // Definir como null para evitar erro de campo não inicializado
    }

    // Construtor COMPLETO incluindo a senha
    public Client(LocalDate birthDate, String cpf, String email, String name, String password, String phone) {
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.email= email;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // NOVO GETTER E SETTER PARA A SENHA
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id) && Objects.equals(cpf, client.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf);
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", cpf='" + getCpf() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", birthDate=" + getBirthDate() +
                // Não incluir a senha no toString por segurança!
                '}';
    }
}