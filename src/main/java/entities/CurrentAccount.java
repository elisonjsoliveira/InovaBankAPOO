package entities;


import jakarta.persistence.Entity;

@Entity
public class CurrentAccount extends Account {

    public CurrentAccount() {
        super();
    }

    public CurrentAccount(String accountNumber, double balance, Client client) {
        super(accountNumber, balance, client);
    }

    // Sem novos campos nem métodos extras, usa tudo herdado
}
