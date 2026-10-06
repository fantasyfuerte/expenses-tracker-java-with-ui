import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Expense {
  private UUID id;
  private double amount;
  private String description;
  private Category category;
  private LocalDateTime rawDate;
  private String date;

  public Expense(double amount, String description, Category category, LocalDateTime rawDate) {
    this.amount = amount;
    this.description = description;
    this.category = category;
    this.rawDate = rawDate;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    this.date = rawDate.format(format);
    this.id = UUID.randomUUID();
  }

  public UUID getId() {
    return this.id;
  }

  public String getDescription() {
    return this.description;
  }

  public double getAmount() {
    return this.amount;
  }

  public LocalDateTime getRawDate() {
    return this.rawDate;
  }

  public String getFormatedDate() {
    return this.date;
  }

  public Category getCategory() {
    return this.category;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public void setRawDate(LocalDateTime rawDate) {
    this.rawDate = rawDate;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public String getInfo() {
    return "Expense of "
        + this.amount
        + " for "
        + this.description
        + " on "
        + this.date
        + ". Belongs to "
        + this.category.getName()
        + " category.";
  }
}
