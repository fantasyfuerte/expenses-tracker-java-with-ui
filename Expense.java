import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Expense {
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

  public String getInfo() {
    return "Expense of " + this.amount + " for " + this.description + " on " + this.date;
  }
}
