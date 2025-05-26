package view;

import entities.PixKey; // Para exibir tipos de chave
import services.AccountService; // Para buscar a conta de origem
import services.PixTransactionService;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import entities.PixTransaction; // Para o método viewAllPixTransactions

public class ViewPixTransaction {

    private final PixTransactionService pixTransactionService;
    private final AccountService accountService; // Necessário para validar a conta de origem
    private final Scanner scanner;

    public ViewPixTransaction(PixTransactionService pixTransactionService, AccountService accountService) {
        this.pixTransactionService = pixTransactionService;
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    public void managePixTransactions() {
        int choice;
        do {
            System.out.println("\n==== Pix Transaction Management ====");
            System.out.println("1. Perform Pix Transfer");
            System.out.println("2. View Pix Transaction by ID");
            System.out.println("3. View All Pix Transactions by Origin Account");
            // Operações de update/delete não são comuns para transações financeiras
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> performPixTransfer();
                    case 2 -> viewPixTransactionById();
                    case 3 -> viewAllPixTransactionsByAccount();
                    case 0 -> System.out.println("Returning to main menu.");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                choice = -1; // Keep loop running
            }

        } while (choice != 0);
    }

    private void performPixTransfer() {
        System.out.print("Enter your Account Number (Origin Account): ");
        String originAccountNumber = scanner.nextLine();

        // Verificar se a conta de origem existe ANTES de prosseguir
        if (accountService.getByAccountNumber(originAccountNumber).isEmpty()) {
            System.out.println("Origin Account not found. Pix transfer cancelled.");
            return;
        }

        System.out.print("Enter Receiver's Pix Key Value: ");
        String pixKeyUsedValue = scanner.nextLine();

        System.out.println("Select Receiver's Pix Key Type:");
        for (PixKey.PixKeyType type : PixKey.PixKeyType.values()) {
            System.out.println((type.ordinal() + 1) + ". " + type.name());
        }
        System.out.print("Choose type: ");
        String keyTypeUsedString;
        try {
            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            keyTypeUsedString = PixKey.PixKeyType.values()[typeChoice - 1].name(); // Get Enum name as String
        } catch (InputMismatchException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid Pix Key Type choice. Pix transfer cancelled.");
            scanner.nextLine(); // Consume invalid input if InputMismatchException
            return;
        }

        System.out.print("Enter amount to transfer: ");
        BigDecimal value;
        try {
            value = new BigDecimal(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Pix transfer cancelled.");
            return;
        }

        // Chamar o método do serviço que orquestra a transação Pix completa
        pixTransactionService.performPixTransfer(originAccountNumber, pixKeyUsedValue, keyTypeUsedString, value);
        // O service já imprime o feedback de sucesso/falha
    }

    private void viewPixTransactionById() {
        System.out.print("Enter Pix Transaction ID to view: ");
        long id;
        try {
            id = scanner.nextLong();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid Transaction ID format. Please enter a number.");
            scanner.nextLine(); // Consume invalid input
            return;
        }

        pixTransactionService.findById(id).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Pix Transaction not found with ID: " + id + "."));
    }

    private void viewAllPixTransactionsByAccount() {
        System.out.print("Enter Account ID to view all Pix Transactions originated from it: ");
        long accountId;
        try {
            accountId = scanner.nextLong();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid Account ID format. Please enter a number.");
            scanner.nextLine(); // Consume invalid input
            return;
        }

        System.out.println("\n--- All Pix Transactions for Origin Account ID: " + accountId + " ---");
        List<PixTransaction> pixTransactions = pixTransactionService.findAllByAccount(accountId);
        if (pixTransactions.isEmpty()) {
            System.out.println("No Pix Transactions originated from this account.");
        } else {
            pixTransactions.forEach(System.out::println);
        }
        System.out.println("--------------------------------------------------");
    }
}