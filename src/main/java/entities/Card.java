package entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class Card {
    // Talvez mude.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long cardNumber;

    private LocalDate validity;

    private int cvv;

    private String cardType;

    private double creditLimit;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public Card() {
        // Construtor padrão exigido pelo JPA
    }

    public Card(long cardNumber, LocalDate validity, int cvv, String cardType, double creditLimit, Account account) {
        this.cardNumber = cardNumber;
        this.validity = validity;
        this.cvv = cvv;
        this.cardType = cardType;
        this.creditLimit = creditLimit;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public int getCvv() {
        return cvv;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + getId() +
                ", cardNumber=" + getCardNumber() +
                ", validity=" + getValidity() +
                ", cvv=" + getCvv() +
                ", cardType='" + getCardType() + '\'' +
                ", creditLimit=" + getCreditLimit() +
                '}';
    }
}
