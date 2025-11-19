package com.example.classes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AuthService {
    private static Path customersDatabasePath = Path.of("accounts", "customers");
    private static Path pharmaciesDatabasePath = Path.of("accounts", "pharmacies");
    private static Path adminFilePath = Path.of("accounts", "admin.json");
    private static List<Path> clientFiles;

    public static Admin logInAdmin(String username, String password) {
        Admin admin = Database.loadAdmin(adminFilePath);
        
        if (admin == null) {
            System.out.println("Error occured");
            return null;
        }

        if (admin.getUsername().equals(username) && admin.getPassword().equals(password)) {
            System.out.println("Admin authorized");
            return admin;
        } else {
            System.out.println("Unauthorized");
            return null;
        }
    }

    public static Pharmacy logInPharmacy(String username, String password) {
        clientFiles = Database.getJsonFilePaths(pharmaciesDatabasePath);
        Pharmacy pharmacy;

        for (Path path : clientFiles) {
            if (Files.isRegularFile(path)) {
                pharmacy = Database.loadPharmacy(path);
                if (pharmacy == null) {
                    System.out.println("Error occured");
                    return null;
                }

                if (pharmacy.getUsername().equals(username) && pharmacy.getPassword().equals(password)) {
                    System.out.println("Pharmacy authorized");
                    return pharmacy;
                }
            }
        }
        
        // No credentials matched after traversing through pharmacies
        System.out.println("Unauthorized");
        return null;
    }

    public static Customer logInCustomer(String username, String password) {
        clientFiles = Database.getJsonFilePaths(customersDatabasePath);
        Customer customer;
        for (Path path : clientFiles) {
            if (Files.isRegularFile(path)) {
                customer = Database.loadCustomer(path);
                if (customer == null) {
                    System.out.println("Error occured");
                    return null;
                }

                if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                    System.out.println("Customer authorized");
                    return customer;
                }
            }
        }
        
        return null;
    }
}
