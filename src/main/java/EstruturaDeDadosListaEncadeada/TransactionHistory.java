package EstruturaDeDadosListaEncadeada;

import entities.Account;

import java.time.LocalDate;

public class TransactionHistory {
    private TransactionInMemory head;

    public void add(long id, String typeTransaction, double value, LocalDate date, Account originAccount, Account destinationAccount) {
        TransactionInMemory newTx = new TransactionInMemory(id, typeTransaction, value, date, originAccount, destinationAccount);
        if (head == null) {
            head = newTx;
        } else {
            TransactionInMemory current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newTx;
        }
    }

    public void printHistory() {
        TransactionInMemory current = head;
        while (current != null) {
            System.out.printf("- ID: %d | Tipo: %s | Valor: R$ %.2f | Data: %s | Origem: %s | Destino: %s\n",
                    current.id, current.typeTransaction, current.value,
                    current.date, current.originAccount, current.destinationAccount);

            current = current.next;
        }
    }
}

