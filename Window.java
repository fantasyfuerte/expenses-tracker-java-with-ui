import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Window {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          JFrame frame = new JFrame("Expenses Tracker");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          JLabel label = new JLabel("Work in progress", SwingConstants.CENTER);
          frame.add(label);

          frame.pack();
          frame.setSize(400, 300);
          frame.setLocationRelativeTo(null);
          frame.setVisible(true);
        });
  }
}
