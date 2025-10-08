import java.util.*;

/**
 * BankAccount class: Manages balance, deposits, withdrawals, and transfers.
 *
*/

class BankAccount {
    private double balance;
    private String accountNumber;
    // History now stores Transaction objects
    private List<Transaction> transactionHistory;
//    private static int accountCounter = 10;

    // Constructor
    // Constructor for our bank account, initialized with a balance
    // stores
    // - a generated account # corresponding to the total # of accounts in a bank + a "ACC" string
    // - balance
    // - transaction history ( list of type transaction )
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;

//        this.accountNumber = "ACC-" + accountCounter;
        this.transactionHistory = new ArrayList<>();
    }

    // --- Core Financial Methods ---

    // Deposit function: used to put money into accounts for general deposits and transfers
    // Params: amount to deposit into this account
    // return: void
    public void deposit(double amount) {
        // Bug: No check for amount > 0
        balance += amount;

        // Log as a transaction where "from" is null (external source)
        logTransaction(new Transaction(amount, null, this.accountNumber));
        System.out.println("ACC-" + accountNumber + ": Deposited " + amount + ". New Balance: " + balance);
    }


    // withdraw function: used to take money out of ones account for transfers and just to get cash
    // params: amount to with draw from this account
    // return: void
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Withdrawal amount must be positive.");
            return;
        }

        balance -= amount;

        // Log as a transaction
        logTransaction(new Transaction(amount, this.accountNumber, null));
        System.out.println("ACC-" + accountNumber + ": Withdrew " + amount + ". New Balance: " + balance);
    }

    // transfer money to and from accounts
    // params: a target account & amount to transfer
    // return: void
    public void transfer(BankAccount target, double amount) {
        if (target == null) {
            System.out.println("Error: Target account is null.");
            return;
        }
        if (amount <= 0) {
            System.out.println("Error: Transfer amount must be positive.");
            return;
        }

        // 1. Withdraw from source
        this.balance -= amount;

        // 2. Deposit into target
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
                // collect all transactions found with that amount and user

            }

        }

        return foundTransactions;
    }


    // Updated number: used to update the account number so that it coressponds with this bank
    // params: a number which will be the # of accounts in the bank
    // return: void
    public void updateNumber (int number){

        this.accountNumber = "ACC-0" + number++;
    }


    // function to get the balance for this account
    // params: none
    // return: double == this accounts balance
    public double getBalance() {
        return balance;
    }


    // function to get the account number for this account
    // params: none
    // return: String == this accounts number
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

// Bank class: Holds accounts & facilitates transfers
class Bank {
    private String bankName;
    private Map<String, BankAccount> accounts;

//    private int accountCounter = accounts.size();

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new HashMap<>();
        System.out.println(bankName + " initialized.");
    }

    public void addAccount(BankAccount account) {
        if (account == null) return;
        // update the account number for this bank
        account.updateNumber(this.getSize());

        accounts.put(account.getAccountNumber(), account);

        System.out.println("Added: " + account.getAccountNumber());

    }

    // Centralized lookup method
    public BankAccount getAccount(String accountNumber) {

        BankAccount account = accounts.get(accountNumber);
        if(account == null) {
            System.out.println("account not found");

        }
        return account;
    }


    // Get Size function used for seeing how many accounts there are: helpful for account # creation
    // params: none
    // return: int size()
    public int getSize(){

        return this.accounts.size();
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
