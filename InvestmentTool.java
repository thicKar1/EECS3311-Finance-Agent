import java.util.HashMap;
import java.util.Map;

public class InvestmentTool implements FinanceTool {
    private static final Map<String, Double> portfolios = new HashMap<>();
    private static final double ANNUAL_RETURN = 0.07;

    @Override
    public String execute(String userId, String[] args) {
        String key = (userId == null || userId.trim().isEmpty()) ? "user1" : userId.trim().toLowerCase();
        portfolios.putIfAbsent(key, 10000.00);

        if (args != null && args.length >= 1 && !args[0].trim().isEmpty()) {
            try {
                double contribution = Double.parseDouble(args[0].trim().replace("$", ""));
                if (contribution < 0) {
                    return "Error: Investment contribution cannot be negative.";
                }
                portfolios.put(key, portfolios.get(key) + contribution);
            } catch (NumberFormatException e) {
                return "Error: Invalid investment amount '" + args[0] + "'.";
            }
        }

        double balance = portfolios.get(key);
        double oneYearProjected = balance * (1.0 + ANNUAL_RETURN);

        return String.format(
            "Investment portfolio for %s: Current balance $%.2f (60%% Tech Index, 30%% S&P 500, 10%% Bonds). Projected 1-year value at 7%% return: $%.2f.",
            userId, balance, oneYearProjected
        );
    }

    @Override
    public String getDescription() {
        return "Tracks investment portfolio balance, allocation, and 1-year return projection (optional arg: contributionAmount).";
    }
}
