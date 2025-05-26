package view;

import services.AccountService;
import services.CardService;
import services.ClientService;
import services.TransactionService;

import java.util.Scanner;

public class InteractiveView {

    private final ViewAccount viewAccount;
    private final ViewClient viewClient;
    private final ViewCard viewCard;
    private final ViewTransaction viewTransaction;
    private final Scanner scanner = new Scanner(System.in);

    public InteractiveView(AccountService accountService, CardService cardService, ClientService clientService, TransactionService transactionService) {

        this.viewAccount = new ViewAccount(accountService);
        this.viewClient = new ViewClient(clientService);
        this.viewCard = new ViewCard(cardService);
        this.viewTransaction = new ViewTransaction(transactionService);
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n==== Banking System ====");
            System.out.println("1. Manage Accounts");
            System.out.println("2. Manage Clients");
            System.out.println("3. Manage Cards");
            System.out.println("4. Manage Transactions");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewAccount.manageAccounts();
                case 2 -> viewClient.manageClients();
                case 3 -> viewCard.manageCards();
                case 4 -> viewTransaction.manageTransactions();
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
