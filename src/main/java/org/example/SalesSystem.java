package org.example;

import org.example.TransactionRepository;
import org.example.TransactionVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Client {
    public final String id;
    public final String name;
    public final String email;

    public Client(String id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }
}

class Employee {
    public final String id;
    public final String name;
    public final String role;

    public Employee(String id, String name, String role) {
        this.id = id; this.name = name; this.role = role;
    }
}

class Product {
    public final String id;
    public final String name;
    public final double price;
    public int stock;

    public Product(String id, String name, double price, int stock) {
        this.id = id; this.name = name; this.price = price; this.stock = stock;
    }
}

enum SaleStatus {
    PENDING, COMPLETED, FAILED
}

class Sale {
    public SaleStatus status = SaleStatus.PENDING;
    public final double totalAmount;
    public final String saleId;
    public final Client client;
    public final Employee employee;
    public final List<Product> products;
    public final String transactionId;

    public Sale(String saleId, Client client, Employee employee, List<Product> products, String transactionId) {
        this.saleId = saleId;
        this.client = client;
        this.employee = employee;
        this.products = products;
        this.transactionId = transactionId;
        
        double sum = 0;
        for (Product p : products) {
            sum += p.price;
        }
        this.totalAmount = sum;
    }
}

public class SalesSystem {
    private Map<String, Client> clients = new HashMap<>();
    private Map<String, Employee> employees = new HashMap<>();
    private Map<String, Product> inventory = new HashMap<>();
    private Map<String, Sale> salesHistory = new HashMap<>();
    
    private TransactionVerifier txVerifier;

    public SalesSystem(TransactionVerifier txVerifier) {
        this.txVerifier = txVerifier;
    }

    public void addClient(Client client) { clients.put(client.id, client); }
    public void addEmployee(Employee employee) { employees.put(employee.id, employee); }
    public void addProduct(Product product) { inventory.put(product.id, product); }

    public Sale processSale(String saleId, String clientId, String employeeId, List<String> productIds, String transactionId) {
        System.out.println("\n--- Initiating Sale: " + saleId + " ---");
        
        Client client = clients.get(clientId);
        Employee employee = employees.get(employeeId);
        
        if (client == null || employee == null) {
            System.err.println("Sale Failed: Missing Client or Employee records.");
            return null;
        }

        List<Product> productsToBuy = new ArrayList<>();
        for (String pid : productIds) {
            Product product = inventory.get(pid);
            if (product == null || product.stock <= 0) {
                System.err.println("Sale Failed: Product " + pid + " is out of stock or does not exist.");
                return null;
            }
            productsToBuy.add(product);
        }

        Sale sale = new Sale(saleId, client, employee, productsToBuy, transactionId);
        salesHistory.put(saleId, sale);

        System.out.println("Total Amount: $" + String.format("%.2f", sale.totalAmount) + " | Cashier: " + employee.name + " | Customer: " + client.name);

        boolean isPaymentValid = this.txVerifier.verifyFinality(transactionId);

        if (isPaymentValid) {
            sale.status = SaleStatus.COMPLETED;
            for (Product p : productsToBuy) {
                p.stock -= 1;
            }
            System.out.println("Sale " + saleId + " completed successfully!");
        } else {
            sale.status = SaleStatus.FAILED;
            System.err.println("Sale " + saleId + " aborted: Payment verification failed.");
        }

        return sale;
    }

    // ==========================================
    // DEMONSTRATION SCRIPT
    // ==========================================
    public static void main(String[] args) {
        TransactionRepository repo = new TransactionRepository();
        TransactionVerifier verifier = new TransactionVerifier(repo);
        SalesSystem store = new SalesSystem(verifier);

        store.addClient(new Client("C001", "Alice Johnson", "alice@example.com"));
        store.addEmployee(new Employee("E001", "Bob Smith", "Cashier"));
        store.addProduct(new Product("P100", "Laptop", 1200.00, 5));
        store.addProduct(new Product("P101", "Wireless Mouse", 25.00, 20));

        store.processSale("SALE-001", "C001", "E001", Arrays.asList("P100", "P101"), "TXN-ABC");
    }
}

