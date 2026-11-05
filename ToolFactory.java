public class ToolFactory {
    public static FinanceTool createTool(String toolType) {
        if (toolType == null) return null;
        
        switch (toolType.toLowerCase()) {
            case "budget": return new BudgetTool();
            case "transaction": return new TransactionTool();
            case "summary": return new SummaryTool();
            case "anomaly": return new AnomalyTool();
            case "goal": return new GoalTool();
            case "tax": return new TaxTool();
            case "debt": return new DebtTool();
            case "currency": return new CurrencyTool();
            case "investment": return new InvestmentTool();
            case "bill": return new BillTool();
            default: throw new IllegalArgumentException("Unknown tool type: " + toolType);
        }
    }
}
