import java.util.UUID;

public class Category {
  private String name;
  private Color color = Color.LIGHT_GRAY;
  private UUID id;

  public Category(String name) {
    this.name = name;
    this.id = UUID.randomUUID();
  }

  public Category(String name, Color color) {
    this.name = name;
    this.color = color;
    this.id = UUID.randomUUID();
  }

  public String getName() {
    return name;
  }

  public Color getColor() {
    return color;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public UUID getId() {
    return id;
  }
}
