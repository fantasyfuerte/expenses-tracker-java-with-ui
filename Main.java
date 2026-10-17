import java.time.LocalDateTime;

public class Main {
  public static void main(String[] args) {
    ExpenseTracker tracker = new ExpenseTracker();
    CategoryTracker categoriesTracker = new CategoryTracker();
    categoriesTracker.addCategory("Food", Color.RED);
    tracker.addExpense(10.0, "Coffee", categoriesTracker.getCategory(0), LocalDateTime.now());
    tracker.displayExpenses();
    categoriesTracker.updateCategory(0, "Misc", null);
    tracker.displayExpenses();
    categoriesTracker.removeCategory(0, tracker);
    tracker.displayExpenses();
  }
}
