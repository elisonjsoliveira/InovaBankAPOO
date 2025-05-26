package view;

import entities.Client;
import entities.CurrentAccount;
import services.ClientService;
import services.CurrentAccountService;
import services.SavingsAccountService; // Necessário para a transferência

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ViewCurrentAccount {

    private final CurrentAccountService currentAccountService;
    private final ClientService clientService;
    private final SavingsAccountService savingsAccountService; // Para transferências
    private final Scanner scanner;

    public ViewCurrentAccount(CurrentAccountService currentAccountService, ClientService clientService, SavingsAccountService savingsAccountService) {
        this.currentAccountService = currentAccountService;
        this.clientService = clientService;
        this.savingsAccountService = savingsAccountService; // Injetar
        this.scanner = new Scanner(System.in);
    }

    public void manageCurrentAccounts(boolean adm) { // Tornar público e com loop
        int choice;
        do {
            System.out.println("\n==== Current Account Management ====");
            System.out.println("1. Create Current Account");
            System.out.println("2. View Current Account by Number");
            System.out.println("3. Update Current Account");
            System.out.println("4. Delete Current Account");
            System.out.println("5. Transfer from Current to Savings Account"); // Nova funcionalidade
            if(adm){
                System.out.println("6. View All Current Accounts");
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> createCurrentAccount();
                    case 2 -> viewCurrentAccountByNumber();
                    case 3 -> updateCurrentAccount();
                    case 4 -> deleteCurrentAccount();
                    case 5 -> transferToSavings();
                    case 6 -> viewAllCurrentAccounts();
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

    private void createCurrentAccount() {
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

        System.out.print("Enter client CPF to associate: ");
        String clientCPF = scanner.nextLine();

        Optional<Client> clientOpt = clientService.getByCPF(clientCPF);

        if (clientOpt.isPresent()) {
            CurrentAccount account = new CurrentAccount(accountNumber, initialBalance, clientOpt.get());
            currentAccountService.create(account);
            // O service já imprime sucesso/falha (ex: conta já existe)
        } else {
            System.out.println("Client not found for CPF: " + clientCPF + ". Current Account not created.");
        }
    }

    private void viewCurrentAccountByNumber() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        currentAccountService.getByAccountNumber(accountNumber).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Current Account not found with number: " + accountNumber + "."));
    }

    private void viewAllCurrentAccounts() {
        System.out.println("\n--- All Current Accounts ---");
        List<CurrentAccount> accounts = currentAccountService.getAll();
        if (accounts.isEmpty()) {
            System.out.println("No Current Accounts registered.");
        } else {
            accounts.forEach(System.out::println);
        }
        System.out.println("--------------------------");
    }

    private void updateCurrentAccount() {
        System.out.print("Enter account number to update: ");
        String accountNumberToUpdate = scanner.nextLine();

        currentAccountService.getByAccountNumber(accountNumberToUpdate).ifPresentOrElse(account -> {
            System.out.println("Current Account found. Enter new details (leave blank to keep current):");

            // Não permitir alterar o número da conta diretamente ou o cliente.
            // Para saldo, seria uma operação de depósito/saque, não um update direto.
            // Exemplo de atualização de algum campo (se houver, que não seja saldo)

            // Por enquanto, CurrentAccount não tem campos únicos para update além de balance
            // e balance é manipulado por transferência/depósito/saque.
            // Se houvesse um 'overdraftLimit', seria atualizado aqui.

            System.out.println("No direct editable fields for Current Account through this update. ");
            System.out.println("Use transfer/deposit/withdraw operations to change balance.");

            // currentAccountService.update(account); // Chamaria isso se houvesse campos para atualizar
            System.out.println("No update performed as there are no direct editable fields.");
        }, () -> System.out.println("Current Account not found with number: " + accountNumberToUpdate + ". Update failed."));
    }

    private void deleteCurrentAccount() {
        System.out.print("Enter account number to delete: ");
        String accountNumberToDelete = scanner.nextLine();
        currentAccountService.delete(accountNumberToDelete);
        // O service já imprime feedback
        System.out.println("Attempted deletion for Current Account number: " + accountNumberToDelete + ".");
    }

    private void transferToSavings() {
        System.out.print("Enter YOUR Current Account number (source): ");
        String currentAccNum = scanner.nextLine();
        System.out.print("Enter Savings Account number (destination): ");
        String savingsAccNum = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        BigDecimal amount;
        try {
            amount = new BigDecimal(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Transfer cancelled.");
            return;
        }

        currentAccountService.transferToSavings(currentAccNum, savingsAccNum, amount);
        // O service já imprime feedback de sucesso/falha (saldo insuficiente, contas não encontradas, etc.)
    }
}