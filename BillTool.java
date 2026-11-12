import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillTool implements FinanceTool {
    private static final Map<String, List<String>> userBills = new HashMap<>();

    @Override
    public String execute(String userId, String[] args) {
        String key = (userId == null || userId.trim().isEmpty()) ? "user1" : userId.trim().toLowerCase();
        if (!userBills.containsKey(key)) {
            List<String> defaults = new ArrayList<>();
            defaults.add("Internet ($80.00) due in 3 days");
            defaults.add("Phone Plan ($55.00) due in 9 days");
            userBills.put(key, defaults);
        }

        if (args != null && args.length >= 2 && !args[0].trim().isEmpty() && !args[1].trim().isEmpty()) {
            String billName = args[0].trim();
            double amount;
            try {
                amount = Double.parseDouble(args[1].trim().replace("$", ""));
                if (amount <= 0) {
                    return "Error: Bill amount must be greater than zero.";
                }
            } catch (NumberFormatException e) {
                return "Error: Invalid bill amount '" + args[1] + "'.";
            }

            int days = 7;
            if (args.length >= 3 && !args[2].trim().isEmpty()) {
                try {
                    days = Integer.parseInt(args[2].trim());
                    if (days < 0) return "Error: Due days cannot be negative.";
                } catch (NumberFormatException e) {
                    return "Error: Invalid due days '" + args[2] + "'.";
                }
            }

            userBills.get(key).add(String.format("%s ($%.2f) due in %d days", billName, amount, days));
        }

        List<String> bills = userBills.get(key);
        return "Upcoming bills for " + userId + ": " + String.join("; ", bills) + ".";
    }

    @Override
    public String getDescription() {
        return "Lists upcoming bills or adds a new bill reminder (optional args: billName, amount, daysDue).";
    }
}
