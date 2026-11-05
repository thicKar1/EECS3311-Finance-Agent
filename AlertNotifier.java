public class AlertNotifier implements BudgetObserver {
    @Override
    public void update(String message) {
        // In the full GUI, this could trigger a popup. For now, it alerts the system.
        System.out.println("\n[ALERT NOTIFICATION] " + message + "\n");
    }
}
