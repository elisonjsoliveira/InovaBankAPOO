package EstruturaDeDadosListaEncadeada;

import entities.Account;

import java.time.LocalDate;

public class TransactionInMemory {
    Long id;
    String typeTransaction;
    double value;
    LocalDate date;
    Account originAccount;
    Account destinationAccount;

    TransactionInMemory next;

    public TransactionInMemory(Long id, String typeTransaction, double value, LocalDate date, Account originAccount, Account destinationAccount) {
        this.id = id;
        this.typeTransaction = typeTransaction;
        this.value = value;
        this.date = date;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
    }
}



