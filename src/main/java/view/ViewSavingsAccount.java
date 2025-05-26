package view;

import entities.Client;
import entities.SavingsAccount;
import services.ClientService;
import services.CurrentAccountService; // Necessário para a transferência
import services.SavingsAccountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ViewSavingsAccount {

    private final SavingsAccountService savingsAccountService;
    private final ClientService clientService;
    private final CurrentAccountService currentAccountService; // Para transferências
    private final Scanner scanner;

    public ViewSavingsAccount(SavingsAccountService savingsAccountService, ClientService clientService, CurrentAccountService currentAccountService) {
        this.savingsAccountService = savingsAccountService;
        this.clientService = clientService;
        this.currentAccountService = currentAccountService; // Injetar
        this.scanner = new Scanner(System.in);
    }

    public void manageSavingsAccounts(boolean adm) { // Tornar público e com loop
        int choice;
        do {
            System.out.println("\n==== Savings Account Management ====");
            System.out.println("1. Create Savings Account");
            System.out.println("2. View Savings Account by Number");
            System.out.println("3. Update Savings Account");
            System.out.println("4. Delete Savings Account");
            System.out.println("5. Apply Interest"); // Nova funcionalidade
            System.out.println("6. Transfer from Savings to Current Account"); // Nova funcionalidade
            if(adm){
                System.out.println("7. View All Savings Accounts");
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> createSavingsAccount();
                    case 2 -> viewSavingsAccountByNumber();
                    case 3 -> updateSavingsAccount();
                    case 4 -> deleteSavingsAccount();
                    case 5 -> applyInterestToSavingsAccount();
                    case 6 -> transferToCurrent();
                    case 7 -> viewAllSavingsAccounts();
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

    private void createSavingsAccount() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        BigDecimal initialBalance;
        try {
            initialBalance = new BigDecimal(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid balance format. Account not created.");
            return;
        }

        System.out.print("Enter interest rate (e.g., 0.01 for 1%): ");
        BigDecimal interestRate;
        try {
            interestRate = new BigDecimal(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid interest rate format. Account not created.");
            return;
        }

        // A data da última atualização de juros pode ser a data atual na criação
        LocalDate latestInterestUpdate = LocalDate.now();

        System.out.print("Enter client CPF to associate: ");
        String clientCPF = scanner.nextLine();

        Optional<Client> clientOpt = clientService.getByCPF(clientCPF);

        if (clientOpt.isPresent()) {
            SavingsAccount account = new SavingsAccount(accountNumber, initialBalance, clientOpt.get(), interestRate, latestInterestUpdate);
            savingsAccountService.create(account);
            // O service já imprime sucesso/falha (ex: conta já existe)
        } else {
            System.out.println("Client not found for CPF: " + clientCPF + ". Savings Account not created.");
        }
    }

    private void viewSavingsAccountByNumber() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        savingsAccountService.getByAccountNumber(accountNumber).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Savings Account not found with number: " + accountNumber + "."));
    }

    private void viewAllSavingsAccounts() {
        System.out.println("\n--- All Savings Accounts ---");
        List<SavingsAccount> accounts = savingsAccountService.getAll();
        if (accounts.isEmpty()) {
            System.out.println("No Savings Accounts registered.");
        } else {
            accounts.forEach(System.out::println);
        }
        System.out.println("--------------------------");
    }

    private void updateSavingsAccount() {
        System.out.print("Enter account number to update: ");
        String accountNumberToUpdate = scanner.nextLine();

        savingsAccountService.getByAccountNumber(accountNumberToUpdate).ifPresentOrElse(account -> {
            System.out.println("Savings Account found. Enter new details (leave blank to keep current):");

            // Atualizar taxa de juros (se for um campo que pode ser alterado)
            System.out.print("Enter new interest rate [" + account.getInterestRate() + "]: ");
            String newInterestRateStr = scanner.nextLine();
            if (!newInterestRateStr.trim().isEmpty()) {
                try {
                    BigDecimal newInterestRate = new BigDecimal(newInterestRateStr);
                    account.setInterestRate(newInterestRate);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid interest rate format. Keeping current rate.");
                }
            }
            // A data da última atualização de juros pode ser atualizada automaticamente no applyInterest

            // Atualizar o account (chama o service para persistir as alterações)
            savingsAccountService.update(account);
            System.out.println("Savings Account updated successfully.");
        }, () -> System.out.println("Savings Account not found with number: " + accountNumberToUpdate + ". Update failed."));
    }

    private void deleteSavingsAccount() {
        System.out.print("Enter account number to delete: ");
        String accountNumberToDelete = scanner.nextLine();
        savingsAccountService.delete(accountNumberToDelete);
        // O service já imprime feedback
        System.out.println("Attempted deletion for Savings Account number: " + accountNumberToDelete + ".");
    }

    private void applyInterestToSavingsAccount() {
        System.out.print("Enter Savings Account number to apply interest: ");
        String accountNumber = scanner.nextLine();
        savingsAccountService.applyInterest(accountNumber);
        // O service já imprime feedback de sucesso/falha
    }

    private void transferToCurrent() {
        System.out.print("Enter YOUR Savings Account number (source): ");
        String savingsAccNum = scanner.nextLine();
        System.out.print("Enter Current Account number (destination): ");
        String currentAccNum = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        BigDecimal amount;
        try {
            amount = new BigDecimal(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Transfer cancelled.");
            return;
        }

        savingsAccountService.transferToCurrent(savingsAccNum, currentAccNum, amount);
        // O service já imprime feedback de sucesso/falha (saldo insuficiente, contas não encontradas, etc.)
    }
}