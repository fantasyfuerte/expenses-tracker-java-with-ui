import java.time.LocalDateTime;
import java.util.Locale;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
  public static void main(String[] args) {
    Locale.setDefault(Locale.ENGLISH);
    SwingUtilities.invokeLater(
        () -> {
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          } catch (Exception ignored) {
          }

          ExpenseTracker tracker = new ExpenseTracker();
          CategoryTracker categories = new CategoryTracker();

          categories.addCategory("Food", Color.RED);
          categories.addCategory("Transport", Color.BLUE);
          categories.addCategory("Health", Color.PURPLE);
          categories.addCategory("Home", Color.GREEN);
          categories.addCategory("Misc", Color.ORANGE);

          tracker.addExpense(12.50, "Coffee", categories.getCategory(0), LocalDateTime.now());
          tracker.addExpense(30.00, "Bus", categories.getCategory(1), LocalDateTime.now());
          tracker.addExpense(9.99, "Streaming", categories.getCategory(2), LocalDateTime.now());

          new MainWindow(tracker, categories).setVisible(true);
        });
  }
}
