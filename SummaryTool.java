import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SummaryTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        DataManager dm = DataManager.getInstance();
        List<DataManager.Transaction> txs = dm.getTransactions(userId);

        if (txs.isEmpty()) {
            return "Summary for " + userId + ": No transactions recorded yet.";
        }

        double total = 0.0;
        Map<String, Double> byCategory = new LinkedHashMap<>();
        for (DataManager.Transaction t : txs) {
            total += t.amount;
            byCategory.put(t.category, byCategory.getOrDefault(t.category, 0.0) + t.amount);
        }

        StringBuilder breakdown = new StringBuilder();
        for (Map.Entry<String, Double> entry : byCategory.entrySet()) {
            if (breakdown.length() > 0) breakdown.append(", ");
            breakdown.append(String.format("%s: $%.2f", entry.getKey(), entry.getValue()));
        }

        return String.format(
            "Summary for %s (%d transactions): Total spent is $%.2f. Breakdown: [%s].",
            userId, txs.size(), total, breakdown.toString()
        );
    }

    @Override
    public String getDescription() {
        return "Provides a real spending summary and category breakdown from stored transactions.";
    }
}
