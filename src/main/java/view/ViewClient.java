package view;

import entities.Client;
import services.ClientService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ViewClient {

    private final ClientService clientService;
    private final Scanner scanner;

    public ViewClient(ClientService clientService) {
        this.clientService = clientService;
        this.scanner = new Scanner(System.in);
    }

    public void manageClients(boolean adm) {
        int choice;
        do {
            System.out.println("\n==== Client Management ====");
            System.out.println("1. Create Client");
            System.out.println("2. View Client by CPF");
            System.out.println("3. Update Client");
            System.out.println("4. Delete Client");
            if(adm){
                System.out.println("5. View All Clients");
            }
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consumir a nova linha

                switch (choice) {
                    case 1 -> createClient();
                    case 2 -> viewClientByCpf();
                    case 3 -> updateClient();
                    case 4 -> deleteClient();
                    case 5 -> viewAllClients();
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

    public void createClient() {
        System.out.print("Enter client name: ");
        String name = scanner.nextLine();
        System.out.print("Enter client CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Enter client phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter client email: ");
        String email = scanner.nextLine();
        LocalDate birthDate;
        // NOVO: Solicitar a senha na criação do cliente
        System.out.print("Enter client password: ");
        String password = scanner.nextLine();


        try {
            System.out.print("Enter client birth date (YYYY-MM-DD): ");
            String birthDateStr = scanner.nextLine();
            birthDate = LocalDate.parse(birthDateStr);

            // NOVO: Usar o construtor do Client que aceita a senha
            Client client = new Client(birthDate, cpf, email, name, password, phone);
            clientService.create(client);
            // O clientService.create já imprime a mensagem de sucesso ou falha (CPF já existe, etc.)
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use ISO format (YYYY-MM-DD). Client not created.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during client creation: " + e.getMessage());
            // e.printStackTrace(); // Apenas para depuração
        }
    }

    private void viewClientByCpf() {
        System.out.print("Enter client CPF: ");
        String cpf = scanner.nextLine();
        clientService.getByCPF(cpf).ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Client not found for CPF: " + cpf + "."));
    }

    private void viewAllClients() {
        System.out.println("\n--- All Clients ---");
        List<Client> clients = clientService.getAll();
        if (clients.isEmpty()) {
            System.out.println("No clients registered.");
        } else {
            clients.forEach(System.out::println);
        }
        System.out.println("-------------------");
    }

    private void updateClient() {
        System.out.print("Enter client CPF to update: ");
        String cpfToUpdate = scanner.nextLine();

        clientService.getByCPF(cpfToUpdate).ifPresentOrElse(client -> {
            System.out.println("Client found. Enter new details (leave blank to keep current):");

            System.out.print("Enter new client name [" + client.getName() + "]: ");
            String newName = scanner.nextLine();
            if (!newName.trim().isEmpty()) {
                client.setName(newName);
            }

            System.out.print("Enter new client phone [" + (client.getPhone() != null ? client.getPhone() : "") + "]: ");
            String newPhone = scanner.nextLine();
            if (!newPhone.trim().isEmpty()) {
                client.setPhone(newPhone);
            }

            System.out.print("Enter new client email [" + (client.getEmail() != null ? client.getEmail() : "") + "]: ");
            String newEmail = scanner.nextLine();
            if (!newEmail.trim().isEmpty()) {
                client.setEmail(newEmail);
            }

            // Opcional: Atualizar data de nascimento
            System.out.print("Enter new client birth date (YYYY-MM-DD) [" + client.getBirthDate() + "]: ");
            String newBirthDateStr = scanner.nextLine();
            if (!newBirthDateStr.trim().isEmpty()) {
                try {
                    LocalDate newBirthDate = LocalDate.parse(newBirthDateStr);
                    client.setBirthDate(newBirthDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Keeping current birth date.");
                }
            }

            // NOVO: Permitir atualização da senha
            System.out.print("Enter new client password (leave blank to keep current): ");
            String newPassword = scanner.nextLine();
            if (!newPassword.trim().isEmpty()) {
                client.setPassword(newPassword);
            } else {
                // Se o usuário não digitar nova senha, garantir que a senha existente não seja nula.
                // O service já valida password.trim().isEmpty()
                if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
                    System.out.println("Warning: Current password is empty and no new password provided. Update might fail if password is required.");
                }
            }


            clientService.update(client);
            // O clientService.update já imprime a mensagem de sucesso ou falha.
        }, () -> System.out.println("Client not found for CPF: " + cpfToUpdate + ". Update failed."));
    }

    private void deleteClient() {
        System.out.print("Enter client CPF to delete: ");
        String cpfToDelete = scanner.nextLine();
        clientService.delete(cpfToDelete);
        // O clientService.delete já imprime se não encontrar.
        System.out.println("Attempted client deletion for CPF: " + cpfToDelete + ".");
    }
}