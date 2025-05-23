package entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.util.Objects;

@Entity
    public class SavingsAccount extends Account {

    private double interestRate;

    @Column(name = "latest_interest_update")
    private LocalDate latestInterestUpdate;

    public SavingsAccount() {
        super();
    }

    public SavingsAccount(String accountNumber, double balance, Client client, double interestRate, LocalDate latestInterestUpdate) {
        super(accountNumber, balance, client);
        this.interestRate = interestRate;
        this.latestInterestUpdate = latestInterestUpdate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
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
        return Double.compare(that.interestRate, interestRate) == 0 &&
                Objects.equals(latestInterestUpdate, that.latestInterestUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), interestRate, latestInterestUpdate);
    }
}
