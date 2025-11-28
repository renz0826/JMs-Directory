package com.jmpharmacyims.classes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jmpharmacyims.classes.TextColor.Color;

public class Admin extends Account {

    private List<Customer> customers;
    private Pharmacy pharmacy;

    @JsonCreator
    Admin(
            @JsonProperty("name") String name,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password) {
        super(name, username, password);

        customers = Database.loadCustomers();
        pharmacy = Database.load(Database.getPharmacyFilePath(), Pharmacy.class);
    }

    // CREATE
    // Method to create a new customer account
    public void addCustomerAccount() {
        UIManager.displayTitle("+ Register A New Customer Account +");
        System.out.println(TextColor.apply("\nEnter Customer details.", Color.LIGHT_YELLOW));
        String name = InputHandler.readInput("\nName >> ");
        String username = InputHandler.readInput("\nUsername >> ");
        String password = InputHandler.readInput("\nPassword >> ");
        List<Medicine> medicines = List.of();

        Customer newCustomer = new Customer(name, username, password, medicines, 0);
        Database.createNew(newCustomer);
        UIManager.loading("Registering customer");
        Database.loadCustomers(); // refresh the list so the new accounts are included
    }

    // READ
    public List<Customer> searchCustomer(String targetName) {
        List<Customer> matched = new ArrayList<>();

        for (Customer customer : customers) {
            if (customer.getName().toLowerCase().contains(targetName.toLowerCase())) {
                matched.add(customer);
            }
        }

        if (matched.isEmpty()) {
            return null;
        }
        return matched;
    }

    // UPDATE
    public void updateCustomerDetails(String targetName) {
        Customer customer = getCustomer(targetName);

        System.out.println(TextColor.apply("\nEnter New Customer details.", Color.LIGHT_YELLOW));
        String username = InputHandler.readInput("\nUsername >> ");
        String password = InputHandler.readInput("\nPassword >> ");

        customer.setUsername(username);
        customer.setPassword(password);

        Database.save(customer);

        UIManager.loading("Updating credentials");
        MessageLog.logSuccess(targetName + "'s credentials has been successfully updated.");
    }

    public void updatePharmacyDetails() {

        System.out.println(TextColor.apply("\nEnter New Pharmacy details.", Color.LIGHT_YELLOW));
        String username = InputHandler.readInput("\nUsername >> ");
        String password = InputHandler.readInput("\nPassword >> ");

        pharmacy.setUsername(username);
        pharmacy.setPassword(password);

        Database.save(pharmacy);

        UIManager.loading("Updating credentials");
        MessageLog.logSuccess(pharmacy.getName() + "'s credentials has been successfully updated.");
    }

    // DELETE
    public void deleteCustomer(String targetName) {
        Customer customer = getCustomer(targetName);
        customers.remove(customer);
        Database.delete(customer);
        Database.loadCustomers();

        UIManager.loading("Deleting customer");
        MessageLog.logSuccess(targetName + "'s account has been successfully deleted.");
    }

    // GETTERS
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
}
