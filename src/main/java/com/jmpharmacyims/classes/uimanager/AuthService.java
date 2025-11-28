package com.jmpharmacyims.classes.uimanager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.jmpharmacyims.classes.coreclasses.Account;
import com.jmpharmacyims.classes.coreclasses.Admin;
import com.jmpharmacyims.classes.coreclasses.Customer;
import com.jmpharmacyims.classes.coreclasses.Database;
import com.jmpharmacyims.classes.coreclasses.Pharmacy;

public class AuthService { 
    private static List<Path> customerFiles;

    public static Admin logInAdmin(String username, String password) {
        Admin admin = Database.load(Database.getAdminFilePath(), Admin.class);
        
        // Propagate null
        if (admin == null) { return null; }

        if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            MessageLog.logSuccess("Admin authorized!");
            return admin;
        } else {
            MessageLog.logError("Unauthorized!");
            return null;
        }
    }

    public static Pharmacy logInPharmacy(String username, String password) {
        Pharmacy pharmacy = Database.load(Database.getPharmacyFilePath(), Pharmacy.class);
        
        // Propagate null
        if (pharmacy == null) { return null; }

        if (pharmacy.getUsername().equals(username) && pharmacy.getPassword().equals(password)) {
            MessageLog.logSuccess("Pharmacy authorized");
            return pharmacy;
        } else {
            MessageLog.logError("Unauthorized");
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
                    MessageLog.logSuccess("Customer authorized.");
                    return customer;
                }
            }
        }
        
        return null;
    }

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
