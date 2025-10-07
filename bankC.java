import java.util.*;

/**
 * BankAccount class: Manages balance, deposits, withdrawals, and transfers.
 *

class BankAccount {
    private double balance;
    private String accountNumber;
    // History now stores Transaction objects
    private List<Transaction> transactionHistory;
    private static int accountCounter = 10;

    // Constructor
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
        this.accountNumber = "ACC-" + accountCounter++;
        this.transactionHistory = new ArrayList<>();
    }

    // --- Core Financial Methods ---

    public void deposit(double amount) {
        // Bug: No check for amount > 0
        balance += amount;

        // Log as a transaction where "from" is null (external source)
        logTransaction(new Transaction(amount, null, this.accountNumber));
        System.out.println("ACC-" + accountNumber + ": Deposited " + amount + ". New Balance: " + balance);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Withdrawal amount must be positive.");
            return;
        }

        // Bug: Overdraft check removed. Balance may go negative.
        balance -= amount;

        // Log as a transaction where "to" is null (external destination)
        logTransaction(new Transaction(amount, this.accountNumber, null));
        System.out.println("ACC-" + accountNumber + ": Withdrew " + amount + ". New Balance: " + balance);
    }

    /**
     * Seeded Bugs: Allows transfer to same account (Bank class handles it), ignores rounding,
     * and seeds the 'to' missing history bug on the transfer-in side.
     */
    public void transfer(BankAccount target, double amount) {
        if (target == null) {
            System.out.println("Error: Target account is null.");
            return;
        }
        if (amount <= 0) {
            System.out.println("Error: Transfer amount must be positive.");
            return;
        }

        // 1. Withdraw from source (no overdraft check)
        this.balance -= amount;

        // 2. Deposit into target (no rounding correction)
        target.balance += amount;

        // 3. Log transactions on both sides
        // Transfer Out: Logs both FROM and TO correctly
        this.logTransaction(new Transaction(amount, this.accountNumber, ""));

        target.logTransaction(new Transaction(amount, this.accountNumber, ""));

        System.out.println("ACC-" + this.accountNumber + " transferred " + amount + " to ACC-" + target.accountNumber + ".");
    }

    // --- Utility Methods ---

    private void logTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    /**
     * New method to verify a transactions histor with another account from the account's perspective.
     * either a batch search for transactions with a specific account
     *
     */
    public boolean searchTransactionHistory(String accNumber){

        //general "did you transact with this account

        for (int i = 0; i < transactionHistory.size(); i++) {
            if (Objects.equals(transactionHistory.get(i).getTo(), accNumber)){
                // break out of the loop because we've found at least 1 match
                return true;
            }

        }
        return false;
    }

    public List<Transaction> FindTransactionInHistory(String accNumber, Double amount){

        List<Transaction> foundTransactions = new ArrayList<>();
        //general "did you transact with this account
        // it will find all transactions that match that user and that amount

        for (int i = 0; i < transactionHistory.size(); i++) {

            if ((Objects.equals(transactionHistory.get(i).getTo(), accNumber))  & (transactionHistory.get(i).getAmount()== amount)){

                foundTransactions.add(transactionHistory.get(i));
                // break out of the loop because we've found at least 1 match

            }

        }

        return foundTransactions;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void printHistory() {
        System.out.println("\n=== History for ACC-" + accountNumber + " (Balance: " + balance + ") ===");
        for (Transaction t : transactionHistory) {
            System.out.print(t.toString());
            // Demonstrate the verification bug
        }
        System.out.println("======================================");
    }
}
// -----------------------------------------------------------------------------
// Bank class: Holds accounts and centralizes account lookup.
class Bank {
    private String bankName;
    private Map<String, BankAccount> accounts;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
        System.out.println(bankName + " initialized.");
    }

    public void addAccount(BankAccount account) {
        if (account == null) return;
        accounts.put(account.getAccountNumber(), account);
        System.out.println("Added: " + account.getAccountNumber());
    }

    // Centralized lookup method
    public BankAccount getAccount(String accountNumber) {
        BankAccount account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account;
    }

    // Centralized transfer logic using the account's internal transfer method
    public void processTransfer(String fromAccountNumber, String toAccountNumber, double amount) {
        try {
            BankAccount fromAccount = getAccount(fromAccountNumber);
            BankAccount toAccount = getAccount(toAccountNumber);

            // Bug: Allows transfer to the same account (the check is deliberately removed)
            /*
            if (fromAccountNumber.equals(toAccountNumber)) {
                System.out.println("Error: Cannot transfer to the same account.");
                return;
            }
            */

            fromAccount.transfer(toAccount, amount);

        } catch (IllegalArgumentException e) {
            System.out.println("Transfer Failed: " + e.getMessage());
        }
    }
}


// --- New Transaction Class ---
class Transaction {
    private double amount;
    private String to;
    private String from;

    public Transaction(double amount, String from, String to) {
        this.amount = amount;
        this.from = from;
        // Bug Seed: The 'to' field is deliberately set to null/missing for a certain type of transaction.
        this.to = to;
    }

    public double getAmount() {
        return amount;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }


    @Override
    public String toString() {
        // Simple string representation for printing history
        return "Transaction: " +
                (from != null ? "FROM: " + from : "") +
                (to != null ? " TO: " + to : "") +
                " | Amount: " + amount;
    }
}

// -----------------------------------------------------------------------------
// Main class for demonstration
public class bankC {
    public static void main(String[] args) {
        System.out.println("=== Bug-Seeded Bank Demo ===");

        // Setup
        Bank centralBank = new Bank("My Simple Bank");
        // Initial balance can be negative (Seeded Bug)
        BankAccount alice = new BankAccount(-100.50);
        BankAccount bob = new BankAccount(500.00);

        centralBank.addAccount(alice);
        centralBank.addAccount(bob);

        System.out.println("\n--- Initial Balances ---");
        System.out.println("Alice's Account (" + alice.getAccountNumber() + "): " + alice.getBalance());
        System.out.println("Bob's Account (" + bob.getAccountNumber() + "): " + bob.getBalance());


        // 1. Test Deposit Bug (Allows negative deposit)
        System.out.println("\n--- Testing Deposit Bug (Negative Amount) ---");
        alice.deposit(-50.00);
        System.out.println("Alice's Balance: " + alice.getBalance());


        // 2. Test Withdrawal Bug (Overdraft not checked)
        System.out.println("\n--- Testing Withdrawal Bug (Overdraft) ---");
        bob.withdraw(600.00); // Should fail, but will succeed and go negative
        System.out.println("Bob's Balance: " + bob.getBalance());


        // 3. Test Transfer Bugs (No Overdraft Check, Float Drift, Transfer to Self)
        System.out.println("\n--- Testing Transfer Bugs ---");
        // Transfer to self (Seeded Bug)
        centralBank.processTransfer(alice.getAccountNumber(), alice.getAccountNumber(), 5.0);
        // Transfer with potential float drift
        centralBank.processTransfer(alice.getAccountNumber(), bob.getAccountNumber(), 10.33);


        // Check final balances and history
        System.out.println("\n--- Final Balances ---");
        System.out.println("Alice's Account (" + alice.getAccountNumber() + "): " + alice.getBalance());
        System.out.println("Bob's Account (" + bob.getAccountNumber() + "): " + bob.getBalance());

        // print out history for each
        alice.printHistory();
        bob.printHistory();
    }
}