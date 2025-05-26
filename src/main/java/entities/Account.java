package entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account { // CONTINUA SENDO ABSTRATA, CORRETO

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 2) // Adicionado nullable e BigDecimal precision/scale
    private BigDecimal balance;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false) // Adicionado nullable=false para a coluna
    private Client client;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PixKey> pixKeys = new HashSet<>();

    public Account() {
    }

    public Account(String accountNumber, BigDecimal balance, Client client) { // Balance como BigDecimal
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    // Remover setId - IDs gerados automaticamente não devem ser setados.
    // public void setId(Long id) { this.id = id; }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() { // Retorna BigDecimal
        return balance;
    }

    public void setBalance(BigDecimal balance) { // Aceita BigDecimal
        this.balance = balance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Set<PixKey> getPixKeys() {
        return pixKeys;
    }

    public void setPixKeys(Set<PixKey> pixKeys) {
        this.pixKeys = pixKeys;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", client=" + (client != null ? client.getCpf() : null) + // Mostrar CPF do cliente para melhor identificação
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // if (!(o instanceof Account)) return false; // Pode ser getClass() != o.getClass() para herança
        // Para herança JOINED, é comum usar getClass() != o.getClass() se a identidade de subclasses é distinta,
        // mas instanceof Account funciona se o ID e accountNumber são o suficiente para identidade na base.
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        // Comparar pelo ID é o mais recomendado para entidades persistidas.
        // accountNumber também é unique, então pode ser incluído para robustez antes da persistência.
        return Objects.equals(id, account.id); // Recomenda-se apenas o ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Recomenda-se apenas o ID
    }
}