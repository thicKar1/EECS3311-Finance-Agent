public class TaxTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Estimated tax bracket based on income data: 22%. Expected deductions: $1,200.";
    }
    @Override
    public String getDescription() { return "Estimates taxes and brackets based on user income."; }
}
