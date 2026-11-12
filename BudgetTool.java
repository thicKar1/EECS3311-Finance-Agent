public class BudgetTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        DataManager dm = DataManager.getInstance();

        // If an amount argument is provided, update the user's budget limit
        if (args != null && args.length >= 1 && !args[0].trim().isEmpty()) {
            try {
                double newLimit = Double.parseDouble(args[0].trim().replace("$", ""));
                dm.setBudgetLimit(userId, newLimit);
            } catch (NumberFormatException e) {
                return "Error: Invalid budget limit '" + args[0] + "'.";
            } catch (IllegalArgumentException e) {
                return "Error: " + e.getMessage();
            }
        }

        double spent = dm.getTotalSpending(userId);
        double limit = dm.getBudgetLimit(userId);
        double percentUsed = (limit > 0) ? (spent / limit) * 100.0 : 0.0;
        double remaining = limit - spent;

        return String.format(
            "Budget status for %s: $%.2f spent of $%.2f limit (%.1f%% used, $%.2f remaining).",
            userId, spent, limit, percentUsed, remaining
        );
    }

    @Override
    public String getDescription() {
        return "Fetches current budget status or updates the budget limit (optional arg: newLimit).";
    }
}
