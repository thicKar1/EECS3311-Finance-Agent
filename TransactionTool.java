public class TransactionTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        if (args == null || args.length < 2 || args[0].trim().isEmpty() || args[1].trim().isEmpty()) {
            return "Error: Please specify both a valid transaction amount and category (e.g., 45.50 Groceries).";
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0].trim().replace("$", ""));
        } catch (NumberFormatException e) {
            return "Error: Invalid amount format '" + args[0] + "'.";
        }

        if (amount <= 0) {
            return "Error: Transaction amount must be greater than zero.";
        }

        String category = args[1].trim();
        DataManager dm = DataManager.getInstance();

        try {
            boolean saved = dm.saveTransaction(userId, amount, category);
            if (!saved) {
                return "Error: Failed to save transaction to storage.";
            }

            double totalSpent = dm.getTotalSpending(userId);
            double limit = dm.getBudgetLimit(userId);

            // Wire up Observer pattern to alert when budget limit is exceeded
            BudgetTracker tracker = new BudgetTracker();
            tracker.addObserver(new AlertNotifier());
            tracker.checkBudget(totalSpent, limit);

            String response = String.format(
                "Saved $%.2f under '%s' for %s. Total spending is now $%.2f / $%.2f.",
                amount, category, userId, totalSpent, limit
            );

            if (totalSpent > limit) {
                response += String.format(" [WARNING: Budget exceeded by $%.2f!]", totalSpent - limit);
            }
            return response;
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Logs a new financial transaction with an amount and category (args: amount, category).";
    }
}
