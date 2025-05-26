package view;

import entities.Account;
import entities.Transaction;
import services.AccountService;
import services.TransactionService;

import java.math.BigDecimal; // Importar BigDecimal
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List; // Importar List
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

    public void manageTransactions(boolean adm) { // Tornar público e com loop
        int choice;
        do {
            System.out.println("\n==== Transaction Management ====");
            System.out.println("1. Create Transaction");
            System.out.println("2. View Transaction by ID");
            if(adm) {
                System.out.println("3. View All Transactions");
                System.out.println("4. Update Transaction (Discouraged!)"); // Adicionado alerta
                System.out.println("5. Delete Transaction (Discouraged!)"); // Adicionado alerta
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consumir a nova linha

                switch (choice) {
                    case 1 -> createTransaction();
                    case 2 -> viewTransactionById();
                    case 3 -> viewAllTransactions();
                    case 4 -> updateTransaction(); // Manter, mas com ressalvas
                    case 5 -> deleteTransaction(); // Manter, mas com ressalvas
                    case 0 -> System.out.println("Returning to main menu.");
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consumir a entrada inválida
                choice = -1; // Para manter o loop
            }

        } while (choice != 0);
    }

    private void createTransaction() {
        System.out.print("Enter transaction type (e.g., Pix, TED): ");
        String typeTransaction = scanner.nextLine();
        System.out.print("Enter transaction value: ");
        BigDecimal value; // Alterado para BigDecimal
        try {
            value = new BigDecimal(scanner.nextLine()); // Lendo como String e convertendo
        } catch (NumberFormatException e) {
            System.out.println("Invalid value format. Transaction creation cancelled.");
            return;
        }

        System.out.print("Enter transaction date (YYYY-MM-DD) [Leave blank for today]: ");
        String dateStr = scanner.nextLine();
        LocalDate date;
        try {
            date = dateStr.trim().isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD. Transaction creation cancelled.");
            return;
        }

        System.out.print("Enter origin account number: ");
        String originAccountNumber = scanner.nextLine();
        System.out.print("Enter destination account number: ");
        String destinationAccountNumber = scanner.nextLine();

        Optional<Account> originAccountOpt = accountService.getByAccountNumber(originAccountNumber);
        Optional<Account> destinationAccountOpt = accountService.getByAccountNumber(destinationAccountNumber);

        if (originAccountOpt.isEmpty()) {
            System.out.println("Origin account not found. Transaction not created.");
            return;
        }
        if (destinationAccountOpt.isEmpty()) {
            System.out.println("Destination account not found. Transaction not created.");
            return;
        }

        // Criar a transação
        Transaction transaction = new Transaction(typeTransaction, value, date, originAccountOpt.get(), destinationAccountOpt.get());
        transactionService.create(transaction); // O service já lida com sucesso/falha e feedback
    }

    private void viewTransactionById() {
        System.out.print("Enter transaction ID: ");
        long id;
        try {
            id = scanner.nextLong();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid ID format. Please enter a number.");
            scanner.nextLine();
            return;
        }

        transactionService.getById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Transaction not found for ID: " + id + "."));
    }

    private void viewAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        List<Transaction> transactions = transactionService.getAll(); // Agora retorna List<Transaction>
        if (transactions.isEmpty()) {
            System.out.println("No transactions registered.");
        } else {
            transactions.forEach(System.out::println); // Imprime cada transação
        }
        System.out.println("------------------------");
    }

    private void updateTransaction() {
        System.out.println("\nWARNING: Updating transactions directly is generally discouraged in financial systems.");
        System.out.println("This operation is for demonstration purposes. Consider creating 'storno' transactions instead.");
        System.out.print("Enter transaction ID to update: ");
        long id;
        try {
            id = scanner.nextLong();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid ID format. Please enter a number.");
            scanner.nextLine();
            return;
        }

        transactionService.getById(id).ifPresentOrElse(transaction -> {
            System.out.println("Transaction found. Enter new details (leave blank to keep current):");

            System.out.print("Enter new transaction type [" + transaction.getTypeTransaction() + "]: ");
            String newTypeTransaction = scanner.nextLine();
            if (!newTypeTransaction.trim().isEmpty()) {
                transaction.setTypeTransaction(newTypeTransaction);
            }

            System.out.print("Enter new transaction value [" + transaction.getValue() + "]: ");
            String newValueStr = scanner.nextLine();
            if (!newValueStr.trim().isEmpty()) {
                try {
                    BigDecimal newValue = new BigDecimal(newValueStr);
                    transaction.setValue(newValue);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid value format. Keeping current value.");
                }
            }

            System.out.print("Enter new transaction date (YYYY-MM-DD) [" + transaction.getDate() + "]: ");
            String newDateStr = scanner.nextLine();
            if (!newDateStr.trim().isEmpty()) {
                try {
                    LocalDate newDate = LocalDate.parse(newDateStr);
                    transaction.setDate(newDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Keeping current date.");
                }
            }

            // ATENÇÃO: Alterar contas de origem/destino de uma transação existente pode gerar inconsistências.
            // Geralmente não se faz isso.
            System.out.print("Enter new origin account number (leave blank to keep current) [" + (transaction.getOriginAccount() != null ? transaction.getOriginAccount().getAccountNumber() : "null") + "]: ");
            String newOriginAccountNumber = scanner.nextLine();
            if (!newOriginAccountNumber.trim().isEmpty()) {
                Optional<Account> newOriginAccountOpt = accountService.getByAccountNumber(newOriginAccountNumber);
                if (newOriginAccountOpt.isPresent()) {
                    transaction.setOriginAccount(newOriginAccountOpt.get());
                } else {
                    System.out.println("New origin account not found. Keeping current origin account.");
                }
            }

            System.out.print("Enter new destination account number (leave blank to keep current) [" + (transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getAccountNumber() : "null") + "]: ");
            String newDestinationAccountNumber = scanner.nextLine();
            if (!newDestinationAccountNumber.trim().isEmpty()) {
                Optional<Account> newDestinationAccountOpt = accountService.getByAccountNumber(newDestinationAccountNumber);
                if (newDestinationAccountOpt.isPresent()) {
                    transaction.setDestinationAccount(newDestinationAccountOpt.get());
                } else {
                    System.out.println("New destination account not found. Keeping current destination account.");
                }
            }

            transactionService.update(transaction);
        }, () -> System.out.println("Transaction not found for ID: " + id + ". Update failed."));
    }

    private void deleteTransaction() {
        System.out.println("\nWARNING: Deleting transactions directly is generally discouraged in financial systems.");
        System.out.println("This operation is for demonstration purposes. Consider creating 'storno' transactions instead.");
        System.out.print("Enter transaction ID to delete: ");
        long id;
        try {
            id = scanner.nextLong();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid ID format. Please enter a number.");
            scanner.nextLine();
            return;
        }

        transactionService.delete(id);
    }
}