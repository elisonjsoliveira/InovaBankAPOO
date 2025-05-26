package view;

import entities.Account;
import entities.PixKey;
import services.AccountService;
import services.PixKeyService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ViewPixKey {

    private final PixKeyService pixKeyService;
    private final AccountService accountService; // Para buscar a conta associada à chave Pix
    private final Scanner scanner;

    public ViewPixKey(PixKeyService pixKeyService, AccountService accountService) {
        this.pixKeyService = pixKeyService;
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    public void managePixKeys() {
        int choice;
        do {
            System.out.println("\n==== Pix Key Management ====");
            System.out.println("1. Create Pix Key");
            System.out.println("2. View Pix Key by Value");
            System.out.println("3. View All Pix Keys by Account");
            System.out.println("4. Update Pix Key");
            System.out.println("5. Delete Pix Key");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> createPixKey();
                    case 2 -> viewPixKeyByValue();
                    case 3 -> viewAllPixKeysByAccount();
                    case 4 -> updatePixKey();
                    case 5 -> deletePixKey();
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

    private void createPixKey() {
        System.out.print("Enter Pix Key Value: ");
        String keyValue = scanner.nextLine();

        System.out.println("Select Pix Key Type:");
        for (PixKey.PixKeyType type : PixKey.PixKeyType.values()) {
            System.out.println((type.ordinal() + 1) + ". " + type.name());
        }
        System.out.print("Choose type: ");
        PixKey.PixKeyType keyType;
        try {
            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            keyType = PixKey.PixKeyType.values()[typeChoice - 1]; // Convert int choice to Enum
        } catch (InputMismatchException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid Pix Key Type choice. Pix Key not created.");
            scanner.nextLine(); // Consume invalid input if InputMismatchException
            return;
        }

        System.out.print("Enter Account Number to link Pix Key: ");
        String accountNumber = scanner.nextLine();

        Optional<Account> accountOpt = accountService.getByAccountNumber(accountNumber);

        if (accountOpt.isPresent()) {
            PixKey pixKey = new PixKey(keyValue, keyType, accountOpt.get());
            pixKeyService.create(pixKey); // Service already prints success/failure messages
        } else {
            System.out.println("Account not found with number: " + accountNumber + ". Pix Key not created.");
        }
    }

    private void viewPixKeyByValue() {
        System.out.print("Enter Pix Key Value to view: ");
        String keyValue = scanner.nextLine();
        pixKeyService.findByKeyValue(keyValue).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Pix Key not found with value: " + keyValue + "."));
    }

    private void viewAllPixKeysByAccount() {
        System.out.print("Enter Account ID to view all Pix Keys: ");
        long accountId;
        try {
            accountId = scanner.nextLong();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.out.println("Invalid Account ID format. Please enter a number.");
            scanner.nextLine(); // Consume invalid input
            return;
        }

        System.out.println("\n--- All Pix Keys for Account ID: " + accountId + " ---");
        List<PixKey> pixKeys = pixKeyService.findAllByAccount(accountId);
        if (pixKeys.isEmpty()) {
            System.out.println("No Pix Keys registered for this account.");
        } else {
            pixKeys.forEach(System.out::println);
        }
        System.out.println("------------------------------------------");
    }

    private void updatePixKey() {
        System.out.print("Enter Pix Key Value to update: ");
        String keyValueToUpdate = scanner.nextLine();

        pixKeyService.findByKeyValue(keyValueToUpdate).ifPresentOrElse(pixKey -> {
            System.out.println("Pix Key found. Enter new details (leave blank to keep current):");

            // Permite atualizar o tipo da chave ou a conta associada, se necessário.
            // Geralmente, o valor da chave (keyValue) não muda.
            System.out.println("Current Key Type: " + pixKey.getKeyType().name());
            System.out.println("Select new Pix Key Type (or leave blank to keep current):");
            for (PixKey.PixKeyType type : PixKey.PixKeyType.values()) {
                System.out.println((type.ordinal() + 1) + ". " + type.name());
            }
            System.out.print("Choose new type: ");
            String typeChoiceStr = scanner.nextLine();
            if (!typeChoiceStr.trim().isEmpty()) {
                try {
                    int typeChoice = Integer.parseInt(typeChoiceStr);
                    pixKey.setKeyType(PixKey.PixKeyType.values()[typeChoice - 1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid Pix Key Type choice. Keeping current type.");
                }
            }

            System.out.print("Enter new Account Number to link Pix Key (or leave blank to keep current) [" + pixKey.getAccount().getAccountNumber() + "]: ");
            String newAccountNumber = scanner.nextLine();
            if (!newAccountNumber.trim().isEmpty()) {
                Optional<Account> newAccountOpt = accountService.getByAccountNumber(newAccountNumber);
                if (newAccountOpt.isPresent()) {
                    pixKey.setAccount(newAccountOpt.get());
                } else {
                    System.out.println("New Account not found with number: " + newAccountNumber + ". Keeping current account.");
                }
            }

            pixKeyService.update(pixKey); // Service prints feedback
        }, () -> System.out.println("Pix Key not found with value: " + keyValueToUpdate + ". Update failed."));
    }

    private void deletePixKey() {
        System.out.print("Enter Pix Key Value to delete: ");
        String keyValueToDelete = scanner.nextLine();
        pixKeyService.delete(keyValueToDelete); // Service prints feedback
        System.out.println("Attempted deletion for Pix Key: " + keyValueToDelete + ".");
    }
}