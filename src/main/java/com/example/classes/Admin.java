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

    // READ
    public List<Customer> searchCustomer(String targetName) {
        List<Customer> matched = new ArrayList<>();

        for (Customer customer : customers) {
            if (customer.getName().toLowerCase().contains(targetName.toLowerCase())) {
                matched.add(customer);
            }
        }

        if (matched.isEmpty()) return null;
        return matched;
    }

    // UPDATE
    public void updateCustomerDetails(String targetName) {
        Customer customer = getCustomer(targetName);

        System.out.println("Enter New Customer details.");
        String name = InputHandler.readInput("Name: ");
        String username = InputHandler.readInput("Username: ");
        String password = InputHandler.readInput("Password: ");


        Database.save(customer);
    }

    public void updatePharamacyDetails() {
        System.out.println("Enter New Pharmacy details.");
        String name = InputHandler.readInput("Name: ");
        String username = InputHandler.readInput("Username: ");
        String password = InputHandler.readInput("Password: ");

        pharmacy.setName(name);
        pharmacy.setUsername(username);
        pharmacy.setPassword(password);

        Database.save(pharmacy);
    }

    // DELETE
    public void deleteCustomer(String targetName) {
        Customer customer = getCustomer(targetName);
        customers.remove(customer);
        Database.delete(customer);
        loadCustomers();
    }

    // getters
    public List<Customer> getCustomers() {
        return customers;
    }

    // HELPER METHODS
    private Customer getCustomer(String targetName) {
        for (Customer customer : customers) {
            if (customer.getName().equalsIgnoreCase(targetName)) {
                return customer;
            }
        }

        return null;
    }

    private void loadCustomers() {
        customers = new ArrayList<>();

        for (Path path : Database.getCustomerJsonFileList()) {
            Customer customer = Database.load(path, Customer.class);
            customers.add(customer);
        }
    }
}
