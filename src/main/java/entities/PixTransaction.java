package entities;

import jakarta.persistence.*;
import java.math.BigDecimal; // Para o valor da transação
import java.time.LocalDateTime; // Para data e hora da transação (mais preciso que LocalDate)
import java.util.Objects;

@Entity
@Table(name = "pix_transactions") // Nome da tabela
public class PixTransaction {

    // Reutilizando o Enum de PixKey para consistência
    // public enum PixKeyType { CPF, EMAIL, PHONE, RANDOM }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dados da chave Pix usada na transação (do recebedor)
    @Column(nullable = false)
    private String pixKeyUsed;

    @Enumerated(EnumType.STRING) // Mapear o Enum como String no DB
    @Column(nullable = false)
    private PixKey.PixKeyType keyTypeUsed; // Reutilizando o PixKeyType

    // Valor da transação (obrigatório e com precisão financeira)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    // Data e hora da transação
    @Column(nullable = false)
    private LocalDateTime dateTime; // Usar LocalDateTime para mais precisão

    // Referência à conta que originou o pagamento Pix
    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_account_id", nullable = false)
    private Account originAccount;

    // Opcional: Referência à conta de destino (se for uma conta interna ao sistema)
    // Se a PixKey do recebedor for de uma conta do SEU sistema, você pode linkar.
    // Se for para um banco externo, essa referência seria nula.
    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount; // Pode ser nulo se for para outro banco

    // Status da transação (PENDING, COMPLETED, FAILED, etc.)
    public enum PixTransactionStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PixTransactionStatus status;


    public PixTransaction() {}

    // Construtor completo para registro de uma transação Pix
    public PixTransaction(String pixKeyUsed, PixKey.PixKeyType keyTypeUsed, BigDecimal value, LocalDateTime dateTime, Account originAccount, Account destinationAccount, PixTransactionStatus status) {
        this.pixKeyUsed = pixKeyUsed;
        this.keyTypeUsed = keyTypeUsed;
        this.value = value;
        this.dateTime = dateTime;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.status = status;
    }

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    // Remover setId(Long id)

    public String getPixKeyUsed() {
        return pixKeyUsed;
    }

    public void setPixKeyUsed(String pixKeyUsed) {
        this.pixKeyUsed = pixKeyUsed;
    }

    public PixKey.PixKeyType getKeyTypeUsed() {
        return keyTypeUsed;
    }

    public void setKeyTypeUsed(PixKey.PixKeyType keyTypeUsed) {
        this.keyTypeUsed = keyTypeUsed;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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

    public PixTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(PixTransactionStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixTransaction that = (PixTransaction) o;
        return Objects.equals(id, that.id); // Comparar apenas pelo ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash baseado apenas no ID
    }

    @Override
    public String toString() {
        return "PixTransaction{" +
                "id=" + id +
                ", pixKeyUsed='" + pixKeyUsed + '\'' +
                ", keyTypeUsed=" + keyTypeUsed +
                ", value=" + value +
                ", dateTime=" + dateTime +
                ", originAccount=" + (originAccount != null ? originAccount.getAccountNumber() : "null") +
                ", destinationAccount=" + (destinationAccount != null ? destinationAccount.getAccountNumber() : "null") +
                ", status=" + status +
                '}';
    }
}