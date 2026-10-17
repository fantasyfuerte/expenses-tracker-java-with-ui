import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CategoryCellRenderer extends DefaultTableCellRenderer {
  @Override
  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    setHorizontalAlignment(SwingConstants.CENTER);

    if (value instanceof Category category) {
      setText(category.getName());
      if (!isSelected) {
        java.awt.Color background = ColorUtil.toAwt(category.getColor());
        setBackground(background);
        setForeground(ColorUtil.contrast(background));
      }
    } else {
      setText("No category");
      if (!isSelected) {
        setBackground(table.getBackground());
        setForeground(java.awt.Color.GRAY);
      }
    }
    return this;
  }
}
