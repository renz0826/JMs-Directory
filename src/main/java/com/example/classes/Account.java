package com.example.classes;

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

    // Login method
    public void login() {
        System.out.println("====== Login System ======");

        boolean isValid;
        do {
            // Ask for username
            String username = InputHandler.readNonEmptyLine("Enter username: ");
            
            // Ask for password
            String password = InputHandler.readNonEmptyLine("Enter password: ");

            isValid = isCredentialsCorrect(username, password); 
            if (!isValid) {
                System.out.println("Credentials are not correct, Try again!");
            }
        } while (!isValid);
    };

    // Logout method
    public void logout() {};

    // Credential validation method
    public boolean isCredentialsCorrect(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    };

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

    // Test method
    public void details() {
        System.out.println(name);
        System.out.println(username);
        System.out.println(password);
    }
}