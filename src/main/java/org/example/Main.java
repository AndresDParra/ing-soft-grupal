package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Starting University Library System ===");

        TransactionRepository repo = new TransactionRepository();
        TransactionVerifier verifier = new TransactionVerifier(repo);
        LibrarySystem biblioteca = new LibrarySystem(verifier);

        // Pre-register some books so we have inventory
        biblioteca.registrarEjemplar(new Ejemplar("E100", "Design Patterns", true));
        biblioteca.registrarEjemplar(new Ejemplar("E101", "Clean Code", true));

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Create a Library User");
            System.out.println("2. Lend a Book");
            System.out.println("3. Return a Book & Pay Fine");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter new User ID (e.g. U001): ");
                    String uid = scanner.nextLine();
                    System.out.print("Enter User Name: ");
                    String name = scanner.nextLine();
                    biblioteca.registrarUsuario(new Usuario(uid, name));
                    System.out.println("User " + name + " created successfully.");
                    break;
                
                case "2":
                    System.out.print("Enter Loan ID (e.g. L001): ");
                    String loanId = scanner.nextLine();
                    System.out.print("Enter User ID: ");
                    String userId = scanner.nextLine();
                    System.out.print("Enter Book ID (Available: E100, E101): ");
                    String bookId = scanner.nextLine();
                    biblioteca.prestarLibro(loanId, userId, bookId);
                    break;
                
                case "3":
                    System.out.print("Enter Loan ID to return: ");
                    String retLoanId = scanner.nextLine();
                    System.out.print("Enter days late (0 if on time): ");
                    int daysLate = Integer.parseInt(scanner.nextLine());
                    
                    String txId = null;
                    if (daysLate > 0) {
                        System.out.print("Fine applied. Enter Transaction ID to pay (use 'FAIL' to simulate failed payment, e.g. TXN-123): ");
                        txId = scanner.nextLine();
                    }
                    
                    biblioteca.devolverYCalcularMulta(retLoanId, daysLate, txId);
                    break;
                
                case "4":
                    running = false;
                    System.out.println("Exiting System...");
                    break;
                
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        
        scanner.close();
        System.out.println("\n=== System Shut Down ===");
    }
}