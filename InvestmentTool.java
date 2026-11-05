public class InvestmentTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Investment portfolio is up 3.5% this quarter. Top performer: Tech Index.";
    }
    @Override
    public String getDescription() { return "Tracks investment portfolio performance."; }
}
