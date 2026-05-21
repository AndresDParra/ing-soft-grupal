package org.example;

/**
 * DIAGRAM COMPONENT: Data Access Layer
 */
public class TransactionRepository {
    public Transaction fetchTransactionById(String id) {
        // Return immediately without network delay simulation
        return new Transaction(id, 100.50, TransactionStatus.FINALIZED, 12, true);
    }
}
