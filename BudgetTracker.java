import java.util.ArrayList;
import java.util.List;

public class BudgetTracker {
    private List<BudgetObserver> observers = new ArrayList<>();
    
    public void addObserver(BudgetObserver observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String message) {
        for (BudgetObserver observer : observers) {
            observer.update(message);
        }
    }
    
    public void checkBudget(double currentSpending, double budgetLimit) {
        if (currentSpending > budgetLimit) {
            notifyObservers("Warning! You have exceeded your budget of $" + budgetLimit + 
                            ". Current spending: $" + currentSpending);
        }
    }
}
