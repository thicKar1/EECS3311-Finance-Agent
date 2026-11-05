public class TransactionTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        // In a real scenario, the LLM will extract these from the query and pass them as args
        double amount = 50.0; // Mock default
        String category = "Groceries"; // Mock default
        
        if (args != null && args.length >= 2) {
            try {
                amount = Double.parseDouble(args[0]);
                category = args[1];
            } catch (NumberFormatException e) {
                return "Error: Invalid amount format.";
            }
        }
        
        DataManager dataManager = DataManager.getInstance();
        boolean success = dataManager.saveTransaction(userId, amount, category);
        
        if (success) {
            return "Transaction of $" + amount + " for " + category + " saved successfully.";
        } else {
            return "Error: Failed to save transaction.";
        }
    }

    @Override
    public String getDescription() {
        return "Allows the user to enter a new financial transaction.";
    }
}
