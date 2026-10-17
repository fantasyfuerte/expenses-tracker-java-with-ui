import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.AbstractTableModel;

public class ExpenseTableModel extends AbstractTableModel {
  private static final String[] COLUMNS = {"Description", "Amount", "Category", "Date"};
  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final ExpenseTracker tracker;

  public ExpenseTableModel(ExpenseTracker tracker) {
    this.tracker = tracker;
  }

  @Override
  public int getRowCount() {
    return tracker.getExpenses().size();
  }

  @Override
  public int getColumnCount() {
    return COLUMNS.length;
  }

  @Override
  public String getColumnName(int column) {
    return COLUMNS[column];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    return switch (columnIndex) {
      case 1 -> Double.class;
      case 2 -> Category.class;
      default -> String.class;
    };
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Expense expense = tracker.getExpense(rowIndex);
    Object value;
    switch (columnIndex) {
      case 0 -> value = expense.getDescription();
      case 1 -> value = expense.getAmount();
      case 2 -> value = expense.getCategory();
      case 3 ->
          value = expense.getRawDate() == null ? "" : expense.getRawDate().format(DATE_FORMAT);
      default -> value = null;
    }
    return value;
  }

  public Expense getExpenseAt(int rowIndex) {
    return tracker.getExpense(rowIndex);
  }

  public void addExpense(double amount, String description, Category category, LocalDateTime date) {
    tracker.addExpense(amount, description, category, date);
    int row = tracker.getExpenses().size() - 1;
    fireTableRowsInserted(row, row);
  }

  public void updateExpense(
      int rowIndex, double amount, String description, Category category, LocalDateTime date) {
    tracker.updateExpense(rowIndex, amount, description, category, date);
    fireTableRowsUpdated(rowIndex, rowIndex);
  }

  public void removeExpense(int rowIndex) {
    tracker.removeExpense(rowIndex);
    fireTableRowsDeleted(rowIndex, rowIndex);
  }

  public void refreshData() {
    fireTableDataChanged();
  }
}
