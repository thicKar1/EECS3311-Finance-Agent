public class TaxTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        double annualIncome = 65000.0;

        if (args != null && args.length >= 1 && !args[0].trim().isEmpty()) {
            try {
                annualIncome = Double.parseDouble(args[0].trim().replace("$", "").replace(",", ""));
            } catch (NumberFormatException e) {
                return "Error: Invalid annual income '" + args[0] + "'.";
            }
        }

        if (annualIncome < 0) {
            return "Error: Annual income cannot be negative.";
        }

        double tax = calculateProgressiveTax(annualIncome);
        double effectiveRate = (annualIncome > 0) ? (tax / annualIncome) * 100.0 : 0.0;
        double netIncome = annualIncome - tax;

        return String.format(
            "Tax estimate for %s on $%.2f income: Estimated tax = $%.2f (Effective rate: %.1f%%, Net income: $%.2f).",
            userId, annualIncome, tax, effectiveRate, netIncome
        );
    }

    public double calculateProgressiveTax(double income) {
        double tax = 0.0;
        if (income > 100000) {
            tax += (income - 100000) * 0.28;
            income = 100000;
        }
        if (income > 50000) {
            tax += (income - 50000) * 0.22;
            income = 50000;
        }
        if (income > 15000) {
            tax += (income - 15000) * 0.15;
            income = 15000;
        }
        tax += income * 0.10;
        return tax;
    }

    @Override
    public String getDescription() {
        return "Estimates progressive income taxes and net take-home pay (optional arg: annualIncome).";
    }
}
