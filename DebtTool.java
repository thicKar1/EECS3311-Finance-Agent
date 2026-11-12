public class DebtTool implements FinanceTool {
    private static final double DEFAULT_DEBT = 1500.00;
    private static final double ANNUAL_APR = 0.18;

    @Override
    public String execute(String userId, String[] args) {
        double monthlyPayment = 150.00;

        if (args != null && args.length >= 1 && !args[0].trim().isEmpty()) {
            try {
                monthlyPayment = Double.parseDouble(args[0].trim().replace("$", ""));
            } catch (NumberFormatException e) {
                return "Error: Invalid monthly payment '" + args[0] + "'.";
            }
        }

        if (monthlyPayment <= 0) {
            return "Error: Monthly payment must be greater than zero.";
        }

        double balance = DEFAULT_DEBT;
        double monthlyRate = ANNUAL_APR / 12.0;

        if (monthlyPayment <= balance * monthlyRate) {
            return String.format(
                "Error: Monthly payment of $%.2f does not cover monthly interest ($%.2f). Increase payment to pay off debt.",
                monthlyPayment, balance * monthlyRate
            );
        }

        int months = 0;
        double totalInterest = 0.0;
        while (balance > 0 && months < 600) {
            double interest = balance * monthlyRate;
            totalInterest += interest;
            balance = balance + interest - monthlyPayment;
            months++;
        }

        return String.format(
            "Debt payoff plan for %s ($%.2f balance at 18%% APR, paying $%.2f/mo): Paid off in %d months with $%.2f total interest.",
            userId, DEFAULT_DEBT, monthlyPayment, months, totalInterest
        );
    }

    @Override
    public String getDescription() {
        return "Calculates debt payoff timeline and total interest (optional arg: monthlyPayment).";
    }
}
