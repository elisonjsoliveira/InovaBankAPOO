import repository.AccountRepository;
import repository.CardRepository;
import repository.ClientRepository;
import repository.CurrentAccountRepository;
import repository.PixKeyRepository;
import repository.PixTransactionRepository;
import repository.SavingsAccountRepository;
import repository.TransactionRepository;

import services.AccountService;
import services.CardService;
import services.ClientService;
import services.CurrentAccountService;
import services.PixKeyService;
import services.PixTransactionService;
import services.SavingsAccountService;
import services.TransactionService;

import util.JPAUtil;
import view.InteractiveView;
import view.ViewClient;
// Nenhuma view é importada diretamente aqui além da InteractiveView,
// pois InteractiveView as instancia.
// import view.ViewCard;
// import view.ViewClient;
// import view.ViewCurrentAccount;
// import view.ViewPixKey;
// import view.ViewPixTransaction;
// import view.ViewSavingsAccount;
// import view.ViewTransaction;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Mensagem inicial bonitinha, só uma vez
        System.out.println("=====================================");
        System.out.println("    InovaBank - Acesso ao sistema    ");
        System.out.println("=====================================");
        System.out.println("Por favor! Antes de iniciar o sistema, digite o seu User e o password do banco de dados para iniciar a sua conexão.");
        System.out.println();

        String dbUser;
        String dbPassword;
        boolean dbConectado = false;

        // Loop até conectar com sucesso ao banco de dados
        while (!dbConectado) {
            System.out.print("Digite seu usuário do banco de dados: ");
            dbUser = sc.nextLine();

            System.out.print("Digite sua senha do banco de dados: ");
            dbPassword = sc.nextLine();

            try {
                JPAUtil.init(dbUser, dbPassword); // Inicializa o JPAUtil com as credenciais do DB
                dbConectado = true;

                System.out.println("\n=========================================");
                System.out.println("      InovaBank - Conexão estabelecida     ");
                System.out.println("===========================================\n");

            } catch (Exception e) {
                System.out.println("\nErro ao conectar ao banco de dados: " + e.getMessage());
                System.out.println("Certifique-se de que o banco de dados está rodando e as credenciais estão corretas.");
                System.out.println("Tente novamente.\n");
            }
        }

        // --- Inicialização dos Repositórios ---
        AccountRepository accountRepository = new AccountRepository();
        CardRepository cardRepository = new CardRepository();
        ClientRepository clientRepository = new ClientRepository();
        TransactionRepository transactionRepository = new TransactionRepository();
        CurrentAccountRepository currentAccountRepository = new CurrentAccountRepository();
        SavingsAccountRepository savingsAccountRepository = new SavingsAccountRepository();
        PixKeyRepository pixKeyRepository = new PixKeyRepository();
        PixTransactionRepository pixTransactionRepository = new PixTransactionRepository();

        // --- Inicialização dos Serviços ---
        // Passando as dependências corretas para cada construtor
        AccountService accountService = new AccountService(accountRepository);
        ClientService clientService = new ClientService(clientRepository); // Depende apenas do repositório
        // CardService agora precisa de AccountService
        CardService cardService = new CardService(cardRepository, accountService);
        TransactionService transactionService = new TransactionService(transactionRepository);
        // CurrentAccountService agora precisa de ClientService e SavingsAccountRepository
        CurrentAccountService currentAccountService = new CurrentAccountService(currentAccountRepository, clientService, savingsAccountRepository);
        // SavingsAccountService agora precisa de ClientService e CurrentAccountRepository
        SavingsAccountService savingsAccountService = new SavingsAccountService(savingsAccountRepository, clientService, currentAccountRepository);
        // PixKeyService agora precisa de AccountService
        PixKeyService pixKeyService = new PixKeyService(pixKeyRepository, accountService);
        // PixTransactionService agora precisa de AccountService e PixKeyService
        PixTransactionService pixTransactionService = new PixTransactionService(pixTransactionRepository, accountService, pixKeyService);

        ViewClient viewClient = new ViewClient(clientService);


        // --- Inicialização da InteractiveView ---
        // Passando TODOS os serviços que ela e suas sub-views podem precisar
        InteractiveView interactiveView = new InteractiveView(
                accountService,
                cardService,
                transactionService,
                currentAccountService,
                savingsAccountService,
                pixKeyService,
                pixTransactionService,
                clientService,
                viewClient

        );

        // Iniciar a aplicação
        interactiveView.startApplication();

        // Fechar o EntityManagerFactory quando a aplicação encerrar
        // Verifique se o método closeEntityManagerFactory() existe em JPAUtil
        // Se não existir, adicione-o em JPAUtil
        JPAUtil.closeEntityManagerFactory();
        sc.close(); // Fechar o scanner principal
        System.out.println("Application closed. Goodbye!");
    }
}