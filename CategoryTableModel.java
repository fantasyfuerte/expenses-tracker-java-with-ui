import javax.swing.table.AbstractTableModel;

public class CategoryTableModel extends AbstractTableModel {
  private static final String[] COLUMNS = {"Category", "Expenses"};

  private final CategoryTracker categoryTracker;
  private final ExpenseTracker expenseTracker;

  public CategoryTableModel(CategoryTracker categoryTracker, ExpenseTracker expenseTracker) {
    this.categoryTracker = categoryTracker;
    this.expenseTracker = expenseTracker;
  }

  @Override
  public int getRowCount() {
    return categoryTracker.getCategories().size();
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
      case 0 -> Category.class;
      case 1 -> Integer.class;
      default -> String.class;
    };
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    Category category = categoryTracker.getCategory(rowIndex);
    return switch (columnIndex) {
      case 0 -> category;
      case 1 -> countExpenses(category);
      default -> null;
    };
  }

  public Category getCategoryAt(int rowIndex) {
    return categoryTracker.getCategory(rowIndex);
  }

  public int countExpenses(Category category) {
    int count = 0;
    for (Expense expense : expenseTracker.getExpenses()) {
      if (expense.getCategory() == category) {
        count++;
      }
    }
    return count;
  }

  public void addCategory(String name, Color color) {
    categoryTracker.addCategory(name, color);
    int row = categoryTracker.getCategories().size() - 1;
    fireTableRowsInserted(row, row);
  }

  public void updateCategory(int rowIndex, String name, Color color) {
    categoryTracker.updateCategory(rowIndex, name, color);
    fireTableRowsUpdated(rowIndex, rowIndex);
  }

  public void removeCategory(int rowIndex) {
    categoryTracker.removeCategory(rowIndex, expenseTracker);
    fireTableRowsDeleted(rowIndex, rowIndex);
  }
}
