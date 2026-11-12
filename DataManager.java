import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static DataManager instance;
    private String dataFile = "finance_transactions.csv";
    private Map<String, Double> budgetLimits = new HashMap<>();
    private static final double DEFAULT_BUDGET_LIMIT = 2000.0;

    public static class Transaction {
        public final String userId;
        public final double amount;
        public final String category;
        public final long timestamp;

        public Transaction(String userId, double amount, String category, long timestamp) {
            this.userId = userId;
            this.amount = amount;
            this.category = category;
            this.timestamp = timestamp;
        }
    }

    private DataManager() {
        initializeStorage();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    // Allows JUnit tests to run on an isolated temporary CSV file
    public synchronized void useCustomDataFile(String customPath) {
        this.dataFile = customPath;
        this.budgetLimits.clear();
        initializeStorage();
    }

    private void initializeStorage() {
        File file = new File(dataFile);
        try {
            boolean created = file.createNewFile();
            if (created || file.length() == 0) {
                seedDefaultData();
            }
        } catch (IOException e) {
            System.out.println("Error initializing data file: " + e.getMessage());
        }
    }

    private void seedDefaultData() {
        saveTransaction("user1", 950.00, "Rent");
        saveTransaction("user1", 220.00, "Groceries");
        saveTransaction("user1", 80.00, "Utilities");
        saveTransaction("user1", 150.00, "Dining");
    }

    public synchronized void resetDatabase() {
        this.budgetLimits.clear();
        try (PrintWriter out = new PrintWriter(new FileWriter(dataFile, false))) {
            // Overwrites the CSV file to be completely empty (0 transactions)
        } catch (IOException e) {
            System.out.println("Error resetting database: " + e.getMessage());
        }
    }

    public synchronized boolean saveTransaction(String userId, double amount, String category) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than zero.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }

        String cleanUser = userId.trim();
        String cleanCategory = category.trim().replace(",", " ");

        try (FileWriter fw = new FileWriter(dataFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(cleanUser + "," + amount + "," + cleanCategory + "," + System.currentTimeMillis());
            return true;
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
            return false;
        }
    }

    public synchronized List<Transaction> getTransactions(String userId) {
        List<Transaction> results = new ArrayList<>();
        if (userId == null || userId.trim().isEmpty()) {
            return results;
        }

        File file = new File(dataFile);
        if (!file.exists()) {
            return results;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].trim().equalsIgnoreCase(userId.trim())) {
                    try {
                        double amt = Double.parseDouble(parts[1].trim());
                        String cat = parts[2].trim();
                        long ts = Long.parseLong(parts[3].trim());
                        results.add(new Transaction(parts[0].trim(), amt, cat, ts));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
        return results;
    }

    public synchronized double getTotalSpending(String userId) {
        double total = 0.0;
        for (Transaction t : getTransactions(userId)) {
            total += t.amount;
        }
        return total;
    }

    public synchronized double getSpendingByCategory(String userId, String category) {
        if (category == null || category.trim().isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Transaction t : getTransactions(userId)) {
            if (t.category.equalsIgnoreCase(category.trim())) {
                total += t.amount;
            }
        }
        return total;
    }

    public synchronized double getBudgetLimit(String userId) {
        if (userId == null) return DEFAULT_BUDGET_LIMIT;
        return budgetLimits.getOrDefault(userId.trim().toLowerCase(), DEFAULT_BUDGET_LIMIT);
    }

    public synchronized void setBudgetLimit(String userId, double newLimit) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty.");
        }
        if (newLimit <= 0) {
            throw new IllegalArgumentException("Budget limit must be greater than zero.");
        }
        budgetLimits.put(userId.trim().toLowerCase(), newLimit);
    }
}
