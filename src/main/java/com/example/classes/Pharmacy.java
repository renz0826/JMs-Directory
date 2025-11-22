package com.example.classes;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        System.out.println("Enter medicine details.");
        String name = InputHandler.readNonEmptyLine("Name: ");
        double price = InputHandler.readDouble("Price (PHP): ");
        int amount = InputHandler.readInt("Initial amount: ");
        String expirationDate = InputHandler.readDate("Expiration Date (d/m/yyyy): ");
        String brand = InputHandler.readNonEmptyLine("Brand: ");
        String purpose = InputHandler.readNonEmptyLine("Purpose: ");

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

    public void updateMedicineAmount(String targetName, int amount) {
        Medicine medicine = getMedicine(targetName);

        // set to 0 if amount would fall under 0
        int result = medicine.getAmount() + amount;
        if (result < 0) { medicine.setAmount(0); } 
        else { medicine.setAmount(result); }
        Database.saveToFile(temporaryFile, permanentFile, this);
    }

    public void updateMedicinePrice() {}
    public void deleteMedicine() {}

    // Getters
    public List<Medicine> getMedicines() {
        return medicines;
    }

    public Medicine getMedicine(String targetName) {
        for (Medicine medicine : medicines) {
            if (medicine.getName().toLowerCase().equals(targetName.toLowerCase())) {
                return medicine;
            }
        }

        return null;
    }

    // Test methods
    @Override
    public void details() {
        super.details();
        for (Medicine medicine : medicines) {
            medicine.details();
        }
    }
}