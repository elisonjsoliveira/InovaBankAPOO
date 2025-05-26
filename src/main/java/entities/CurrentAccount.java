package entities;


import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class CurrentAccount extends Account {

    public CurrentAccount() {
        super();
    }

    public CurrentAccount(String accountNumber, BigDecimal balance, Client client) {
        super(accountNumber, balance, client);
    }

    // Sem novos campos nem métodos extras, usa tudo herdado
}
