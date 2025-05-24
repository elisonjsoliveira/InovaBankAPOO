package entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class PixKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyValue;

    private String keyType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id")
    private Account account;

    public PixKey() {}

    public PixKey(String keyValue, String keyType, Account account) {
        this.keyValue = keyValue;
        this.keyType = keyType;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
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
        if (!(o instanceof PixKey)) return false;
        PixKey pixKey = (PixKey) o;
        return Objects.equals(id, pixKey.id) &&
                Objects.equals(keyValue, pixKey.keyValue) &&
                Objects.equals(keyType, pixKey.keyType) &&
                Objects.equals(account, pixKey.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keyValue, keyType, account);
    }

    @Override
    public String toString() {
        return "PixKey{" +
                "id=" + id +
                ", keyValue='" + keyValue + '\'' +
                ", keyType='" + keyType + '\'' +
                ", account=" + (account != null ? account.getAccountNumber() : null) +
                '}';
    }
}
