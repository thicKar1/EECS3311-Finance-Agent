public class DebtTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Outstanding debt: $1,500 on credit card. Suggested payoff date: Dec 15.";
    }
    @Override
    public String getDescription() { return "Tracks outstanding debts and calculates payoff schedules."; }
}
