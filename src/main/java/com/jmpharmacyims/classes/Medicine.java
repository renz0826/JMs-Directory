package com.jmpharmacyims.classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class Medicine {
    // Fields
    private String name;
    private String brand;
    private String purpose;
    private String expirationDate;
    private int amount;
    private double price;

    @JsonCreator
    Medicine(
        @JsonProperty("name") String name, 
        @JsonProperty("brand") String brand, 
        @JsonProperty("purpose") String purpose, 
        @JsonProperty("expirationDate") String expirationDate, 
        @JsonProperty("amount") int amount,
        @JsonProperty("price") double price
    ) {
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
}