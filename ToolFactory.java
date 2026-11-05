public class ToolFactory {
    public static FinanceTool createTool(String toolType) {
        if (toolType == null) {
            return null;
        }
        
        if (toolType.equalsIgnoreCase("budget")) {
            return new BudgetTool();
        } else if (toolType.equalsIgnoreCase("transaction")) {
            return new TransactionTool();
        }
        // We will add the remaining 8 tools here as we build them
        
        throw new IllegalArgumentException("Unknown tool type requested: " + toolType);
    }
}
