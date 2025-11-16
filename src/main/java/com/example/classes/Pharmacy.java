package com.example.classes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Pharmacy extends Account {
    // Necessary initializations
    private static ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); // Pretty print json in file
    private List<Medicine> medicines = new ArrayList<>();

    // TODO: these are just test files, make them more adaptable later
    private Path permanentFile = Path.of(Account.ROOT_DIRECTORY, "pharmacies", "JmPharmacy.json");
    private Path temporaryFile = Path.of(permanentFile.toString() + ".tmp"); // temporary filepath

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
        AccountDatabaseHandler.saveToFile(temporaryFile, permanentFile, this);        
    }

    public void searchMedicine() {}
    public void updateMedicineAmount() {}
    public void updateMedicinePrice() {}
    public void deleteMedicine() {}

    // Getters
    public List<Medicine> getMedicines() {
        return medicines;
    }
}