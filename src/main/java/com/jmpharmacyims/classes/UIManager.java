package com.jmpharmacyims.classes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.jmpharmacyims.classes.MenuOption.AccountType;
import com.jmpharmacyims.classes.MenuOption.AdminOperation;
import com.jmpharmacyims.classes.MenuOption.CustomerOperation;
import com.jmpharmacyims.classes.MenuOption.PharmacyOperation;
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
            UIManager.displayBanner("title.txt");
            UIManager.displayChooseAccountMenu();
            MessageLog.displayAll();

            // ask user which account to login
            int accountChoice = InputHandler.readValidChoice(AccountType.getValues());
            if (accountChoice == MenuOption.AccountType.LOGOUT) {
                UIManager.loading("Exiting Program");
                System.out.println();
                break;
            }
            UIManager.login(accountChoice);
        }
    }

    // Login menu, happens after choosing an account 
    public static void login(int accountChoice) {
        Account account;

        do {
            UIManager.clearScreen();
            UIManager.displayTitle("+ Login +");

            // Prompt user credentials
            String username = InputHandler.readInput("\nEnter Username\nUsername >> ");
            String password = InputHandler.readInput("\nEnter Password\nPassword >> ");
            account = AuthService.verifyCredentials(username, password, accountChoice);

            // A null account means verification failed
            if (account == null) {
                boolean continueToAttemptLogin = UIManager.retryLogin();
                if (!continueToAttemptLogin) {
                    return;
                }
            }
        } while (account == null);
        UIManager.loading("Logging in");
        UIManager.routeToAccountMenu(account);
    }

    public static void runCustomerMenu(Customer customer) {
        boolean continueMenuLoop = true;

        do {
            // Clear screen when entering
            UIManager.clearScreen();
            UIManager.displayBanner("customer.txt");
            UIManager.displayCustomerMenu();
            // Display any error messages
            MessageLog.displayNext();

            // Get valid choices from user
            int choice = InputHandler.readValidChoice(CustomerOperation.getValues());

            switch (choice) {
                case CustomerOperation.BUY_MEDICINE -> {
                    UIManager.clearScreen();
                    customer.buyMedicine();
                }
                case CustomerOperation.VIEW_ACCOUNT_DETAILS ->
                    customer.viewAccountDetails();
                case CustomerOperation.DEPOSIT_FUNDS -> {
                    boolean stayingInAddMenu = true;
                    do {

                        customer.depositFunds();

                        System.err.println();
                        System.out.println(AsciiTableBuilder.buildSingleRow("Would you like to deposit again? (y/n)"));
                        if (InputHandler.promptYesOrNo()) {
                            continue;
                        } else {
                            stayingInAddMenu = false;
                        }
                    } while (stayingInAddMenu);
                }
                case CustomerOperation.LOGOUT -> {
                    UIManager.loading("Logging out");
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
            UIManager.displayBanner("pharmacy.txt");

            // Render table menu
            UIManager.displayPharmacyMenu();
            // Display any errors
            MessageLog.displayAll();

            // Initialize choice
            int mainChoice = InputHandler.readValidChoice(PharmacyOperation.getValues());

            // Handle Main Menu Actions
            switch (mainChoice) {
                case PharmacyOperation.LOGOUT -> {
                    UIManager.loading("Logging out");
                    continueMenuLoop = false;
                }
                case PharmacyOperation.ADD_MEDICINE -> {
                    do {
                        UIManager.clearScreen();
                        pharmacy.addMedicine(); // Perform the action FIRST
                        String message = "Would you like to add another medicine? (y/n)";
                        System.out.println(AsciiTableBuilder.buildSingleRow(message));

                        if (InputHandler.promptYesOrNo()) {
                            continue;
                        } else {
                            break;
                        }
                    } while (true);
                }
                case PharmacyOperation.SHOW_MEDICINE_LIST -> {
                    // display all medicines once
                    List<Medicine> medicines = pharmacy.getMedicines();

                    do {
                        UIManager.clearScreen();
                        UIManager.displayTitle("+ Medicine Inventory +");
                        displayMedicineTable(medicines);
                        MessageLog.displayNext();

                        System.out.println(TextColor.apply("\nSearch Instructions: ", Color.WHITE));
                        System.out.println(TextColor.apply("- Search medicine by name", Color.LIGHT_YELLOW));
                        System.out.println(TextColor.apply("- Enter 'q' to exit.", Color.LIGHT_RED));
                        String targetName = InputHandler.readInput("\nEnter >> ");
                        if (targetName.equalsIgnoreCase("q")) {
                            break;
                        }
                        List<Medicine> found = pharmacy.searchMedicine(targetName);

                        if (found == null) {
                            // Reset to original list if target medicines are not found
                            MessageLog.logError("No results found.");
                            medicines = pharmacy.getMedicines();
                        } else {
                            MessageLog.logSuccess("Returned " + found.size() + " results.");
                            medicines = found;
                        }
                    } while (true);
                    UIManager.clearScreen();
                }

                case PharmacyOperation.UPDATE_MEDICINE_AMOUNT, PharmacyOperation.UPDATE_MEDICINE_PRICE, PharmacyOperation.DELETE_MEDICINE -> {
                    do {
                        List<Medicine> medicines = pharmacy.getMedicines();
                        UIManager.clearScreen();
                        // Display respective title
                        if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_AMOUNT) {
                            UIManager.displayTitle("+ Update Medicine Amount +");
                        } else if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_PRICE) {
                            UIManager.displayTitle("+ Update Medicine Price +");
                        } else {
                            UIManager.displayTitle("+ Delete a Medicine +");
                        }
                        displayMedicineTable(medicines);
                        MessageLog.displayAll();

                        System.out.println(TextColor.apply("\nSelect Instructions: ", Color.WHITE));
                        System.out.println(TextColor.apply("- Select medicine by entering its position number.", Color.LIGHT_YELLOW));
                        System.out.println(TextColor.apply("- Select medicine by name", Color.LIGHT_YELLOW));
                        System.out.println(TextColor.apply("- Enter 'q' to exit.", Color.LIGHT_RED));

                        String input = InputHandler.readInput("\nEnter input >> ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) {
                            break;
                        }

                        // do not allow double for position
                        String doublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)";
                        if (input.matches(doublePattern)) {
                            MessageLog.logError("Enter a valid position");
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
                                MessageLog.logError("No results found");
                            } else {
                                medicines = result;
                            }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            MessageLog.logError("Invalid Position.");
                            continue;
                        }

                        if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_AMOUNT) {
                            int amount = InputHandler.readInt("\nEnter amount >> ", true);
                            pharmacy.updateMedicineAmount(targetName, amount);
                            MessageLog.logSuccess(targetName + "\'s amount has been successfully updated to " + amount + ".");
                        } else if (mainChoice == PharmacyOperation.UPDATE_MEDICINE_PRICE) {
                            double amount = InputHandler.readDouble("\nEnter new price >> ");
                            pharmacy.updateMedicinePrice(targetName, amount);
                            MessageLog.logSuccess(targetName + "\'s price has been successfully updated to " + amount + ".");
                        } else {
                            String message = "Are you sure you want to delete " + targetName + "? (y/n)";
                            System.err.println();
                            System.out.println(AsciiTableBuilder.buildSingleRow(message));

                            // 2. Prompt user choice
                            if (InputHandler.promptYesOrNo()) {
                                pharmacy.deleteMedicine(targetName);
                                MessageLog.logSuccess(targetName + " has been successfully deleted.");
                            }

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
            UIManager.displayBanner("admin.txt");
            // Print table and any error message
            UIManager.displayAdminMenu();
            MessageLog.displayAll();

            // Valid choices
            int choice = InputHandler.readValidChoice(AdminOperation.getValues());

            switch (choice) {
                case AdminOperation.REGISTER_CUSTOMER -> {
                    UIManager.clearScreen();
                    admin.addCustomerAccount();
                }
                case AdminOperation.SHOW_CUSTOMER_LIST -> {
                    List<Customer> customers = admin.getCustomers();

                    do {
                        UIManager.clearScreen();
                        UIManager.displayTitle("+ List Of Registered Customer Accounts +");
                        displayCustomerTable(customers);
                        MessageLog.displayNext();

                        System.out.println(TextColor.apply("\nSearch Instructions: ", Color.WHITE));
                        System.out.println(TextColor.apply("- Search customer by name", Color.LIGHT_YELLOW));
                        System.out.println(TextColor.apply("- Enter 'q' to exit.", Color.LIGHT_RED));
                        String targetName = InputHandler.readInput("\nEnter >> ");
                        if (targetName.equalsIgnoreCase("q")) {
                            break;
                        }
                        List<Customer> found = admin.searchCustomer(targetName);

                        if (found == null) {
                            MessageLog.logError("No results found.");
                            customers = admin.getCustomers();
                        } else {
                            MessageLog.logSuccess("Returned " + found.size() + " results.");
                            customers = found;
                        }
                    } while (true);
                }
                // Customer update or delete
                case AdminOperation.UPDATE_CUSTOMER_CREDENTIALS, AdminOperation.DELETE_CUSTOMER -> {
                    List<Customer> customers = admin.getCustomers();

                    do {
                        UIManager.clearScreen();
                        // Display respective operation title
                        if (choice == AdminOperation.UPDATE_CUSTOMER_CREDENTIALS) {
                            UIManager.displayTitle("+ Update Customer Credentials +");
                        } else {
                            UIManager.displayTitle("+ Remove A Customer Account +");
                        }
                        displayCustomerTable(customers);
                        MessageLog.displayNext();

                        System.out.println(TextColor.apply("\nSelect Instructions: ", Color.WHITE));
                        System.out.println(TextColor.apply("- Select a customer by entering its position number.", Color.LIGHT_YELLOW));
                        System.out.println(TextColor.apply("- Select customer by name", Color.LIGHT_YELLOW));
                        System.out.println(TextColor.apply("- Enter 'q' to exit.", Color.LIGHT_RED));

                        String input = InputHandler.readInput("\nEnter input >> ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) {
                            break;
                        }

                        // do not allow double for position
                        String doublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)";
                        if (input.matches(doublePattern)) {
                            MessageLog.logError("Enter a valid position");
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
                                MessageLog.logError("No results found.");
                                customers = admin.getCustomers(); // reset the table
                            } else {
                                customers = found;
                            }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            MessageLog.logError("Invalid Position.");
                            continue;
                        }

                        if (choice == AdminOperation.UPDATE_CUSTOMER_CREDENTIALS) {
                            admin.updateCustomerDetails(targetName);
                        } else {
                            String message = "Are you sure you want to delete " + targetName + "? (y/n)";
                            System.err.println();
                            System.out.println(AsciiTableBuilder.buildSingleRow(message));
                            if (InputHandler.promptYesOrNo()) {
                                admin.deleteCustomer(targetName);
                            }
                            customers = admin.getCustomers(); // update list
                        }
                    } while (true);
                }
                case AdminOperation.UPDATE_PHARMACY_CREDENTIALS -> {
                    UIManager.clearScreen();
                    UIManager.displayTitle("+ Update Pharmacy Credentials +");
                    admin.updatePharmacyDetails();
                }
                case AdminOperation.LOGOUT -> {
                    UIManager.loading("Logging out");
                    continueMenuLoop = false;
                }
            }
        } while (continueMenuLoop);
    }

    private static void routeToAccountMenu(Account account) {
        // Call respective Account menu
        if (account instanceof Customer) {
            runCustomerMenu((Customer) account);
        } else if (account instanceof Pharmacy) {
            runPharmacyMenu((Pharmacy) account);
        } else if (account instanceof Admin) {
            runAdminMenu((Admin) account);
        }
    }

    private static boolean retryLogin() {
        MessageLog.displayAll();
        MessageLog.logError("Login failed.");
        System.out.println(TextColor.apply("\n- Enter anything to continue", Color.LIGHT_YELLOW));
        System.out.println(TextColor.apply("- Enter 'q' to exit.", Color.LIGHT_RED));
        String input = InputHandler.readInput("\nEnter Choice >> ", true);
        if (input.equals("q")) {
            return false;
        }
        return true;
    }

    private static void displayCustomerTable(List<Customer> customers) {
        System.out.println(AsciiTableBuilder.buildCustomerTable(customers));
    }

    public static void displayMedicineTable(List<Medicine> medicines) {
        System.out.println(AsciiTableBuilder.buildMedicineTable(medicines));
    }

    public static void displayMenu(String header, String[][] menuItems, String exitLabel) {
        List<String> row = new ArrayList<>();
        for (String[] item : menuItems) {
            row.add(">> [" + item[0] + "] " + item[1]);
        }
        String footer = " << [0] " + exitLabel;

        String table = new AsciiTableBuilder()
                .setHeader(header)
                .setRows(row.toArray(new String[0]))
                .setFooter(footer)
                .buildGenericMenuTable();

        for (String[] item : menuItems) {
            String id = "[" + item[0] + "]";
            String label = item[1];

            table = table.replace(id, TextColor.apply(id, Color.LIGHT_GREEN));
            table = table.replace(label, TextColor.apply(label, Color.WHITE));

        }

        table = table.replace(header, TextColor.apply(header, Color.WHITE));
        table = table.replace(footer, TextColor.apply(footer, Color.LIGHT_RED));

        table = table.replace(">>", TextColor.apply(">>", Color.WHITE));

        System.out.println(table);
    }

    public static void displayPopUp(String header, String prompt) {

        String table = new AsciiTableBuilder()
                .setHeader(header)
                .setRow(">> " + prompt)
                .buildGenericPopUpMenu();

        table = table.replace(prompt, TextColor.apply(prompt, Color.WHITE));

        table = table.replace(header, TextColor.apply(header, Color.LIGHT_GREEN));

        table = table.replace(">>", TextColor.apply(">>", Color.WHITE));

        System.out.println(table);
    }

    public static void displayChooseAccountMenu() {

        String[][] items = {
            {"1", "Customer"},
            {"2", "Pharmacy"},
            {"3", "Admin"}
        };

        UIManager.displayMenu("+ Select Account Type +", items, "Exit");
    }

    public static void displayTitle(String title) {

        String table = TextColor.apply(AsciiTableBuilder.buildSingleRow(title), Color.WHITE);

        table = table.replace(title, TextColor.apply(title, Color.LIGHT_GREEN));

        System.out.println(table);
    }

    private static void displayCustomerMenu() {

        String[][] items = {
            {"1", "Buy Medicine"},
            {"2", "View Account Details"},
            {"3", "Deposit Funds"}
        };

        UIManager.displayMenu("+ Customer Menu +", items, "Logout");
    }

    private static void displayPharmacyMenu() {

        String[][] items = {
            {"1", "Add Medicine"},
            {"2", "Show List of Medicines"},
            {"3", "Update Medicine Amount"},
            {"4", "Update Medicine Price"},
            {"5", "Delete Medicine"}
        };

        UIManager.displayMenu("+ Pharmacy Menu +", items, "Logout");
    }

    private static void displayAdminMenu() {

        String[][] items = {
            {"1", "Register A Customer"},
            {"2", "Show List Of Customers"},
            {"3", "Edit Customer Credentials"},
            {"4", "Edit Pharmacy Credentials"},
            {"5", "Delete Customer"}
        };

        UIManager.displayMenu("+ Admin Menu +", items, "Logout");
    }

    public static void displayBanner(String filename) {
        try {
            String logo = Files.readString(Path.of("assets", filename));
            System.out.println(TextColor.apply(logo, Color.LIGHT_GREEN));
        } catch (IOException e) {
            MessageLog.logError("Failed to load Banner.");
            MessageLog.displayNext();
        }
    }

    public static void displayCustomerAccountDetails(Customer customer) {
        UIManager.displayTitle("+ Account Details +");
        System.out.println(AsciiTableBuilder.buildCustomerAccountDetails(customer));
        UIManager.displayTitle("+ Medicine Cabinet +");
    }

    public static void delay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void loading(String label) {
        System.out.print(TextColor.apply("\n" + label, Color.BRIGHT_BLACK));

        for (int i = 0; i < 3; i++) {
            UIManager.delay(500);
            System.out.print(TextColor.apply(" .", Color.BRIGHT_BLACK));
        }

        System.out.println();
    }
}
