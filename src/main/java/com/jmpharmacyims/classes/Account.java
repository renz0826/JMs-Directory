package com.jmpharmacyims.classes;

// Base class account
public class Account{
    protected String name;
    protected String username;
    protected String password;

    // Base class constructor
    protected Account(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
}