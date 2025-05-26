package view;

import entities.Account;
import entities.Client; // Importar Client para o login
import services.AccountService;
import services.CardService;
import services.ClientService;
import services.CurrentAccountService; // Nova
import services.PixKeyService;       // Nova
import services.PixTransactionService; // Nova
import services.SavingsAccountService; // Nova
import services.TransactionService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class InteractiveView {

    // Serviços para login e operações gerais (ex: visualizar todas as contas)
    private final AccountService accountService; // Para visualizar todas as contas (polimórfico)

    // Views específicas para cada funcionalidade
    private final ViewClient viewClient;
    private final ViewCard viewCard;
    private final ViewCurrentAccount viewCurrentAccount; // Nova
    private final ViewSavingsAccount viewSavingsAccount; // Nova
    private final ViewPixKey viewPixKey;             // Nova
    private final ViewPixTransaction viewPixTransaction; // Nova
    private final ViewTransaction viewTransaction; // A ViewTransaction antiga para "Transaction" genérica
    private final ClientService clientService;
    private final Scanner scanner;

    // Construtor atualizado para injetar todos os serviços e Views necessárias
    public InteractiveView(AccountService accountService, CardService cardService,
                           TransactionService transactionService, CurrentAccountService currentAccountService,
                           SavingsAccountService savingsAccountService, PixKeyService pixKeyService,
                           PixTransactionService pixTransactionService, ClientService clientService, ViewClient viewClient) {

        this.clientService = clientService;
        this.accountService = accountService; // Para visualização geral de contas

        // Instanciar as Views específicas, passando suas dependências
        this.viewClient = new ViewClient(clientService);
        this.viewCard = new ViewCard(cardService, accountService); // CardService precisa de AccountService
        this.viewCurrentAccount = new ViewCurrentAccount(currentAccountService, clientService, savingsAccountService); // CurrentAccountService precisa de ClientService e SavingsAccountService
        this.viewSavingsAccount = new ViewSavingsAccount(savingsAccountService, clientService, currentAccountService); // SavingsAccountService precisa de ClientService e CurrentAccountService
        this.viewPixKey = new ViewPixKey(pixKeyService, accountService); // PixKeyService precisa de AccountService
        this.viewPixTransaction = new ViewPixTransaction(pixTransactionService, accountService); // PixTransactionService precisa de AccountService

        this.viewTransaction = new ViewTransaction(transactionService, accountService); // Já existia

        this.scanner = new Scanner(System.in);
    }

    public void startApplication() { // Renomeado de showMenu para algo mais abrangente
        int choice;
        boolean loggedIn = false;
        boolean adm = false;
        Client authenticatedClient = null; // Armazena o cliente logado

        do {
            if (!loggedIn) {
                System.out.println("\n==== Welcome to InovaBank ====");
                System.out.println("1. Create New Client (No Login Required)"); // Acesso sem login
                System.out.println("2. Login to System(ADM)");
                System.out.println("3. Login to System(Client)");
                System.out.println("0. Exit Application");
                System.out.print("Choose an option: ");

                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (choice) {
                        case 1 -> viewClient.createClient(); // Permite criar cliente sem login
                        case 2 -> {
                            authenticatedClient = performLogin();
                            if (authenticatedClient != null) {
                                loggedIn = true;
                                adm = true;
                                System.out.println("\nLogin successful! Welcome, " + authenticatedClient.getName() + ".");
                            } else {
                                System.out.println("Login failed. Please try again.");
                            }
                        }
                        case 3 -> {
                            authenticatedClient = performLogin();
                            if (authenticatedClient != null) {
                                loggedIn = true;
                                adm = false;
                                System.out.println("\nLogin successful! Welcome, " + authenticatedClient.getName() + ".");
                            } else {
                                System.out.println("Login failed. Please try again.");
                            }
                        }
                        case 0 -> System.out.println("Exiting Application.");
                        default -> System.out.println("Invalid choice. Try again.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine(); // Consume invalid input
                    choice = -1; // Keep loop running
                }
            } else { // Usuário logado
                    System.out.println("\n==== InovaBank Main Menu ====");
                    System.out.println("Logged in as: " + authenticatedClient.getName() + " (CPF: " + authenticatedClient.getCpf() + ")");
                    System.out.println("1. Manage Clients");
                    System.out.println("2. Manage Accounts (Current & Savings)");
                    System.out.println("3. Manage Cards");
                    System.out.println("4. Manage Transactions (General)");
                    System.out.println("5. Manage Pix Keys");
                    System.out.println("6. Manage Pix Transactions");
                    System.out.println("7. View All Accounts (Generic)"); // Nova opção para AccountService.getAll()
                    System.out.println("8. Logout");
                    System.out.println("0. Exit Application");
                    System.out.print("Choose an option: ");

                    try {
                        choice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        switch (choice) {
                            case 1 -> viewClient.manageClients(adm);
                            case 2 -> manageAccountTypes(adm); // Novo método para escolher entre Current/Savings
                            case 3 -> viewCard.manageCards(adm);
                            case 4 -> viewTransaction.manageTransactions(adm);
                            case 5 -> viewPixKey.managePixKeys();
                            case 6 -> viewPixTransaction.managePixTransactions();
                            case 7 -> viewAllAccountsGeneric(); // Implementar
                            case 8 -> {
                                loggedIn = false;
                                authenticatedClient = null;
                                System.out.println("Logged out successfully.");
                            }
                            case 0 -> System.out.println("Exiting Application.");
                            default -> System.out.println("Invalid choice. Try again.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine();
                        choice = -1;
                    }
                }

        } while (choice != 0);
    }

    private Client performLogin() {
        System.out.print("Enter your CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();

        Optional<Client> clientOpt = clientService.authenticate(cpf, password);
        return clientOpt.orElse(null); // Retorna o cliente se autenticado, senão null
    }

    // Método para escolher entre gerenciar contas correntes ou poupança
    private void manageAccountTypes(boolean adm) {
        int subChoice;
        do {
            System.out.println("\n==== Account Types Management ====");
            System.out.println("1. Manage Current Accounts");
            System.out.println("2. Manage Savings Accounts");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                subChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (subChoice) {
                    case 1 -> viewCurrentAccount.manageCurrentAccounts(adm);
                    case 2 -> viewSavingsAccount.manageSavingsAccounts(adm);
                    case 0 -> System.out.println("Returning to main menu.");
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
                subChoice = -1;
            }
        } while (subChoice != 0);
    }

    // Método para visualizar todas as contas (Current e Savings) usando AccountService
    private void viewAllAccountsGeneric() {
        System.out.println("\n--- All Accounts (Current and Savings) ---");
        List<Account> accounts = accountService.getAll(); // Usa o AccountService genérico
        if (accounts.isEmpty()) {
            System.out.println("No accounts registered in the system.");
        } else {
            accounts.forEach(System.out::println);
        }
        System.out.println("------------------------------------------");
    }
}