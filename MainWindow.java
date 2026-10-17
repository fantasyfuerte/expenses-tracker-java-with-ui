import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

public class MainWindow extends JFrame {
  private final ExpenseTracker tracker;
  private final CategoryTracker categoryTracker;
  private final ExpenseTableModel tableModel;
  private final JTable table;

  private final JLabel totalLabel = new JLabel();
  private final JButton editButton = new JButton("Edit");
  private final JButton deleteButton = new JButton("Delete");

  public MainWindow(ExpenseTracker tracker, CategoryTracker categoryTracker) {
    super("Expenses Tracker");
    this.tracker = tracker;
    this.categoryTracker = categoryTracker;
    this.tableModel = new ExpenseTableModel(tracker);
    this.table = new JTable(tableModel);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setJMenuBar(buildMenuBar());
    configureTable();

    add(buildToolbar(), BorderLayout.NORTH);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(buildFooter(), BorderLayout.SOUTH);

    refreshTotal();
    updateButtons();

    setMinimumSize(new Dimension(640, 400));
    setSize(840, 540);
    setLocationRelativeTo(null);
  }

  private void configureTable() {
    table.setFillsViewportHeight(true);
    table.setRowHeight(28);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setAutoCreateRowSorter(true);
    table.setShowGrid(false);
    table.setIntercellSpacing(new Dimension(0, 0));

    table.getColumnModel().getColumn(1).setCellRenderer(new AmountCellRenderer());
    table.getColumnModel().getColumn(2).setCellRenderer(new CategoryCellRenderer());

    Comparator<Object> byCategoryName =
        (a, b) -> {
          String na = a == null ? "" : ((Category) a).getName();
          String nb = b == null ? "" : ((Category) b).getName();
          return na.compareToIgnoreCase(nb);
        };
    ((DefaultRowSorter<?, ?>) table.getRowSorter()).setComparator(2, byCategoryName);

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
              editExpense();
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
                deleteExpense();
              }
            });
  }

  private JToolBar buildToolbar() {
    JToolBar bar = new JToolBar();
    bar.setFloatable(false);
    bar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

    JButton addButton = new JButton("Add");
    addButton.addActionListener(e -> addExpense());
    editButton.addActionListener(e -> editExpense());
    deleteButton.addActionListener(e -> deleteExpense());

    JButton categoriesButton = new JButton("Categories");
    categoriesButton.addActionListener(e -> manageCategories());

    bar.add(addButton);
    bar.add(editButton);
    bar.add(deleteButton);
    bar.addSeparator();
    bar.add(categoriesButton);
    return bar;
  }

  private JPanel buildFooter() {
    JPanel footer = new JPanel(new BorderLayout());
    footer.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    totalLabel.setFont(
        totalLabel.getFont().deriveFont(Font.BOLD, totalLabel.getFont().getSize2D() + 2f));
    footer.add(totalLabel, BorderLayout.EAST);
    return footer;
  }

  private JMenuBar buildMenuBar() {
    int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

    JMenuBar menuBar = new JMenuBar();

    JMenu fileMenu = new JMenu("File");

    JMenuItem addItem = new JMenuItem("Add expense");
    addItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcut));
    addItem.addActionListener(e -> addExpense());

    JMenuItem editItem = new JMenuItem("Edit expense");
    editItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, shortcut));
    editItem.addActionListener(e -> editExpense());

    JMenuItem deleteItem = new JMenuItem("Delete expense");
    deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, shortcut));
    deleteItem.addActionListener(e -> deleteExpense());

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcut));
    exitItem.addActionListener(e -> System.exit(0));

    fileMenu.add(addItem);
    fileMenu.add(editItem);
    fileMenu.add(deleteItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);

    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.addActionListener(e -> showAbout());
    helpMenu.add(aboutItem);

    JMenu categoriesMenu = new JMenu("Categories");
    JMenuItem manageItem = new JMenuItem("Manage categories...");
    manageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, shortcut));
    manageItem.addActionListener(e -> manageCategories());
    categoriesMenu.add(manageItem);

    menuBar.add(fileMenu);
    menuBar.add(categoriesMenu);
    menuBar.add(helpMenu);
    return menuBar;
  }

  private void addExpense() {
    if (categoryTracker.getCategories().isEmpty()) {
      JOptionPane.showMessageDialog(
          this, "There is no available categories", "No categories", JOptionPane.WARNING_MESSAGE);
      return;
    }
    ExpenseFormDialog dialog = new ExpenseFormDialog(this, categoryTracker, null);
    dialog.setVisible(true);
    if (!dialog.isConfirmed()) {
      return;
    }
    tableModel.addExpense(
        dialog.getAmount(), dialog.getDescription(), dialog.getCategory(), dialog.getDate());
    refreshTotal();
  }

  private void editExpense() {
    int viewRow = table.getSelectedRow();
    if (viewRow == -1) {
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    Expense expense = tableModel.getExpenseAt(modelRow);

    ExpenseFormDialog dialog = new ExpenseFormDialog(this, categoryTracker, expense);
    dialog.setVisible(true);
    if (!dialog.isConfirmed()) {
      return;
    }
    tableModel.updateExpense(
        modelRow,
        dialog.getAmount(),
        dialog.getDescription(),
        dialog.getCategory(),
        dialog.getDate());
    refreshTotal();
  }

  private void deleteExpense() {
    int viewRow = table.getSelectedRow();
    if (viewRow == -1) {
      return;
    }
    int modelRow = table.convertRowIndexToModel(viewRow);
    Expense expense = tableModel.getExpenseAt(modelRow);

    int choice =
        JOptionPane.showConfirmDialog(
            this,
            "Delete \""
                + expense.getDescription()
                + "\" ($"
                + String.format("%,.2f", expense.getAmount())
                + ")?",
            "Confirm deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
    if (choice != JOptionPane.YES_OPTION) {
      return;
    }
    tableModel.removeExpense(modelRow);
    refreshTotal();
    updateButtons();
  }

  private void showAbout() {
    JOptionPane.showMessageDialog(
        this,
        "Expenses Tracker\nExpenses tracker with Java Swing\n\nMVP: list and CRUD in memory.",
        "About",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private void manageCategories() {
    CategoryManagerDialog dialog = new CategoryManagerDialog(this, categoryTracker, tracker);
    dialog.setVisible(true);
    tableModel.refreshData();
  }

  private void refreshTotal() {
    totalLabel.setText(String.format("Total: $%,.2f", tracker.getTotal()));
  }

  private void updateButtons() {
    boolean hasSelection = table.getSelectedRow() != -1;
    editButton.setEnabled(hasSelection);
    deleteButton.setEnabled(hasSelection);
  }
}
