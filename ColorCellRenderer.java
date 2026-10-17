import java.awt.Component;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Renders a domain {@link Color} as a small swatch followed by its pretty-printed name, so the
 * category color combo box reads "Light Gray" instead of "LIGHT_GRAY".
 */
public class ColorCellRenderer extends JLabel implements ListCellRenderer<Color> {
  public ColorCellRenderer() {
    setOpaque(true);
    setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
  }

  @Override
  public Component getListCellRendererComponent(
      JList<? extends Color> list,
      Color value,
      int index,
      boolean isSelected,
      boolean cellHasFocus) {
    if (value == null) {
      setText("");
      setIcon(null);
    } else {
      setText(prettify(value));
      setIcon(new Swatch(ColorUtil.toAwt(value)));
    }

    if (isSelected) {
      setBackground(list.getSelectionBackground());
      setForeground(list.getSelectionForeground());
    } else {
      setBackground(list.getBackground());
      setForeground(list.getForeground());
    }
    return this;
  }

  private static String prettify(Color color) {
    StringBuilder label = new StringBuilder();
    for (String word : color.name().split("_")) {
      if (label.length() > 0) {
        label.append(' ');
      }
      label.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
    }
    return label.toString();
  }

  private static final class Swatch implements Icon {
    private static final int SIZE = 14;

    private final java.awt.Color color;

    Swatch(java.awt.Color color) {
      this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(color);
      g.fillRect(x, y, SIZE, SIZE);
      g.setColor(java.awt.Color.GRAY);
      g.drawRect(x, y, SIZE - 1, SIZE - 1);
    }

    @Override
    public int getIconWidth() {
      return SIZE;
    }

    @Override
    public int getIconHeight() {
      return SIZE;
    }
  }
}
