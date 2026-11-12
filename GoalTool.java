import java.util.HashMap;
import java.util.Map;

public class GoalTool implements FinanceTool {
    private static final Map<String, Double> savedAmounts = new HashMap<>();
    private static final Map<String, Double> targetAmounts = new HashMap<>();

    @Override
    public String execute(String userId, String[] args) {
        String key = (userId == null || userId.trim().isEmpty()) ? "user1" : userId.trim().toLowerCase();
        savedAmounts.putIfAbsent(key, 2500.00);
        targetAmounts.putIfAbsent(key, 5000.00);

        if (args != null && args.length >= 1 && !args[0].trim().isEmpty()) {
            try {
                double deposit = Double.parseDouble(args[0].trim().replace("$", ""));
                if (deposit <= 0) {
                    return "Error: Savings deposit must be greater than zero.";
                }
                savedAmounts.put(key, savedAmounts.get(key) + deposit);
            } catch (NumberFormatException e) {
                return "Error: Invalid deposit amount '" + args[0] + "'.";
            }
        }

        if (args != null && args.length >= 2 && !args[1].trim().isEmpty()) {
            try {
                double newTarget = Double.parseDouble(args[1].trim().replace("$", ""));
                if (newTarget <= 0) {
                    return "Error: Target goal amount must be greater than zero.";
                }
                targetAmounts.put(key, newTarget);
            } catch (NumberFormatException e) {
                return "Error: Invalid target goal amount '" + args[1] + "'.";
            }
        }

        double current = savedAmounts.get(key);
        double target = targetAmounts.get(key);
        double pct = (current / target) * 100.0;
        double remaining = Math.max(0.0, target - current);

        return String.format(
            "Savings Goal for %s: $%.2f saved toward $%.2f target (%.1f%% complete, $%.2f remaining).",
            userId, current, target, pct, remaining
        );
    }

    @Override
    public String getDescription() {
        return "Tracks and updates progress toward savings goals (optional args: depositAmount, targetAmount).";
    }
}
