package com.jmpharmacyims.classes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.jmpharmacyims.classes.MenuOption.*;
import com.jmpharmacyims.classes.TextColor.Color;
// UI class for design
class UIManager {

    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Start menu, guides the user which account to pick (or logout)
    public static void chooseAccountMenu() {
        while (true) {
            UIManager.clearScreen();
            UIManager.displayProgramLogo();
            UIManager.displayChooseAccountMenu();
            MessageLog.displayAll();

            // ask user which account to login
            int accountChoice = InputHandler.getValidChoice(AccountType.getValues());
            if (accountChoice == MenuOption.AccountType.LOGOUT) { break; }
            UIManager.login(accountChoice);
        }
    }

    // Login menu, happens after choosing an account 
    public static void login(int accountChoice) {
        Account account;

        do {
            UIManager.clearScreen();
            UIManager.displayLoginTitle();
            
            // Prompt user credentials
            String username = InputHandler.readInput("\nEnter Username\nUsername >> ");
            String password = InputHandler.readInput("\nEnter Password\nPassword >> ");
            account = AuthService.verifyCredentials(username, password, accountChoice);

            // A null account means verification failed
            if (account == null) {
                boolean continueToAttemptLogin = UIManager.retryLogin();
                if (!continueToAttemptLogin) { return; }
            }
        } while (account == null);

        UIManager.routeToAccountMenu(account);
    }

    public static void runCustomerMenu(Customer customer) {
        boolean continueMenuLoop = true;

        do {
            // Clear screen when entering
            UIManager.clearScreen();
            UIManager.displayCustomerMenu();
            // Display any error messages
            MessageLog.displayNext();

            // Get valid choices from user
            int choice = InputHandler.getValidChoice(CustomerOperation.getValues());

            switch (choice) {
                case CustomerOperation.BUY_MEDICINE -> {
                    UIManager.clearScreen();
                    customer.buyMedicine();
                }
                case CustomerOperation.VIEW_ACCOUNT_DETAILS -> customer.viewAccountDetails();
                case CustomerOperation.DEPOSIT_FUNDS -> {
                    boolean stayingInAddMenu = true;
                    do {
                        // 1. Perform the action FIRST
                        customer.depositFunds();
                        MessageLog.displayAll();
                        // 2. Then ask what to do next
                        System.out.println(AsciiTableBuilder.buildSingleRow("Would you like to deposit again? (y/n)"));
                        if (InputHandler.promptYesOrNo()) { continue; }
                        else { stayingInAddMenu = false; }
                    } while (stayingInAddMenu);
                }
                case CustomerOperation.LOGOUT -> {
                    System.out.println("\nExiting...");
                    continueMenuLoop = false;
                }
            }
        } while (continueMenuLoop);
    }

    public static void runPharmacyMenu(Pharmacy pharmacy) {
        boolean continueMenuLoop = true;

        do {
            // Clear screen at start of every loop
            UIManager.clearScreen();
            // Render table menu
            UIManager.displayPharmacyMenu();
            // Display any errors
            MessageLog.displayAll();

            // Initialize choice
            int mainChoice = InputHandler.getValidChoice(PharmacyOperation.getValues());

            // Handle Main Menu Actions
            switch (mainChoice) {
                case PharmacyOperation.LOGOUT -> {
                    System.out.println("\nLogging out...");
                    continueMenuLoop = false;
                }
                case PharmacyOperation.ADD_MEDICINE -> {
                    // --- SUB MENU LOOP ---
                    do {
                        UIManager.clearScreen();
                        pharmacy.addMedicine(); // 1. Perform the action FIRST
                        UIManager.clearScreen();
                        String message = "Would you like to add another medicine? (y/n)";
                        System.out.println(AsciiTableBuilder.buildSingleRow(message));

                        if (InputHandler.promptYesOrNo()) continue; // 2. Then ask what to do next
                        else break;
                    } while (true);
                }
                case PharmacyOperation.SHOW_MEDICINE_LIST -> {
                    // display all medicines once
                    List<Medicine> medicines = pharmacy.getMedicines();
                    
                    do {
                        UIManager.clearScreen();
                        System.out.println(AsciiTableBuilder.buildSingleRow("+ Medicine Inventory +"));
                        displayMedicineTable(medicines);
                        MessageLog.displayNext();

                        System.out.println("Search medicine by name or enter 'q' to exit.");
                        String targetName = InputHandler.readInput("Enter >> ");
                        if (targetName.equalsIgnoreCase("q")) { break; }
                        List<Medicine> found = pharmacy.searchMedicine(targetName);

                        if (found == null) {
                            // Reset to original list if target medicines are not found
                            MessageLog.addSuccess("No results found.");
                            medicines = pharmacy.getMedicines();
                        } else {
                            MessageLog.addSuccess("Returned " + found.size() + " results.");
                            medicines = found;
                        }
                    } while (true);
                    UIManager.clearScreen();
                }

                case 
                PharmacyOperation.UPDATE_MEDICINE_AMOUNT, 
                PharmacyOperation.UPDATE_MEDICINE_PRICE, 
                PharmacyOperation.DELETE_MEDICINE -> {
                    do {
                        List<Medicine> medicines = pharmacy.getMedicines();
                        UIManager.clearScreen();
                        // Display respective title
                        if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_AMOUNT) {
                            System.out.println(AsciiTableBuilder.buildSingleRow("+ Update Medicine Amount +"));
                        } else if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_PRICE) {
                            System.out.println(AsciiTableBuilder.buildSingleRow("+ Update Medicine Price +"));
                        } else {
                            System.out.println(AsciiTableBuilder.buildSingleRow("+ Delete A Medicine +"));
                        }
                        displayMedicineTable(medicines);
                        MessageLog.displayAll();

                        System.out.println("Instructions: ");
                        System.out.println("- Select medicine by entering its position number.");
                        System.out.println("- Search medicine by name or enter 'q' to exit.");

                        String input = InputHandler.readInput("Enter input >> ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) {
                            break;
                        }

                        // do not allow double for position
                        String doublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)";
                        if (input.matches(doublePattern)) {
                            MessageLog.addError("Enter a valid position");
                            continue;
                        }

                        // if number then select medicine, else search
                        int pos = 0;
                        String targetName;
                        try {
                            pos = Integer.parseInt(input);
                            targetName = medicines.get(pos).getName();
                        } catch (NumberFormatException e) {
                            List<Medicine> result = pharmacy.searchMedicine(input);
                            if (result == null) {
                                MessageLog.addSuccess("No results found");
                            } else {
                                medicines = result;
                            }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            MessageLog.addError("Invalid Position.");
                            continue;
                        }

                        if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_AMOUNT) {
                            int amount = InputHandler.readInt("Enter amount >> ", true);
                            pharmacy.updateMedicineAmount(targetName, amount);
                        } else if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_PRICE) {
                            double amount = InputHandler.readDouble("Enter new price >> ");
                            pharmacy.updateMedicinePrice(targetName, amount);
                        } else {
                            String message = "Are you sure you want to delete " + targetName + "? (y/n)";
                            System.out.println(AsciiTableBuilder.buildSingleRow(message));

                            // 2. Prompt user choice
                            if (InputHandler.promptYesOrNo()) { pharmacy.deleteMedicine(targetName); }
                            
                            // 3. Update list
                            medicines = pharmacy.getMedicines();
                        }
                    } while (true);
                }
            }
        } while (continueMenuLoop);
    }

    public static void runAdminMenu(Admin admin) {
        boolean continueMenuLoop = true;

        do {
            UIManager.clearScreen();
            // Print table and any error message
            UIManager.displayAdminMenu();
            MessageLog.displayAll();

            // Valid choices
            int choice = InputHandler.getValidChoice(AdminOperation.getValues());
            
            switch (choice) {
                case AdminOperation.REGISTER_CUSTOMER -> {
                    UIManager.clearScreen();
                    admin.addCustomerAccount();
                }
                case AdminOperation.SHOW_CUSTOMER_LIST -> {
                    List<Customer> customers = admin.getCustomers();
                    
                    do {
                        UIManager.clearScreen();
                        System.out.println(AsciiTableBuilder.buildSingleRow("+ List Of Registered Customer Accounts +"));
                        displayCustomerTable(customers);
                        MessageLog.displayNext();

                        System.out.println("Search customer by name or enter 'q' to exit.");
                        String targetName = InputHandler.readInput("Enter >> ");
                        if (targetName.equalsIgnoreCase("q")) break;
                        List<Customer> found = admin.searchCustomer(targetName);

                        if (found == null) {
                            MessageLog.addSuccess("No results found.");
                            customers = admin.getCustomers();
                        } else {
                            MessageLog.addSuccess("Returned " + found.size() + " results.");
                            customers = found;
                        }
                    } while (true);
                }
                // Customer update or delete
                case 
                AdminOperation.UPDATE_CUSTOMER_CREDENTIALS, 
                AdminOperation.DELETE_CUSTOMER -> {
                    List<Customer> customers = admin.getCustomers();

                    do {
                        UIManager.clearScreen();
                        // Display respective operation title
                        if (choice == AdminOperation.UPDATE_CUSTOMER_CREDENTIALS) {
                            System.out.println(AsciiTableBuilder.buildSingleRow("+ Update Customer Credentials +"));
                        } else {
                            System.out.println(AsciiTableBuilder.buildSingleRow("+ Remove A Customer Account +"));
                        }
                        displayCustomerTable(customers);
                        MessageLog.displayNext();

                        System.out.println("Instructions: ");
                        System.out.println("- Select a customer by entering its position number.");
                        System.out.println("- Search customer by name or enter 'q' to exit.");

                        String input = InputHandler.readInput("Enter input >> ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) break;

                        // do not allow double for position
                        String doublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)"; 
                        if (input.matches(doublePattern)) {
                            MessageLog.addError("Enter a valid position");
                            continue;
                        }
                        
                        // if number then select customer, else search
                        int pos = 0;
                        String targetName;
                        try {
                            pos = Integer.parseInt(input);
                            targetName = customers.get(pos).getName();
                        } catch (NumberFormatException e) {
                            List<Customer> found = admin.searchCustomer(input);
                            if (found == null) { 
                                MessageLog.addSuccess("No results found."); 
                                customers = admin.getCustomers(); // reset the table
                            }
                            else { customers = found; }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            MessageLog.addError("Invalid Position.");
                            continue;
                        }

                        if (choice == AdminOperation.UPDATE_CUSTOMER_CREDENTIALS) { 
                            admin.updateCustomerDetails(targetName); 
                        } else {
                            String message = "Are you sure you want to delete " + targetName + "? (y/n)";
                            System.out.println(AsciiTableBuilder.buildSingleRow(message));
                            if (InputHandler.promptYesOrNo()) { admin.deleteCustomer(targetName); }
                            customers = admin.getCustomers(); // update list
                        }
                    } while (true);
                }
                case AdminOperation.UPDATE_PHARMACY_CREDENTIALS -> {
                    UIManager.clearScreen();
                    System.out.println(AsciiTableBuilder.buildSingleRow("+ Update Pharmacy Credentials +"));
                    admin.updatePharmacyDetails();
                }
                case AdminOperation.LOGOUT -> {
                    System.out.println("\nExiting...");
                    continueMenuLoop = false;
                }
            }
        } while (continueMenuLoop);
    }

    public static void routeToAccountMenu(Account account) {
        // Call respective Account menu
        if (account instanceof Customer) {
            runCustomerMenu((Customer) account);
        } else if (account instanceof Pharmacy) {
            runPharmacyMenu((Pharmacy) account);
        } else if (account instanceof Admin) {
            runAdminMenu((Admin) account);
        }
    }

    public static boolean retryLogin() {
        MessageLog.displayAll();
        System.out.println("\nLogin failed.");
        System.out.println("Enter anything to try again or enter 'q' to exit.");
        String input = InputHandler.readInput("\nEnter Choice >> ", true);
        if (input.equals("q")) { return false; }
        return true;
    }

    private static void displayCustomerTable(List<Customer> customers) {
        System.out.println(AsciiTableBuilder.buildCustomerTable(customers));
    }

    public static void displayMedicineTable(List<Medicine> medicines) {
        System.out.println(AsciiTableBuilder.buildMedicineTable(medicines));
    };
    
    public static void displayChooseAccountMenu() {
        String table = new AsciiTableBuilder()
        .setHeader("+ Select Account Type +")
        .setRows("1. Customer", "2. Pharmacy", "3. Admin")
        .setFooter("0. Exit")
        .buildGenericMenuTable();

        System.out.println(table);
    }

    public static void displayLoginTitle() {
        System.out.println(AsciiTableBuilder.buildSingleRow("+ Login +"));
    }

    public static void displayCustomerMenu() {
        String table = new AsciiTableBuilder()
        .setHeader("+ Customer Menu +")
        .setRows("1. Buy Medicine", "2. View Account Details", "3. Deposit Funds")
        .setFooter("0. Logout")
        .buildGenericMenuTable();

        System.out.println(table);
    }

    public static void displayPharmacyMenu() {
        String[] rows = {
                "1. Add Medicine",
                "2. Show List of Medicines",
                "3. Update Medicine Amount",
                "4. Update Medicine Price",
                "5. Delete Medicine"
        };
        String table = new AsciiTableBuilder()
                .setHeader("+ Pharmacy Menu +")
                .setRows(rows)
                .setFooter("0. Logout")
                .buildGenericMenuTable();

        System.out.println(table);
    }

    public static void displayAdminMenu() {
        String[] rows = {
            "1. Register A Customer",
            "2. Show List Of Customers",
            "3. Edit Customer Credentials",
            "4. Edit Pharmacy Credentials",
            "5. Delete Customer"
        };
        String table = new AsciiTableBuilder()
                    .setHeader("+ Admin Menu +")
                    .setRows(rows)
                    .setFooter("0. Exit")
                    .buildGenericMenuTable();

        System.out.println(table);
    }

    public static void displayProgramLogo() {
        try {
            String logo = Files.readString(Path.of("assets", "title.txt"));
            System.out.println(TextColor.apply(logo, Color.LIGHT_GREEN));
        } catch (IOException e) {
            MessageLog.addError("Failed to load logo.");
            MessageLog.displayNext();
        }
    }

    public static void displayCustomerAccountDetails(Customer customer) {
        System.out.println(AsciiTableBuilder.buildSingleRow("+ Account Details +"));
        System.out.println(AsciiTableBuilder.buildCustomerAccountDetails(customer));
        System.out.println(AsciiTableBuilder.buildSingleRow("+ Medicine Cabinet +"));
    }
}
