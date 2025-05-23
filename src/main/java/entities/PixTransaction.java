package entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class PixTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "transaction_id", unique = true)
    private Transaction transaction;

    private String pixKeyUsed;

    private String keyTypeUsed;

    public PixTransaction() {
    }

    public PixTransaction(Transaction transaction, String pixKeyUsed, String keyTypeUsed) {
        this.transaction = transaction;
        this.pixKeyUsed = pixKeyUsed;
        this.keyTypeUsed = keyTypeUsed;
    }

    public Long getId() {
        return id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getPixKeyUsed() {
        return pixKeyUsed;
    }

    public void setPixKeyUsed(String pixKeyUsed) {
        this.pixKeyUsed = pixKeyUsed;
    }

    public String getKeyTypeUsed() {
        return keyTypeUsed;
    }

    public void setKeyTypeUsed(String keyTypeUsed) {
        this.keyTypeUsed = keyTypeUsed;
    }

    @Override
    public String toString() {
        return "PixTransaction{" +
                "id=" + id +
                ", transaction=" + (transaction != null ? transaction.getId() : null) +
                ", pixKeyUsed='" + pixKeyUsed + '\'' +
                ", keyTypeUsed='" + keyTypeUsed + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PixTransaction)) return false;
        PixTransaction that = (PixTransaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(transaction, that.transaction) &&
                Objects.equals(pixKeyUsed, that.pixKeyUsed) &&
                Objects.equals(keyTypeUsed, that.keyTypeUsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, transaction, pixKeyUsed, keyTypeUsed);
    }
}
