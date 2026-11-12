import java.util.ArrayList;
import java.util.List;

public class AnomalyTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        DataManager dm = DataManager.getInstance();
        List<DataManager.Transaction> txs = dm.getTransactions(userId);

        if (txs.size() < 2) {
            return "Anomaly analysis for " + userId + ": Not enough transactions to establish a spending baseline.";
        }

        double sum = 0.0;
        for (DataManager.Transaction t : txs) {
            sum += t.amount;
        }
        double average = sum / txs.size();
        double threshold = average * 2.0;

        List<String> flagged = new ArrayList<>();
        for (DataManager.Transaction t : txs) {
            if (t.amount > threshold) {
                flagged.add(String.format("%s ($%.2f)", t.category, t.amount));
            }
        }

        if (flagged.isEmpty()) {
            return String.format(
                "Anomaly analysis for %s: No unusual spending detected across %d transactions (avg: $%.2f).",
                userId, txs.size(), average
            );
        }

        return String.format(
            "Anomaly alert for %s: %d transaction(s) exceeded 2x average ($%.2f): %s.",
            userId, flagged.size(), average, String.join(", ", flagged)
        );
    }

    @Override
    public String getDescription() {
        return "Detects unusually high transactions compared to the user's average spending.";
    }
}
