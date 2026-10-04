import java.time.LocalDateTime;

public class Main {
  public static void main(String[] args) {
    ExpenseTracker tracker = new ExpenseTracker();
    tracker.addExpense(100, "Coffee", LocalDateTime.now());
    tracker.addExpense(200, "Coffee", LocalDateTime.now());
    tracker.displayExpenses();
    tracker.removeExpense(1);
  }
}
