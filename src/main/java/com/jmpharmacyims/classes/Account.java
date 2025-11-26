package com.jmpharmacyims.classes;

// Base class account
public class Account{
    // Sets username and password's max size to 128 characters
    final protected int MAX_USERNAME_SIZE = 128;
    final protected int MAX_PASSWORD_SIZE = 128;

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