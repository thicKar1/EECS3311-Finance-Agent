public class CurrencyTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Currency converted: 100 USD is currently 138 CAD.";
    }
    @Override
    public String getDescription() { return "Converts currency values based on current exchange rates."; }
}
