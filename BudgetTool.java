public class BudgetTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        // Later, this will connect to your database
        return "Fetched budget data for " + userId + ": 70% of monthly budget used.";
    }

    @Override
    public String getDescription() {
        return "Fetches the user's current budget and spending status.";
    }
}
