package com.example.classes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Admin extends Account {
    private List<Customer> customers;
    private Pharmacy pharmacy;
    
    @JsonCreator
    Admin(
        @JsonProperty("name") String name,
        @JsonProperty("username") String username,
        @JsonProperty("password") String password) {
        super(name, username, password);

        loadCustomers();
        pharmacy = Database.load(Database.getPharmacyFilePath(), Pharmacy.class);
    }

    // CREATE
    // Method to create a new customer account
    public void addCustomerAccount() {
        System.out.println("Enter Customer details.");
        String name = InputHandler.readInput("Name: ");
        String username = InputHandler.readInput("Username: ");
        String password = InputHandler.readInput("Password: ");
        List<Medicine> medicines = List.of();

        Customer newCustomer = new Customer(name, username, password, medicines, 0);
        Database.createCustomer(newCustomer);
        loadCustomers(); // refresh the list so the new accounts are included
    }

    // Method to create a new pharmacy account
    public void addPharmacyAccount() {
        System.out.println("Enter Pharmacy Details.");
        String name = InputHandler.readInput("Name: ");
        String username = InputHandler.readInput("Username: ");
        String password = InputHandler.readInput("Password: ");
        List<Medicine> medicines = List.of();

        Pharmacy newPharmacy = new Pharmacy(name, username, password, medicines);
        Database.createAccount(newPharmacy);
    private void loadCustomers() {
        customers = new ArrayList<>();

        for (Path path : Database.getCustomerJsonFileList()) {
            Customer customer = Database.load(path, Customer.class);
            customers.add(customer);
        }
    }
}
