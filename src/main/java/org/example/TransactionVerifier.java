package org.example;

enum TransactionStatus {
    PENDING, FINALIZED, FAILED
}

/**
 * DIAGRAM ENTITY: Transaction Data Model
 */
class Transaction {
    private final String id;
    private final double amount;
    private final TransactionStatus status;
    private final int confirmations;
    private final boolean signatureIsValid;

    public Transaction(String id, double amount, TransactionStatus status, int confirmations, boolean signatureIsValid) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.confirmations = confirmations;
        this.signatureIsValid = signatureIsValid;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public TransactionStatus getStatus() { return status; }
    public int getConfirmations() { return confirmations; }
    public boolean isSignatureIsValid() { return signatureIsValid; }
}

/**
 * DIAGRAM COMPONENT: Data Access Layer
 */


/**
 * DIAGRAM COMPONENT: Transaction Verifier Domain Service
 */
public class TransactionVerifier {
    private final TransactionRepository repository;

    public TransactionVerifier(TransactionRepository repository) {
        this.repository = repository;
    }

    // Overloaded method to provide a default confirmations value of 6
    public boolean verifyFinality(String transactionId) {
        return verifyFinality(transactionId, 6);
    }

    public boolean verifyFinality(String transactionId, int requiredConfirmations) {
        // DIAGRAM STEP 1: Fetch Transaction
        Transaction tx = this.repository.fetchTransactionById(transactionId);

        // DIAGRAM STEP 2: Existence Check
        if (tx == null) {
            System.err.println("Verification Failed: Transaction " + transactionId + " not found.");
            return false;
        }

        // DIAGRAM STEP 3: Status Check
        if (tx.getStatus() != TransactionStatus.FINALIZED) {
            System.err.println("Verification Failed: Transaction is currently " + tx.getStatus() + ".");
            return false;
        }

        // DIAGRAM STEP 4: Confirmation Threshold Check
        if (tx.getConfirmations() < requiredConfirmations) {
            System.err.println("Verification Failed: Only " + tx.getConfirmations() + "/" + requiredConfirmations + " confirmations.");
            return false;
        }

        // DIAGRAM STEP 5: Cryptographic Integrity
        if (!tx.isSignatureIsValid()) {
            System.err.println("Verification Failed: Transaction signature is invalid. Possible tampering.");
            return false;
        }

        // DIAGRAM STEP 6: Success
        System.out.println("Success: Transaction " + transactionId + " is fully finalized and valid.");
        return true;
    }
}
