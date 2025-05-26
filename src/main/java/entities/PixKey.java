package entities;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "pix_keys") // Boa prática para nomear a tabela explicitamente
public class PixKey {

    // Recomendação: Criar um Enum para os tipos de chave Pix
    public enum PixKeyType {
        CPF, EMAIL, PHONE, RANDOM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Chave Pix deve ser única e não nula
    private String keyValue;

    @Enumerated(EnumType.STRING) // Mapear o Enum como String no DB
    @Column(nullable = false) // Tipo da chave não pode ser nulo
    private PixKeyType keyType; // Alterado para o tipo Enum

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false) // Coluna account_id não pode ser nula
    private Account account;

    public PixKey() {}

    public PixKey(String keyValue, PixKeyType keyType, Account account) { // Construtor com PixKeyType Enum
        this.keyValue = keyValue;
        this.keyType = keyType;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    // Remover setId(Long id) - ID é gerado automaticamente e não deve ser setado manualmente.
    // public void setId(Long id) { this.id = id; }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public PixKeyType getKeyType() { // Retorna PixKeyType Enum
        return keyType;
    }

    public void setKeyType(PixKeyType keyType) { // Recebe PixKeyType Enum
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
        if (o == null || getClass() != o.getClass()) return false; // Melhor verificação de classe
        PixKey pixKey = (PixKey) o;
        // Para entidades JPA, é mais seguro usar apenas o ID para equals/hashCode
        // após a persistência. Antes da persistência (ID nulo), pode-se usar keyValue.
        // No entanto, para simplicidade e consistência, focamos no ID persistido.
        return Objects.equals(id, pixKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Gera hash baseado apenas no ID
    }

    @Override
    public String toString() {
        return "PixKey{" +
                "id=" + id +
                ", keyValue='" + keyValue + '\'' +
                ", keyType=" + keyType + // toString do Enum já é o nome
                ", account=" + (account != null ? account.getAccountNumber() : "null") +
                '}';
    }
}