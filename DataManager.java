import java.io.*;

public class DataManager {
    // Singleton instance
    private static DataManager instance;
    private final String dataFile = "finance_transactions.csv";

    // Private constructor prevents instantiation from other classes
    private DataManager() {
        File file = new File(dataFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error initializing data file: " + e.getMessage());
            }
        }
    }

    // Public method to provide access to the single instance
    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public boolean saveTransaction(String userId, double amount, String category) {
        try (FileWriter fw = new FileWriter(dataFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            // Saves in format: userId,amount,category,timestamp
            out.println(userId + "," + amount + "," + category + "," + System.currentTimeMillis());
            return true;
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
            return false;
        }
    }
}
