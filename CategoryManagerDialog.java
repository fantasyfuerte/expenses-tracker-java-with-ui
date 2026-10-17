import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

public class CategoryManagerDialog extends JDialog {
  private final ExpenseTracker expenseTracker;
  private final CategoryTableModel tableModel;
  private final JTable table;

  private final JButton editButton = new JButton("Edit");
  private final JButton deleteButton = new JButton("Delete");

  public CategoryManagerDialog(
      Frame owner, CategoryTracker categoryTracker, ExpenseTracker expenseTracker) {
    super(owner, "Categories", true);
    this.expenseTracker = expenseTracker;
    this.tableModel = new CategoryTableModel(categoryTracker, expenseTracker);
    this.table = new JTable(tableModel);

    configureTable();

    add(buildToolbar(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildButtons(), BorderLayout.SOUTH);

    updateButtons();

    setMinimumSize(new Dimension(420, 320));
    setSize(480, 380);
    setLocationRelativeTo(owner);
  }

  private void configureTable() {
    table.setFillsViewportHeight(true);
    table.setRowHeight(28);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));

    table.getColumnModel().getColumn(0).setCellRenderer(new CategoryCellRenderer());

    table
        .getSelectionModel()
        .addListSelectionListener(
            e -> {
              if (!e.getValueIsAdjusting()) {
                updateButtons();
              }
            });

    table.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
              editCategory();
            }
          }
        });

    table
        .getInputMap(JComponent.WHEN_FOCUSED)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
    table
        .getActionMap()
        .put(
            "delete",
            new AbstractAction() {
              @Override
              public void actionPerformed(ActionEvent e) {
                deleteCategory();
              }
            });
  }

  private JPanel buildToolbar() {
    JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
    bar.setBorder(BorderFactory.createEmptyBorder(6, 6, 0, 6));

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addCategory());
    editButton.addActionListener(e -> editCategory());
    deleteButton.addActionListener(e -> deleteCategory());

    bar.add(addButton);
    bar.add(editButton);
    bar.add(deleteButton);
    return bar;
  }

  private JPanel buildButtons() {
    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());
    buttons.add(closeButton);
    return buttons;
  }

  private void addCategory() {
    CategoryFormDialog dialog = new CategoryFormDialog((Frame) getOwner(), null);
    dialog.setVisible(true);
    if (!dialog.isConfirmed()) {
      return;
    }
    tableModel.addCategory(dialog.getCategoryName(), dialog.getSelectedColor());
    selectLastRow();
  }

  private void editCategory() {
    int viewRow = table.getSelectedRow();
    if (viewRow == -1) {
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    Category category = tableModel.getCategoryAt(modelRow);

    CategoryFormDialog dialog = new CategoryFormDialog((Frame) getOwner(), category);
    dialog.setVisible(true);
    if (!dialog.isConfirmed()) {
      return;
    }
    tableModel.updateCategory(modelRow, dialog.getCategoryName(), dialog.getSelectedColor());
  }

  private void deleteCategory() {
    int viewRow = table.getSelectedRow();
    if (viewRow == -1) {
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    Category category = tableModel.getCategoryAt(modelRow);
    int inUse = tableModel.countExpenses(category);

    String message =
        inUse == 0
            ? "Delete category \"" + category.getName() + "\"?"
            : "Category \""
                + category.getName()
                + "\" is used by "
                + inUse
                + " expense(s).\nThose expenses will become uncategorized.\n\nDelete anyway?";

    int choice =
        JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirm deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
    if (choice != JOptionPane.YES_OPTION) {
      return;
    }

    tableModel.removeCategory(modelRow);
    updateButtons();
  }

  private void selectLastRow() {
    int last = table.getRowCount() - 1;
    if (last >= 0) {
      table.setRowSelectionInterval(last, last);
      table.scrollRectToVisible(table.getCellRect(last, 0, true));
    }
  }

  private void updateButtons() {
    boolean hasSelection = table.getSelectedRow() != -1;
    editButton.setEnabled(hasSelection);
    deleteButton.setEnabled(hasSelection);
  }
}
