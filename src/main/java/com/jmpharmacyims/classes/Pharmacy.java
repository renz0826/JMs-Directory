package com.jmpharmacyims.classes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jmpharmacyims.classes.TextColor.Color;

public class Pharmacy extends Account {
    // Necessary initializations
    private List<Medicine> medicines;

    @JsonCreator
    Pharmacy(
        @JsonProperty("name") String name, 
        @JsonProperty("username") String username, 
        @JsonProperty("password") String password,
        @JsonProperty("medicines") List<Medicine> medicines
    ) {
        super(name, username, password);
        this.medicines = medicines;
    }

    // CRUD Methods
    public void addMedicine() {
        // Prompt line and Input validation
        UIManager.clearScreen();
        System.out.println(AsciiTableBuilder.buildSingleRow("+ Add New Medicine +"));
        System.out.println(TextColor.apply("Instructions: Enter medicine details.", Color.LIGHT_YELLOW));
        String name = InputHandler.readInput("Name >> ");
        double price = InputHandler.readDouble("Price (PHP) >> ");
        int amount = InputHandler.readInt("Initial amount >> ");
        String expirationDate = InputHandler.readMedicineDate("Expiration Date (d/m/yyyy) >> ");
        String brand = InputHandler.readInput("Brand >> ");
        String purpose = InputHandler.readInput("Purpose >> ");

        // Save sanitized inputs to database
        Medicine newMedicine = new Medicine(name, brand, purpose, expirationDate, amount, price);
        medicines.add(newMedicine);
        Database.save(this);
    }

    /**
     * Method to search for medicines that contains a matching substring from targetName 
     * @param targetName
     * @return a list of medicines whose names contains the substring or null if none
     */
    public List<Medicine> searchMedicine(String targetName) {
        List<Medicine> matched = new ArrayList<>();

        for (Medicine medicine : medicines) {
            if (medicine.getName().toLowerCase().contains(targetName.toLowerCase())) {
                matched.add(medicine);
            }
        }

        if (matched.isEmpty()) return null;
        return matched;
    }
    
    // Method to update medicine amount
    public void updateMedicineAmount(String targetName, int amount) {
        Medicine medicine = getMedicine(targetName);

        // set to 0 if amount would fall under 0
        int result = medicine.getAmount() + amount;
        if (result < 0) { medicine.setAmount(0); } 
        else { medicine.setAmount(result); }
        Database.save(this);
    }

    // Method to update medicine price
    public void updateMedicinePrice(String targetName, double amount) {
        Medicine medicine = getMedicine(targetName);

        if (amount < 0) { medicine.setAmount(0); }
        medicine.setPrice(amount);
        Database.save(this);
    }

    // Method to delete a medicine from list
    public void deleteMedicine(String targetName) {
        Medicine medicine = getMedicine(targetName);
        medicines.remove(medicine);
        Database.save(this);
    }

    // Getters
    public List<Medicine> getMedicines() {
        return medicines;
    }

    // returns the medicine that matches the targetName
    public Medicine getMedicine(String targetName) {
        for (Medicine medicine : medicines) {
            if (medicine.getName().equalsIgnoreCase(targetName)) {
                return medicine;
            }
        }

        return null;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}