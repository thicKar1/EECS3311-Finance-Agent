public class BillTool implements FinanceTool {
    @Override
    public String execute(String userId, String[] args) {
        return "Upcoming bills: Internet ($80) due in 3 days.";
    }
    @Override
    public String getDescription() { return "Manages and reminds the user of upcoming bills."; }
}
