package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LibraryUI extends JFrame {

    private final LibrarySystem biblioteca;
    private final CardLayout cardLayout;
    private final JPanel cardsPanel;
    private final JLabel statusLabel;

    public LibraryUI(LibrarySystem biblioteca) {
        this.biblioteca = biblioteca;
        this.cardLayout = new CardLayout();
        this.cardsPanel = new JPanel(cardLayout);
        this.statusLabel = new JLabel("Status: Welcome to the Library System", SwingConstants.CENTER);

        setTitle("University Library System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setup Main Layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(statusLabel, BorderLayout.NORTH);
        contentPane.add(cardsPanel, BorderLayout.CENTER);

        // Create and add screens to the CardLayout
        cardsPanel.add(createMenuPanel(), "MENU");
        cardsPanel.add(createCreateUserPanel(), "CREATE_USER");
        cardsPanel.add(createLendBookPanel(), "LEND_BOOK");
        cardsPanel.add(createReturnBookPanel(), "RETURN_BOOK");

        cardLayout.show(cardsPanel, "MENU");
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnCreateUser = new JButton("Create User");
        JButton btnLendBook = new JButton("Lend Book");
        JButton btnReturnBook = new JButton("Return Book & Pay Fine");

        btnCreateUser.addActionListener(e -> cardLayout.show(cardsPanel, "CREATE_USER"));
        btnLendBook.addActionListener(e -> cardLayout.show(cardsPanel, "LEND_BOOK"));
        btnReturnBook.addActionListener(e -> cardLayout.show(cardsPanel, "RETURN_BOOK"));

        panel.add(btnCreateUser);
        panel.add(btnLendBook);
        panel.add(btnReturnBook);

        return panel;
    }

    private JPanel createCreateUserPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtUid = new JTextField();
        JTextField txtName = new JTextField();

        panel.add(new JLabel("User ID (e.g. U001):"));
        panel.add(txtUid);
        panel.add(new JLabel("User Name:"));
        panel.add(txtName);

        JButton btnSubmit = new JButton("Submit");
        JButton btnCancel = new JButton("Cancel");

        btnSubmit.addActionListener(e -> {
            String uid = txtUid.getText();
            String name = txtName.getText();
            biblioteca.registrarUsuario(new Usuario(uid, name));
            statusLabel.setText("Status: User " + name + " created.");
            txtUid.setText(""); txtName.setText("");
            cardLayout.show(cardsPanel, "MENU");
        });

        btnCancel.addActionListener(e -> cardLayout.show(cardsPanel, "MENU"));

        panel.add(btnSubmit);
        panel.add(btnCancel);

        return panel;
    }

    private JPanel createLendBookPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtLoanId = new JTextField();
        JTextField txtUserId = new JTextField();
        JTextField txtBookId = new JTextField();

        panel.add(new JLabel("Loan ID (e.g. L001):"));
        panel.add(txtLoanId);
        panel.add(new JLabel("User ID:"));
        panel.add(txtUserId);
        panel.add(new JLabel("Book ID (E100, E101):"));
        panel.add(txtBookId);

        JButton btnSubmit = new JButton("Submit");
        JButton btnCancel = new JButton("Cancel");

        btnSubmit.addActionListener(e -> {
            Prestamo result = biblioteca.prestarLibro(txtLoanId.getText(), txtUserId.getText(), txtBookId.getText());
            statusLabel.setText(result != null ? "Status: Book lent successfully." : "Status: Failed to lend book.");
            txtLoanId.setText(""); txtUserId.setText(""); txtBookId.setText("");
            cardLayout.show(cardsPanel, "MENU");
        });

        btnCancel.addActionListener(e -> cardLayout.show(cardsPanel, "MENU"));

        panel.add(btnSubmit);
        panel.add(btnCancel);

        return panel;
    }

    private JPanel createReturnBookPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtLoanId = new JTextField();
        JTextField txtDaysLate = new JTextField("0");
        JTextField txtTxId = new JTextField();

        panel.add(new JLabel("Loan ID to Return:"));
        panel.add(txtLoanId);
        panel.add(new JLabel("Days Late:"));
        panel.add(txtDaysLate);
        panel.add(new JLabel("Payment Tx ID (if late):"));
        panel.add(txtTxId);

        JButton btnSubmit = new JButton("Submit");
        JButton btnCancel = new JButton("Cancel");

        btnSubmit.addActionListener(e -> {
            String loanId = txtLoanId.getText();
            int daysLate = 0;
            try {
                daysLate = Integer.parseInt(txtDaysLate.getText());
            } catch (NumberFormatException ex) {
                // Ignore parsing errors, assume 0
            }
            
            String txId = daysLate > 0 && !txtTxId.getText().isEmpty() ? txtTxId.getText() : null;
            biblioteca.devolverYCalcularMulta(loanId, daysLate, txId);
            
            statusLabel.setText("Status: Return process complete. Check console.");
            txtLoanId.setText(""); txtDaysLate.setText("0"); txtTxId.setText("");
            cardLayout.show(cardsPanel, "MENU");
        });

        btnCancel.addActionListener(e -> cardLayout.show(cardsPanel, "MENU"));

        panel.add(btnSubmit);
        panel.add(btnCancel);

        return panel;
    }

    public static void main(String[] args) {
        // 1. Initialize existing Java core
        TransactionRepository repo = new TransactionRepository();
        TransactionVerifier verifier = new TransactionVerifier(repo);
        LibrarySystem biblioteca = new LibrarySystem(verifier);

        // Pre-register books
        biblioteca.registrarEjemplar(new Ejemplar("E100", "Design Patterns", true));
        biblioteca.registrarEjemplar(new Ejemplar("E101", "Clean Code", true));

        // 2. Launch the Swing UI
        SwingUtilities.invokeLater(() -> {
            LibraryUI ui = new LibraryUI(biblioteca);
            ui.setVisible(true);
        });
    }
}

