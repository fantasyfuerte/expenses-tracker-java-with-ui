public final class ColorUtil {
  private ColorUtil() {}

  public static java.awt.Color toAwt(Color color) {
    if (color == null) {
      return new java.awt.Color(0xB0, 0xBE, 0xC5);
    }
    return switch (color) {
      case RED -> new java.awt.Color(0xE5, 0x39, 0x35);
      case GREEN -> new java.awt.Color(0x43, 0xA0, 0x47);
      case BLUE -> new java.awt.Color(0x1E, 0x88, 0xE5);
      case YELLOW -> new java.awt.Color(0xFD, 0xD8, 0x35);
      case PURPLE -> new java.awt.Color(0x8E, 0x24, 0xAA);
      case ORANGE -> new java.awt.Color(0xFB, 0x8C, 0x00);
      case PINK -> new java.awt.Color(0xD8, 0x1B, 0x60);
      case GRAY -> new java.awt.Color(0x75, 0x75, 0x75);
      case LIGHT_GRAY -> new java.awt.Color(0xE0, 0xE0, 0xE0);
      case DARK_GRAY -> new java.awt.Color(0x42, 0x42, 0x42);
      case BLACK -> new java.awt.Color(0x21, 0x21, 0x21);
      case WHITE -> new java.awt.Color(0xFA, 0xFA, 0xFA);
    };
  }

  /** Devuelve texto negro o blanco segun la luminancia del fondo. */
  public static java.awt.Color contrast(java.awt.Color background) {
    double luminance =
        (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue())
            / 255.0;
    return luminance > 0.6 ? new java.awt.Color(0x21, 0x21, 0x21) : java.awt.Color.WHITE;
  }
}
