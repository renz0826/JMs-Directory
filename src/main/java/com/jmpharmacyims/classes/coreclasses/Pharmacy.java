package com.jmpharmacyims.classes.coreclasses;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.jmpharmacyims.classes.uimanager.InputHandler;
import com.jmpharmacyims.classes.uimanager.MessageLog;
import com.jmpharmacyims.classes.uimanager.TextColor;
import com.jmpharmacyims.classes.uimanager.TextColor.Color;
import com.jmpharmacyims.classes.uimanager.UIManager;

public class Pharmacy extends Account implements CanEditCredentials {

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
        UIManager.displayTitle("+ Add New Medicine +");
        System.out.println(TextColor.apply("\nEnter medicine details.", Color.LIGHT_YELLOW));
        String name = InputHandler.readInput("\nName >> ");
        double price = InputHandler.readDouble("\nPrice (PHP) >> ");
        int amount = InputHandler.readInt("\nInitial amount >> ");
        String expirationDate = InputHandler.readMedicineDate("\nExpiration Date (d/m/yyyy) >> ");
        String brand = InputHandler.readInput("\nBrand >> ");
        String purpose = InputHandler.readInput("\nPurpose >> ");

        // Save sanitized inputs to database
        Medicine newMedicine = new Medicine(name, brand, purpose, expirationDate, amount, price);
        medicines.add(newMedicine);
        Database.save(this);

        UIManager.loading("Adding medicine");
        MessageLog.logSuccess(name + " has been successfully added to the inventory.\n");
        MessageLog.displayNext();
    }

    /**
     * Method to search for medicines that contains a matching substring from
     * targetName
     *
     * @param targetName
     * @return a list of medicines whose names contains the substring or null if
     * none
     */
    public List<Medicine> searchMedicine(String targetName) {
        List<Medicine> matched = new ArrayList<>();

        for (Medicine medicine : medicines) {
            if (medicine.getName().toLowerCase().contains(targetName.toLowerCase())) {
                matched.add(medicine);
            }
        }

        if (matched.isEmpty()) {
            return null;
        }
        return matched;
    }

    // Method to update medicine amount
    public void updateMedicineAmount(String targetName, int amount) {
        Medicine medicine = getMedicine(targetName);

        // set to 0 if amount would fall under 0
        int result = medicine.getAmount() + amount;
        if (result < 0) {
            medicine.setAmount(0);
        } else {
            medicine.setAmount(result);
        }
        Database.save(this);

        UIManager.loading("Updating medicine amount");
    }

    // Method to update medicine price
    public void updateMedicinePrice(String targetName, double amount) {
        Medicine medicine = getMedicine(targetName);

        if (amount < 0) {
            medicine.setAmount(0);
        }
        medicine.setPrice(amount);
        Database.save(this);

        UIManager.loading("Updating medicine price");
    }

    // Method to delete a medicine from list
    public void deleteMedicine(String targetName) {
        Medicine medicine = getMedicine(targetName);
        medicines.remove(medicine);
        Database.save(this);

        UIManager.loading("Deleting medicine");
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

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public void setMedicines(List<Medicine> medicines) {
        if (!medicines.isEmpty()) {
            this.medicines = medicines;
        }
    }
}
