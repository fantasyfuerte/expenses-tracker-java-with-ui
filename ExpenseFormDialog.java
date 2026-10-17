import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

public class ExpenseFormDialog extends JDialog {
  private final JTextField descriptionField = new JTextField(22);
  private final JSpinner amountSpinner =
      new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1_000_000_000.0, 1.0));
  private final JComboBox<Category> categoryCombo;
  private final JSpinner dateSpinner;

  private boolean confirmed = false;

  public ExpenseFormDialog(Frame owner, CategoryTracker categoryTracker, Expense expense) {
    super(owner, expense == null ? "New expense" : "Edit expense", true);

    Category[] categories = categoryTracker.getCategories().toArray(new Category[0]);
    this.categoryCombo = new JComboBox<>(categories);

    this.dateSpinner =
        new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
    this.dateSpinner.setEditor(new JSpinner.DateEditor(this.dateSpinner, "yyyy-MM-dd HH:mm"));

    this.amountSpinner.setEditor(new JSpinner.NumberEditor(this.amountSpinner, "0.00"));

    if (expense != null) {
      populate(expense);
    }

    buildLayout(owner);
  }

  private void populate(Expense expense) {
    descriptionField.setText(expense.getDescription());
    amountSpinner.setValue(expense.getAmount());
    if (expense.getCategory() != null) {
      categoryCombo.setSelectedItem(expense.getCategory());
    }
    if (expense.getRawDate() != null) {
      Date date = Date.from(expense.getRawDate().atZone(ZoneId.systemDefault()).toInstant());
      dateSpinner.setValue(date);
    }
  }

  private void buildLayout(Frame owner) {
    JPanel form = new JPanel(new GridBagLayout());
    form.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    addRow(form, gbc, 0, "Description:", descriptionField);
    addRow(form, gbc, 1, "Amount:", amountSpinner);
    addRow(form, gbc, 2, "Category:", categoryCombo);
    addRow(form, gbc, 3, "Date:", dateSpinner);

    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    saveButton.addActionListener(e -> onSave());
    cancelButton.addActionListener(e -> dispose());

    JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    buttons.add(cancelButton);
    buttons.add(saveButton);

    setLayout(new BorderLayout());
    add(form, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

    getRootPane().setDefaultButton(saveButton);

    pack();
    setResizable(false);
    setLocationRelativeTo(owner);
  }

  private static void addRow(
      JPanel panel, GridBagConstraints gbc, int row, String label, Component field) {
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0;
    panel.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1;
    panel.add(field, gbc);
  }

  private void onSave() {
    String description = getDescription();
    if (description.isEmpty()) {
      warn("Description cannot be empty.");
      return;
    }
    if (getAmount() <= 0) {
      warn("Amount must be greater than 0.");
      return;
    }
    if (getCategory() == null) {
      warn("You must select a category.");
      return;
    }
    confirmed = true;
    dispose();
  }

  private void warn(String message) {
    JOptionPane.showMessageDialog(this, message, "Validation", JOptionPane.WARNING_MESSAGE);
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public String getDescription() {
    return descriptionField.getText().trim();
  }

  public double getAmount() {
    return ((Number) amountSpinner.getValue()).doubleValue();
  }

  public Category getCategory() {
    return (Category) categoryCombo.getSelectedItem();
  }

  public LocalDateTime getDate() {
    Date date = (Date) dateSpinner.getValue();
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }
}
