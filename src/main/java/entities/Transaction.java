package entities;

import jakarta.persistence.*;
import java.math.BigDecimal; // Importado BigDecimal
import java.time.LocalDate;
import java.util.Objects; // Importado Objects para equals/hashCode

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String typeTransaction;

    @Column(nullable = false, precision = 19, scale = 2) // Usar BigDecimal para valores monetários
    private BigDecimal value;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "origin_account_id", nullable = false)
    private Account originAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount;

    public Transaction() {
        // Construtor vazio exigido pelo JPA
    }

    // O construtor NÃO deve conter lógica de negócio de saldo.
    // Essa lógica será movida para o TransactionService.
    public Transaction(String typeTransaction, BigDecimal value, LocalDate date, Account originAccount, Account destinationAccount) {
        this.typeTransaction = typeTransaction;
        this.value = value;
        this.date = date;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
    }

    public Long getId() {
        return id;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public BigDecimal getValue() { // Alterado para BigDecimal
        return value;
    }

    public void setValue(BigDecimal value) { // Alterado para BigDecimal
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Account getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Account originAccount) {
        this.originAccount = originAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + getId() +
                ", typeTransaction='" + getTypeTransaction() + '\'' +
                ", value=" + getValue() +
                ", date=" + getDate() +
                ", originAccount=" + (originAccount != null ? originAccount.getAccountNumber() : "null") + // Mostrar apenas o número da conta
                ", destinationAccount=" + (destinationAccount != null ? destinationAccount.getAccountNumber() : "null") + // Mostrar apenas o número da conta
                '}';
    }
}