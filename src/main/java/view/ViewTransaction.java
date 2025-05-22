package view;

import entities.Account;
import entities.Transaction;
import services.AccountService;
import services.TransactionService;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class ViewTransaction {

    private final TransactionService transactionService;
    private final Scanner scanner;

    private final AccountService accountService;

    public ViewTransaction(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    void manageTransactions() {
        System.out.println("\n==== Transaction Management ====");
        System.out.println("1. Create Transaction");
        System.out.println("2. View Transaction");
        System.out.println("3. View All Transactions");
        System.out.println("4. Update Transaction");
        System.out.println("5. Delete Transaction");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter transaction type (e.g., Pix, TED): ");
                String typeTransaction = scanner.nextLine();
                System.out.print("Enter transaction value: ");
                double value = scanner.nextDouble();
                scanner.nextLine();
                System.out.print("Enter transaction date (YYYY-MM-DD): ");
                String dateStr = scanner.nextLine();
                LocalDate date = LocalDate.parse(dateStr);
                System.out.print("Enter origin account number: ");
                String originAccountNumber = scanner.nextLine();



                System.out.print("Enter destination account number: ");
                String destinationAccountNumber = scanner.nextLine();
                Optional<Account> originAccount = accountService.getByAccountNumber(originAccountNumber);
                Optional<Account> destinationAccount =accountService.getByAccountNumber(destinationAccountNumber);

                if(originAccount.isPresent() && destinationAccount.isPresent()){
                    Transaction transaction = new Transaction(typeTransaction, value, date, originAccount.get(), destinationAccount.get());
                    transactionService.create(transaction);
                    System.out.println("Transaction created successfully.");
                }


            }
            case 2 -> {
                System.out.print("Enter transaction ID: ");
                long id = scanner.nextLong();
                scanner.nextLine();
                transactionService.getById(id).ifPresentOrElse(
                        System.out::println,
                        () -> System.out.println("Transaction not found."));
            }
            case 3 -> transactionService.getAll();
            case 4 -> {
                System.out.print("Enter transaction ID: ");
                long id = scanner.nextLong();
                scanner.nextLine();
                transactionService.getById(id).ifPresentOrElse(transaction -> {
                    System.out.print("Enter new transaction type: ");
                    String newTypeTransaction = scanner.nextLine();
                    System.out.print("Enter new transaction value: ");
                    double newValue = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Enter new transaction date (YYYY-MM-DD): ");
                    String newDateStr = scanner.nextLine();
                    LocalDate newDate = LocalDate.parse(newDateStr);
                    System.out.print("Enter new origin account number: ");
                    String newOriginAccountNumber = scanner.nextLine();
                    System.out.print("Enter new destination account number: ");
                    String newDestinationAccountNumber = scanner.nextLine();

                    Optional<Account> newOriginAccount = accountService.getByAccountNumber(newOriginAccountNumber);
                    Optional<Account> newDestinationAccount =accountService.getByAccountNumber(newDestinationAccountNumber);

                    if(newOriginAccount.isPresent() && newDestinationAccount.isPresent()){
                        transaction.setTypeTransaction(newTypeTransaction);
                        transaction.setValue(newValue);
                        transaction.setDate(newDate);
                        transaction.setOriginAccount(newOriginAccount.get());
                        transaction.setDestinationAccount(newDestinationAccount.get());
                        transactionService.update(transaction);
                        System.out.println("Transaction updated successfully.");
                    }
                }, () -> System.out.println("Transaction not found."));
            }
            case 5 -> {
                System.out.print("Enter transaction ID: ");
                long id = scanner.nextLong();
                scanner.nextLine();
                transactionService.delete(id);
                System.out.println("Transaction deleted successfully.");
            }
            default -> System.out.println("Invalid choice. Try again.");
        }
    }
}
