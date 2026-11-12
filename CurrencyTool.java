public class CurrencyTool implements FinanceTool {
    private static final double USD_TO_CAD = 1.38;

    @Override
    public String execute(String userId, String[] args) {
        double amount = 100.0;
        String from = "USD";
        String to = "CAD";

        if (args != null && args.length >= 1 && !args[0].trim().isEmpty()) {
            try {
                amount = Double.parseDouble(args[0].trim().replace("$", ""));
            } catch (NumberFormatException e) {
                return "Error: Invalid currency amount '" + args[0] + "'.";
            }
            if (amount <= 0) {
                return "Error: Currency amount must be greater than zero.";
            }
        }

        if (args != null && args.length >= 2 && !args[1].trim().isEmpty()) {
            from = args[1].trim().toUpperCase();
        }
        if (args != null && args.length >= 3 && !args[2].trim().isEmpty()) {
            to = args[2].trim().toUpperCase();
        }

        if (!from.equals("USD") && !from.equals("CAD")) {
            return "Error: Unsupported currency '" + from + "'. Only USD and CAD are supported.";
        }
        if (!to.equals("USD") && !to.equals("CAD")) {
            return "Error: Unsupported currency '" + to + "'. Only USD and CAD are supported.";
        }

        double converted;
        if (from.equals(to)) {
            converted = amount;
        } else if (from.equals("USD")) {
            converted = amount * USD_TO_CAD;
        } else {
            converted = amount / USD_TO_CAD;
        }

        return String.format("Currency conversion: %.2f %s = %.2f %s.", amount, from, converted, to);
    }

    @Override
    public String getDescription() {
        return "Converts currency amounts between USD and CAD (args: amount, fromCurrency, toCurrency).";
    }
}
