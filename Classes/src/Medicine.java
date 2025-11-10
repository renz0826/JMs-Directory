import java.time.LocalDateTime;

class Medicine {
    // Fields
    private String name;
    private String brand;
    private String purpose;
    private LocalDateTime expirationDate;
    private int amount;
    private double price;

    public Medicine(String name, String brand, String purpose, LocalDateTime expirationDate, int amount, double price) {
        this.name = name;
        this.brand = brand;
        this.purpose = purpose;
        this.expirationDate = expirationDate;
        setAmount(amount);
        setPrice(price);
    }
    // Getters
    public String getName() {
        return name;
    }
    public String getBrand() {
        return brand;
    }
    public String getPurpose() {
        return purpose;
    }
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
    public int getAmount() {
        return amount;
    }
    public double getPrice() {
        return price;
    }
    

    // Setters
    public void setAmount(int amount) {
        if (amount < 0) {
            this.amount = 0;
        } else {
            this.amount = amount;
        }
    }
    public void setPrice(double price) {
        if (price < 0) {
            this.price = 0;
        } else {
            this.price = price;
        }
    }
}