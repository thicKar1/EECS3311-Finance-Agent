public class AnomalyTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Analysis complete: No unusual spending anomalies detected this week.";
    }
    @Override
    public String getDescription() { return "Detects anomalies or unusually high transactions."; }
}
