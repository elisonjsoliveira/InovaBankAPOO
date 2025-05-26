package entities;

import jakarta.persistence.*;
import java.math.BigDecimal; // Importando BigDecimal
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false) // Garante que o número do cartão é único e não nulo
    private String cardNumber; // Alterado para String para melhor manipulação (zeros à esquerda, algoritmos de validação)

    @Column(nullable = false) // Data de validade não pode ser nula
    private LocalDate validity;

    @Column(nullable = false) // CVV não pode ser nulo. ATENÇÃO: Em um sistema real, o CVV não seria armazenado em texto puro.
    private int cvv; // Mantido como int para este projeto, mas com ressalva de segurança

    @Column(nullable = false) // Tipo de cartão não pode ser nulo
    private String cardType;

    @Column(nullable = false, precision = 19, scale = 2) // Usar BigDecimal para valores monetários
    private BigDecimal creditLimit;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false) // Um cartão pertence a UMA conta, e a conta não pode ser nula
    private Account account;

    public Card() {
        // Construtor vazio exigido pelo JPA
    }

    public Card(String cardNumber, LocalDate validity, int cvv, String cardType, BigDecimal creditLimit, Account account) {
        this.cardNumber = cardNumber;
        this.validity = validity;
        this.cvv = cvv;
        this.cardType = cardType;
        this.creditLimit = creditLimit;
        this.account = account;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public int getCvv() {
        return cvv;
    }

    public String getCardType() {
        return cardType;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public Account getAccount() {
        return account;
    }

    // Setters
    // Note: Geralmente, o cardNumber e o id não têm setters públicos após a criação para manter a imutabilidade
    // Mas para flexibilidade do JPA e para o contexto do projeto, mantemos.
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Usa getClass() != o.getClass() para comparação estrita de tipo,
        // ou 'instanceof Card card' se polimorfismo for esperado em equals.
        // Para entidades JPA, Objects.equals(id, card.id) é a abordagem mais segura.
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        // Compara pelo ID e também pelo cardNumber, pois ele é único.
        // Idealmente, apenas o ID seria suficiente para entidades persistidas.
        return Objects.equals(id, card.id) &&
                Objects.equals(cardNumber, card.cardNumber);
    }

    @Override
    public int hashCode() {
        // Calcula o hash baseado no ID e no cardNumber, pois ambos são únicos e cruciais para a identidade.
        return Objects.hash(id, cardNumber);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", cardNumber='" + cardNumber + '\'' +
                ", validity=" + validity +
                ", cvv=" + cvv + // CVV pode ser omitido do toString por segurança
                ", cardType='" + cardType + '\'' +
                ", creditLimit=" + creditLimit +
                ", accountId=" + (account != null ? account.getId() : "null") +
                '}';
    }
}