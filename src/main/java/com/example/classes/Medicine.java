package com.example.classes;

class Medicine {
    // Fields
    private String name;
    private String brand;
    private String purpose;
    private String expirationDate;
    private int amount;
    private double price;

    public Medicine(String name, String brand, String purpose, String expirationDate, int amount, double price) {
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
    public String getExpirationDate() {
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

    // Test method
    public void details() {
        System.out.println("====== Medicine Details ======");
        System.out.println("Name: " + name);
        System.out.println("Brand: " + brand);
        System.out.println("Purpose: " + purpose);
        System.out.println("Expiration Date: " + expirationDate);
        System.out.println("Amount: " + amount);
        System.out.println("Price: $" + price);
        System.out.println("===============================");
    }
}