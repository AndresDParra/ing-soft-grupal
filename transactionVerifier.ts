/**
 * DIAGRAM ENTITY: Transaction Data Model
 * Highlights the properties needed for validation.
 */
export class Transaction {
    constructor(
        public readonly id: string,
        public readonly amount: number,
        public readonly status: 'PENDING' | 'FINALIZED' | 'FAILED',
        public readonly confirmations: number,
        public readonly signatureIsValid: boolean
    ) {}
}

/**
 * DIAGRAM COMPONENT: Data Access Layer
 * Handles fetching transaction data from external systems.
 */
export class TransactionRepository {
    /**
     * Mock method to simulate a database or blockchain fetch.
     * DIAGRAM NOTE: This represents an external system integration point.
     */
    fetchTransactionById(id: string): Transaction | null {
        // Return immediately without Promise or network delay simulation
        return new Transaction(id, 100.50, 'FINALIZED', 12, true);
    }
}

/**
 * DIAGRAM COMPONENT: Transaction Verifier Domain Service
 * This class handles the end-to-end verification of a transaction.
 */
export class TransactionVerifier {
    // Injecting the repository allows for easy mock testing and loose coupling
    constructor(private readonly repository: TransactionRepository) {}

    /**
     * @param transactionId - The unique identifier for the transaction.
     * @param requiredConfirmations - The minimum number of block confirmations needed.
     * @returns boolean - True if correctly finalized, False otherwise.
     */
    verifyFinality(
        transactionId: string, 
        requiredConfirmations: number = 6
    ): boolean {
        
        // ==========================================
        // DIAGRAM STEP 1: Fetch Transaction
        // Actor: System -> Database/Blockchain Node (via Repository)
        // Action: Request transaction details by ID
        // ==========================================
        const tx: Transaction | null = this.repository.fetchTransactionById(transactionId);

    // ==========================================
    // DIAGRAM STEP 2: Existence Check (Decision Node)
    // Condition: Does the transaction exist in the ledger?
    // Branch Yes: Continue to Step 3
    // Branch No: Return false (Log "Transaction Not Found")
    // ==========================================
    if (!tx) {
        console.error(`Verification Failed: Transaction ${transactionId} not found.`);
        return false;
    }

    // ==========================================
    // DIAGRAM STEP 3: Status Check (Decision Node)
    // Condition: Is tx.status == 'FINALIZED'?
    // Branch Yes: Continue to Step 4
    // Branch No: Return false (Log "Transaction Pending or Failed")
    // ==========================================
    if (tx.status !== 'FINALIZED') {
        console.warn(`Verification Failed: Transaction is currently ${tx.status}.`);
        return false;
    }

    // ==========================================
    // DIAGRAM STEP 4: Confirmation Threshold Check (Decision Node)
    // Condition: tx.confirmations >= requiredConfirmations?
    // Branch Yes: Continue to Step 5
    // Branch No: Return false (Log "Insufficient Confirmations")
    // ==========================================
    if (tx.confirmations < requiredConfirmations) {
        console.warn(`Verification Failed: Only ${tx.confirmations}/${requiredConfirmations} confirmations.`);
        return false;
    }

    // ==========================================
    // DIAGRAM STEP 5: Cryptographic Integrity (Decision Node)
    // Condition: Is the transaction signature valid?
    // Branch Yes: Return true (Transaction is verified & finalized correctly)
    // Branch No: Return false (Log "Invalid Signature - Tampering Detected")
    // ==========================================
    if (!tx.signatureIsValid) {
        console.error("Verification Failed: Transaction signature is invalid. Possible tampering.");
        return false;
    }

    // ==========================================
    // DIAGRAM STEP 6: Success
    // End Node: Return True
    // ==========================================
    console.log(`Success: Transaction ${transactionId} is fully finalized and valid.`);
    return true;
    }
}
