public class SummaryTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Summary for " + userId + ": Total spent this month is $450.00.";
    }
    @Override
    public String getDescription() { return "Provides a summary of recent transactions and total spending."; }
}
