public class Category {
  private String name;
  private String color;
  private int id;

  public Category(String name, int id) {
    this.name = name;
    this.id = id;
    this.color = "default";
  }

  public Category(String name, String color, int id) {
    this.name = name;
    this.color = color;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getId() {
    return id;
  }
}
