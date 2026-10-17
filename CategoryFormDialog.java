import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CategoryFormDialog extends JDialog {
  private final JTextField nameField = new JTextField(18);
  private final JComboBox<Color> colorCombo;

  private boolean confirmed = false;

  public CategoryFormDialog(Frame owner, Category category) {
    super(owner, category == null ? "New category" : "Edit category", true);

    this.colorCombo = new JComboBox<>(Color.values());
    this.colorCombo.setRenderer(new ColorCellRenderer());

    if (category != null) {
      nameField.setText(category.getName());
      colorCombo.setSelectedItem(category.getColor());
    }

    buildLayout(owner);
  }

  private void buildLayout(Frame owner) {
    JPanel form = new JPanel(new GridBagLayout());
    form.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    addRow(form, gbc, 0, "Name:", nameField);
    addRow(form, gbc, 1, "Color:", colorCombo);

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
    if (getCategoryName().isEmpty()) {
      JOptionPane.showMessageDialog(
          this, "Name cannot be empty.", "Validation", JOptionPane.WARNING_MESSAGE);
      return;
    }
    confirmed = true;
    dispose();
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public String getCategoryName() {
    return nameField.getText().trim();
  }

  public Color getSelectedColor() {
    return (Color) colorCombo.getSelectedItem();
  }
}
