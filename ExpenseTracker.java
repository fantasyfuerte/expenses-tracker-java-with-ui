import java.time.LocalDateTime;
import java.util.ArrayList;

public class ExpenseTracker {
  private ArrayList<Expense> expenses;

  public ExpenseTracker() {
    this.expenses = new ArrayList<Expense>();
  }

  public Expense addExpense(
      double amount, String description, Category category, LocalDateTime date) {
    Expense expense = new Expense(amount, description, category, date);
    this.expenses.add(expense);
    return expense;
  }

  public Expense getExpense(int index) {
    return this.expenses.get(index);
  }

  public Expense updateExpense(
      int index, Double amount, String description, Category category, LocalDateTime date) {
    Expense expense = getExpense(index);
    if (amount != null) {
      expense.setAmount(amount);
    }
    if (description != null) {
      expense.setDescription(description);
    }
    if (category != null) {
      expense.setCategory(category);
    }
    if (date != null) {
      expense.setRawDate(date);
    }
    return expense;
  }

  public Expense removeExpense(int index) {
    Expense expense = getExpense(index);
    this.expenses.remove(index);
    return expense;
  }

  public void displayExpenses() {
    for (Expense expense : expenses) {
      System.out.println(expense.getInfo());
    }
  }
}
