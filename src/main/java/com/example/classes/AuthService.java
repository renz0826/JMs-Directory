package com.example.classes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AuthService {
    private static List<Path> customerFiles;

    public static Admin logInAdmin(String username, String password) {
        Admin admin = Database.load(Database.getAdminFilePath(), Admin.class);
        
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
        Pharmacy pharmacy = Database.load(Database.getPharmacyFilePath(), Pharmacy.class);
        
        if (pharmacy == null) {
            System.out.println("Error occured");
            return null;
        }

        if (pharmacy.getUsername().equals(username) && pharmacy.getPassword().equals(password)) {
            System.out.println("Pharmacy authorized");
            return pharmacy;
        } else {
            System.out.println("Unauthorized");
            return null;
        }
    }

    public static Customer logInCustomer(String username, String password) {
        customerFiles = Database.getCustomerJsonFileList();
        Customer customer;
        for (Path path : customerFiles) {
            if (Files.isRegularFile(path)) {
                customer = Database.load(path, Customer.class);
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
