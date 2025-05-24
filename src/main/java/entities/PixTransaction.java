package entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class PixTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pixKeyUsed;

    private String keyTypeUsed;

    // Relacionamento com a conta que fez a transação
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    public PixTransaction() {}

    public PixTransaction(String pixKeyUsed, String keyTypeUsed, Account account) {
        this.pixKeyUsed = pixKeyUsed;
        this.keyTypeUsed = keyTypeUsed;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PixTransaction)) return false;
        PixTransaction that = (PixTransaction) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(pixKeyUsed, that.pixKeyUsed) &&
                Objects.equals(keyTypeUsed, that.keyTypeUsed) &&
                Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pixKeyUsed, keyTypeUsed, account);
    }

    @Override
    public String toString() {
        return "PixTransaction{" +
                "id=" + id +
                ", pixKeyUsed='" + pixKeyUsed + '\'' +
                ", keyTypeUsed='" + keyTypeUsed + '\'' +
                ", account=" + (account != null ? account.getAccountNumber() : null) +
                '}';
    }
}
