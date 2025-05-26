package view;

import entities.Account;
import entities.Card;
import services.AccountService;
import services.CardService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ViewCard {

    private final CardService cardService;
    private final Scanner scanner;
    private final AccountService accountService;

    public ViewCard(CardService cardService, AccountService accountService) {
        this.cardService = cardService;
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    public void manageCards(boolean adm) {
        int choice;
        do {
            System.out.println("\n==== Card Management ====");
            System.out.println("1. Create Card");
            System.out.println("2. View Card");

            System.out.println("3. Update Card");
            System.out.println("4. Delete Card");
            if(adm){
                System.out.println("5. View All Cards");
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consumir a nova linha

                switch (choice) {
                    case 1 -> createCard();
                    case 2 -> viewCard();
                    case 3 -> updateCard();
                    case 4 -> deleteCard();
                    case 5 -> viewAllCards();
                    case 0 -> System.out.println("Returning to main menu.");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number for the choice.");
                scanner.nextLine(); // Consumir a entrada inválida para evitar loop infinito
                choice = -1; // Define choice para continuar o loop
            }

        } while (choice != 0);
    }

    private void createCard() {
        System.out.print("Enter card number: ");
        String cardNumber = scanner.nextLine();
        LocalDate validity;
        int cvv;
        String cardType;
        BigDecimal creditLimit = BigDecimal.ZERO; // Default para cartões de débito
        String accountNumber;

        try {
            System.out.print("Enter card validity (YYYY-MM-DD): ");
            validity = LocalDate.parse(scanner.nextLine());
            System.out.print("Enter CVV: ");
            cvv = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

            System.out.println("Select card type:");
            System.out.println("1. Debit");
            System.out.println("2. Credit");
            System.out.println("3. Both (Debit/Credit)");
            System.out.print("Choose type: ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // Consumir a nova linha

            switch (typeChoice) {
                case 1:
                    cardType = "DEBIT";
                    creditLimit = BigDecimal.ZERO; // Cartão de débito não tem limite de crédito
                    break;
                case 2:
                    cardType = "CREDIT";
                    System.out.print("Enter credit limit: ");
                    creditLimit = new BigDecimal(scanner.nextLine());
                    break;
                case 3:
                    cardType = "BOTH";
                    System.out.print("Enter credit limit: "); // Cartão múltiplo precisa de limite de crédito
                    creditLimit = new BigDecimal(scanner.nextLine());
                    break;
                default:
                    System.out.println("Invalid card type choice. Defaulting to DEBIT.");
                    cardType = "DEBIT";
                    creditLimit = BigDecimal.ZERO;
                    break;
            }

            System.out.print("Enter account number associated with the card: ");
            accountNumber = scanner.nextLine();

            Optional<Account> account = accountService.getByAccountNumber(accountNumber);

            if (account.isPresent()) {
                Card card = new Card(cardNumber, validity, cvv, cardType, creditLimit, account.get());
                cardService.create(card);
            } else {
                System.out.println("Account not found. Card not created.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        } catch (NumberFormatException | InputMismatchException e) {
            System.out.println("Invalid number format for CVV or Credit Limit. Please enter a valid number.");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during card creation: " + e.getMessage());
            e.printStackTrace(); // Apenas para depuração
        }
    }

    private void viewCard() {
        System.out.print("Enter card number: ");
        String cardNumber = scanner.nextLine();
        cardService.getByCardNumber(cardNumber).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Card not found."));
    }

    private void viewAllCards() {
        System.out.println("\n--- All Cards ---");
        List<Card> cards = cardService.getAll();
        if (cards.isEmpty()) {
            System.out.println("No cards registered.");
        } else {
            cards.forEach(System.out::println);
        }
        System.out.println("-----------------");
    }

    private void updateCard() {
        System.out.print("Enter card number of the card to update: ");
        String cardNumberToUpdate = scanner.nextLine();

        cardService.getByCardNumber(cardNumberToUpdate).ifPresentOrElse(card -> {
            System.out.println("Card found. Enter new details (leave blank to keep current):");

            LocalDate newValidity = null;
            System.out.print("Enter new validity (YYYY-MM-DD) [" + card.getValidity() + "]: ");
            String newValidityStr = scanner.nextLine();
            if (!newValidityStr.trim().isEmpty()) {
                try {
                    newValidity = LocalDate.parse(newValidityStr);
                    card.setValidity(newValidity);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Keeping current validity.");
                }
            }

            // Não pedimos para alterar o tipo de cartão nem o CVV aqui para simplificar e por segurança.
            // Se precisar, essa lógica pode ser adicionada.

            // Apenas pedir novo limite de crédito se o tipo do cartão atual for CRÉDITO ou AMBOS
            if ("CREDIT".equalsIgnoreCase(card.getCardType()) || "BOTH".equalsIgnoreCase(card.getCardType())) {
                BigDecimal newCreditLimit = null;
                System.out.print("Enter new credit limit [" + card.getCreditLimit() + "]: ");
                String newCreditLimitStr = scanner.nextLine();
                if (!newCreditLimitStr.trim().isEmpty()) {
                    try {
                        newCreditLimit = new BigDecimal(newCreditLimitStr);
                        card.setCreditLimit(newCreditLimit);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format for credit limit. Keeping current limit.");
                    }
                }
            } else {
                // Cartão de débito puro, o limite de crédito é sempre zero
                card.setCreditLimit(BigDecimal.ZERO);
            }

            cardService.update(card);
            System.out.println("Card updated successfully.");
        }, () -> System.out.println("Card not found. Update failed."));
    }

    private void deleteCard() {
        System.out.print("Enter card number of the card to delete: ");
        String cardNumberToDelete = scanner.nextLine();
        cardService.delete(cardNumberToDelete);
        System.out.println("Attempted card deletion for number: " + cardNumberToDelete + ".");
    }
}