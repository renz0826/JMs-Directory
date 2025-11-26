package com.example.classes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AuthService {
    private static List<Path> customerFiles;

    public static Admin logInAdmin(String username, String password) {
        Admin admin = Database.load(Database.getAdminFilePath(), Admin.class);
        
        // Propagate null
        if (admin == null) { return null; }

        if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            MessageLog.addMessage("[SUCCESS]: Admin authorized!");
            return admin;
        } else {
            MessageLog.addMessage("[ERROR]: Unauthorized!");
            return null;
        }
    }

    public static Pharmacy logInPharmacy(String username, String password) {
        Pharmacy pharmacy = Database.load(Database.getPharmacyFilePath(), Pharmacy.class);
        
        // Propagate null
        if (pharmacy == null) { return null; }

        if (pharmacy.getUsername().equals(username) && pharmacy.getPassword().equals(password)) {
            MessageLog.addMessage("[SUCCESS]: Pharmacy authorized");
            return pharmacy;
        } else {
            MessageLog.addMessage("[ERROR]: Unauthorized");
            return null;
        }
    }

    public static Customer logInCustomer(String username, String password) {
        customerFiles = Database.getCustomerJsonFileList();

        // Propagate null
        if (customerFiles == null) { return null; }

        Customer customer;
        for (Path path : customerFiles) {
            if (Files.isRegularFile(path)) {
                customer = Database.load(path, Customer.class);
                if (customer == null) {
                    return null;
                }

                if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                    MessageLog.addMessage("[SUCCESS]: Customer authorized.");
                    return customer;
                }
            }
        }
        
        return null;
    }

    // METHODS CREATED DURING REFACTORIZATION
    public static Account verifyCredentials(String username, String password, int accountChoice) {
        return switch (accountChoice) {
            case MenuOption.AccountType.CUSTOMER ->
                AuthService.logInCustomer(username, password);
            case MenuOption.AccountType.PHARMACY ->
                AuthService.logInPharmacy(username, password);
            case MenuOption.AccountType.ADMIN ->
                AuthService.logInAdmin(username, password);
            default -> null;
        };
    }
}
