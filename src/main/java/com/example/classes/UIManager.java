package com.example.classes;

import java.util.List;
import java.util.Set;

import de.vandermeer.asciitable.AT_Cell;
import de.vandermeer.asciitable.AT_Row;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

// UI class for design
class UIManager {
    private enum AccountType {
        CUSTOMER,
        PHARMACY,
        ADMIN
    }
    private static AsciiTable asciiTable;

    public static void clear() {
        // try {
        //     new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        // } catch (Exception e) {
        //     System.out.println(e);
        // }
    }

    // Login choice method
    public static void displayLoginChoice() {
        String table = new AsciiTableBuilder()
                .setHeader("+ Select Account Type +")
                .setRows("1. Customer", "2. Pharmacy", "3. Admin")
                .setFooter("0. Exit")
                .buildGenericMenuTable();

        // Login loop
        while (true) {
            UIManager.clear();
            // Render Table
            System.out.println(table);

            Account authenticated = null;
            AccountType accountType;

            // ask user which account to login
            accountType = switch (InputHandler.getValidChoice(Set.of(3, 2, 1, 0))) {
                case 1 ->
                    AccountType.CUSTOMER;
                case 2 ->
                    AccountType.PHARMACY;
                case 3 ->
                    AccountType.ADMIN;
                case 0 -> {
                    System.out.println("\nExiting...");
                    yield null;
                }
                default ->
                    null;
            };

            if (accountType == null) {
                break;
            }

            // prompt user credentials
            while (authenticated == null) {

                UIManager.clear();
                System.out.println(AsciiTableBuilder.buildSingleRow("+ Login +"));
                String promptUsername = "\nEnter Username\nUsername >> ";
                String promptPassword = "\nEnter Password\nPassword >> ";
                String username = InputHandler.readInput(promptUsername);
                String password = InputHandler.readInput(promptPassword);

                // verify credentials
                authenticated = switch (accountType) {
                    case CUSTOMER ->
                        AuthService.logInCustomer(username, password);
                    case PHARMACY ->
                        AuthService.logInPharmacy(username, password);
                    case ADMIN ->
                        AuthService.logInAdmin(username, password);
                };

                if (authenticated == null) {
                    ErrorMessage.display("\n[ERROR] Login failed.");
                    System.out.println("Enter anything to try again or enter 'q' to exit.");
                    String input = InputHandler.readInput("\nEnter Choice >> ", true);
                    if (input.equals("q")) {
                        break;
                    }
                }

            }

            // Call respective Account menu
            if (authenticated instanceof Customer) {
                displayCustomerMenu((Customer) authenticated);
            } else if (authenticated instanceof Pharmacy) {
                displayPharmacyMenu((Pharmacy) authenticated);
            } else if (authenticated instanceof Admin) {
                displayAdminMenu((Admin) authenticated);
            }

        }

    }

    public static void displayCustomerMenu(Customer customer) {
        String table = new AsciiTableBuilder()
                .setHeader("+ Customer Menu +")
                .setRows("1. Buy Medicine", "2. View Account Details", "3. Deposit Funds")
                .setFooter("0. Logout")
                .buildGenericMenuTable();
        boolean continueMenuLoop = true;

        do {
            // Clear screen when entering
            UIManager.clear();
            // Render table
            System.out.println(table);
            // Display any error messages
            ErrorMessage.displayNext();

            // Get valid choices from user
            int choice = InputHandler.getValidChoice(Set.of(4, 3, 2, 1, 0));

            switch (choice) {
                case 1 -> customer.buyMedicine();
                case 2 -> customer.viewAccountDetails();
                case 3 -> {
                    boolean stayingInAddMenu = true;
                    do {
                        // 1. Perform the action FIRST
                        customer.depositFunds();
                        // 2. Then ask what to do next
                        System.out.println(AsciiTableBuilder.buildSingleRow("Would you like to deposit again? (y/n)"));
                        if (InputHandler.promptYesOrNo()) { continue; }
                        else { stayingInAddMenu = false; }
                    } while (stayingInAddMenu);
                }
                case 0 -> {
                    System.out.println("\nExiting...");
                    continueMenuLoop = false;
                }
            }
        } while (continueMenuLoop);
    }

    public static void displayPharmacyMenu(Pharmacy pharmacy) {
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
        boolean continueMenuLoop = true;

        do {
            // Clear screen at start of every loop
            UIManager.clear();
            // Render table menu
            System.out.println(table);
            // Display any errors
            ErrorMessage.displayAll();

            // Initialize choice
            int mainChoice = InputHandler.getValidChoice(Set.of(5, 4, 3, 2, 1, 0));

            // Handle Main Menu Actions
            switch (mainChoice) {
                case 0 -> {
                    System.out.println("\nLogging out...");
                    continueMenuLoop = false;
                }
                case 1 -> {
                    // --- SUB MENU LOOP ---
                    do {
                        pharmacy.addMedicine(); // 1. Perform the action FIRST
                        UIManager.clear();
                        String message = "Would you like to add another medicine? (y/n)";
                        System.out.println(AsciiTableBuilder.buildSingleRow(message));

                        if (InputHandler.promptYesOrNo()) continue; // 2. Then ask what to do next
                        else break;
                    } while (true);
                }
                case 2 -> {
                    // display all medicines once
                    List<Medicine> medicines = pharmacy.getMedicines();
                    
                    do {
                        displayMedicineTable(medicines);
                        ErrorMessage.displayNext();

                        System.out.println("Search medicine by name or enter 'q' to exit.");
                        String targetName = InputHandler.readInput("Enter >> ");
                        if (targetName.equalsIgnoreCase("q")) { break; }
                        List<Medicine> found = pharmacy.searchMedicine(targetName);

                        if (found == null) {
                            // Reset to original list if target medicines are not found
                            ErrorMessage.queueMessage("\n[SUCCESS]: No results found.");
                            medicines = pharmacy.getMedicines();
                        } else {
                            ErrorMessage.queueMessage("\n[SUCCESS]: Returned " + found.size() + " results.");
                            medicines = found;
                        }
                    } while (true);
                    UIManager.clear();
                }
                // FOR UPDATE AND DELETE
                case 3, 4, 5 -> {
                    do {
                        List<Medicine> medicines = pharmacy.getMedicines();
                        displayMedicineTable(medicines);
                        ErrorMessage.displayAll();

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
                            ErrorMessage.queueMessage("\n[ERROR]: Enter a valid position");
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
                                ErrorMessage.queueMessage("\n[SUCCESS]: No results found");
                            } else {
                                medicines = result;
                            }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            ErrorMessage.queueMessage("\n[ERROR]: Invalid Position.");
                            continue;
                        }

                        if (mainChoice == 3) {
                            int amount = InputHandler.readInt("Enter amount >> ", true);
                            pharmacy.updateMedicineAmount(targetName, amount);
                        } else if (mainChoice == 4) {
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

    public static void displayAdminMenu(Admin admin) {
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
        boolean continueMenuLoop = true;

        do {
            UIManager.clear();
            // Print table and any error message
            System.out.println(table);
            ErrorMessage.displayAll();

            // Valid choices
            int choice = InputHandler.getValidChoice(Set.of(8, 7, 6, 5, 4, 3, 2, 1, 0));
            
            switch (choice) {
                case 1 -> admin.addCustomerAccount();
                case 2 -> {
                    List<Customer> customers = admin.getCustomers();
                    
                    do {
                        // display once
                        displayCustomerTable(customers);
                        ErrorMessage.displayNext();

                        System.out.println("Search customer by name or enter 'q' to exit.");
                        String targetName = InputHandler.readInput("Enter >> ");
                        if (targetName.equalsIgnoreCase("q")) break;
                        List<Customer> found = admin.searchCustomer(targetName);

                        if (found == null) {
                            ErrorMessage.queueMessage("\n[SUCCESS]: No results found.");
                            customers = admin.getCustomers();
                        } else {
                            ErrorMessage.queueMessage("\n[SUCCESS]: Returned " + found.size() + " results.");
                            customers = found;
                        }
                    } while (true);
                }
                // Customer update or delete
                case 3, 5 -> {
                    List<Customer> customers = admin.getCustomers();

                    do {
                        displayCustomerTable(customers);
                        ErrorMessage.displayNext();

                        System.out.println("Instructions: ");
                        System.out.println("- Select a customer by entering its position number.");
                        System.out.println("- Search customer by name or enter 'q' to exit.");

                        String input = InputHandler.readInput("Enter input >> ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) break;

                        // do not allow double for position
                        String doublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)"; 
                        if (input.matches(doublePattern)) {
                            ErrorMessage.queueMessage("\n[ERROR]: Enter a valid position");
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
                                ErrorMessage.queueMessage("\n[SUCCESS]: No results found."); 
                                customers = admin.getCustomers(); // reset the table
                            }
                            else { customers = found; }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            ErrorMessage.queueMessage("\n[ERROR]: Invalid Position.");
                            continue;
                        }

                        if (choice == 3) { 
                            admin.updateCustomerDetails(targetName); 
                        } else {
                            String message = "Are you sure you want to delete " + targetName + "? (y/n)";
                            System.out.println(AsciiTableBuilder.buildSingleRow(message));
                            if (InputHandler.promptYesOrNo()) { admin.deleteCustomer(targetName); }
                            customers = admin.getCustomers(); // update list
                        }
                    } while (true);
                }
                case 4 -> admin.updatePharmacyDetails();
                case 0 -> {
                    System.out.println("\nExiting...");
                    continueMenuLoop = false;
                }
            }
        } while (continueMenuLoop);
    }

    private static void displayCustomerTable(List<Customer> accounts) {
        asciiTable = new AsciiTable();

        // 2. [WIDTH] Use LongestLine renderer
        asciiTable.getRenderer().setCWC(new CWC_LongestLine());

        // Header
        asciiTable.addRule();
        asciiTable.addRow("Position #", "Name", "Username", "Password", "Funds");
        asciiTable.addRule();

        int indexCounter = 0;
        for (Customer customer : accounts) {
            asciiTable.addRow(
                indexCounter, customer.getName(), customer.getUsername(), 
                customer.getPassword(), customer.getFunds()
            );
            asciiTable.addRule();
            indexCounter++;
        }

        asciiTable.setPadding(2);

        // Render and print
        String rend = asciiTable.render();
        System.out.println(rend);
    }

    public static void displayMedicineTable(List<Medicine> medicines) {
        asciiTable = new AsciiTable();

        // Header
        asciiTable.addRule();
        asciiTable.addRow("Position #", "Name", "Price", "Amount", "Brand", "Expires At", "Purpose");
        asciiTable.addRule();

        int indexCounter = 0;
        for (Medicine medicine : medicines) {
            asciiTable.addRow(
                indexCounter, medicine.getName(), medicine.getPrice(), medicine.getAmount(),
                medicine.getBrand(), medicine.getExpirationDate(), medicine.getPurpose()
            );
            asciiTable.addRule();
            indexCounter++;
        }

        // Render and print table to console
        String rend = asciiTable.render();
        System.out.println(rend);
    };
}
