public class GoalTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Savings Goal Progress: You are 50% towards your $5000 vacation fund.";
    }
    @Override
    public String getDescription() { return "Tracks progress towards financial savings goals."; }
}
