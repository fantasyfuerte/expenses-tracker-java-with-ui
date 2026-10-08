import java.util.ArrayList;

public class CategoryTracker {
  private ArrayList<Category> categories;

  public CategoryTracker() {
    this.categories = new ArrayList<Category>();
  }

  public Category getCategory(int index) {
    return this.categories.get(index);
  }

  public Category addCategory(String name, Color color) {
    Category category = new Category(name, color);
    this.categories.add(category);
    return category;
  }

  public Category removeCategory(int index) {
    Category category = categories.get(index);
    this.categories.remove(index);
    return category;
  }

  public Category updateCategory(int index, String name, Color color) {
    Category category = getCategory(index);
    if (name != null) {
      category.setName(name);
    }
    if (color != null) {
      category.setColor(color);
    }
    return category;
  }

  public void displayCategories() {
    for (Category category : categories) {
      category.displayInfo();
    }
  }
}
