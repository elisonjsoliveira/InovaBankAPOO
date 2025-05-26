package entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
    public class SavingsAccount extends Account {

    private BigDecimal interestRate;

    @Column(name = "latest_interest_update")
    private LocalDate latestInterestUpdate;

    public SavingsAccount() {
        super();
    }

    public SavingsAccount(String accountNumber, BigDecimal balance, Client client, BigDecimal interestRate, LocalDate latestInterestUpdate) {
        super(accountNumber, balance, client);
        this.interestRate = interestRate;
        this.latestInterestUpdate = latestInterestUpdate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate latestInterestUpdate() {
        return latestInterestUpdate;
    }

    public void setLatestInterestUpdate(LocalDate latestInterestUpdate) {
        this.latestInterestUpdate = latestInterestUpdate;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "interestRate=" + interestRate +
                ", latestInterestUpdate=" + latestInterestUpdate +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavingsAccount)) return false;
        if (!super.equals(o)) return false;
        SavingsAccount that = (SavingsAccount) o;
        return Objects.equals(interestRate, that.interestRate) &&
                Objects.equals(latestInterestUpdate, that.latestInterestUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), interestRate, latestInterestUpdate);
    }
}
