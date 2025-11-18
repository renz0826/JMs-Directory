package com.example.classes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AuthService {
    private static Path customersDatabasePath = Path.of("accounts", "customers");
    private static Path pharmaciesDatabasePath = Path.of("accounts", "pharmacies");
    private static Path adminFilePath = Path.of("accounts", "admin.json");
    private static DirectoryStream<Path> clientFiles;

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
        clientFiles = Database.listJsonFiles(pharmaciesDatabasePath);
        Pharmacy pharmacy;
        for (Path path : clientFiles) {
            if (Files.isRegularFile(path)) {
                pharmacy = Database.loadPharmacy(path);
                // authentication logic.....
            }
        }
        return null;
    }

    public static Customer logInCustomer(String username, String password) {
        clientFiles = Database.listJsonFiles(customersDatabasePath);
        Customer customer;
        for (Path path : clientFiles) {
            if (Files.isRegularFile(path)) {
                System.out.println("File: " + path);
                // authentication logic.....
            }
        }
        return null;
    }
}
