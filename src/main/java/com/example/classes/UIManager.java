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

    private static AsciiTable asciiTable;

    public static void clear() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Login choice method
    public static void displayLoginChoice() {

        // Login loop
        while (true) {
            UIManager.clear();
            // Header
            asciiTable = new AsciiTable();
            asciiTable.addRule();
            asciiTable.addRow("+ Select Account Type +");
            asciiTable.addRule();
            asciiTable.setTextAlignment(TextAlignment.CENTER);

            String[] rows = {
                "1. Customer",
                "2. Pharmacy",
                "3. Admin",};

            for (String label : rows) {
                AT_Cell cell = asciiTable.addRow(label).getCells().get(0);
                cell.getContext().setPadding(1).setPaddingLeft(7);
                cell.getContext().setTextAlignment(TextAlignment.LEFT);
            }

            asciiTable.addRule();
            AT_Row row = asciiTable.addRow("0. Exit");
            row.setPadding(1).setPaddingLeft(7);
            asciiTable.addRule();

            // Render and print
            String rend = asciiTable.render();
            System.out.println(rend);

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

                asciiTable = new AsciiTable();

                asciiTable.addRule();
                asciiTable.addRow("+ Login +");
                asciiTable.addRule();
                asciiTable.setTextAlignment(TextAlignment.CENTER);
                String rend2 = asciiTable.render();
                System.out.println(rend2);

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
                    System.out.println("\n[ERROR] Login failed. \nEnter anything to try again or enter 'q' to exit.");
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
        UIManager.clear();

        asciiTable = new AsciiTable();
        // Header
        asciiTable.addRule();
        asciiTable.addRow("+ Customer Menu +");
        asciiTable.addRule();
        asciiTable.setTextAlignment(TextAlignment.CENTER);

        String[] rows = {
            "1. Buy Medicine",
            "2. View Account Details",
            "3. Deposit Funds",};

        for (String label : rows) {
            AT_Cell cell = asciiTable.addRow(label).getCells().get(0);
            cell.getContext().setPadding(1).setPaddingLeft(7);
            cell.getContext().setTextAlignment(TextAlignment.LEFT);
        }

        asciiTable.addRule();
        AT_Row row = asciiTable.addRow("0. Logout");
        row.setPadding(1).setPaddingLeft(7);
        asciiTable.addRule();

        // Render and print
        String rend = asciiTable.render();
        System.out.println(rend);

        // Valid choices
        int choice = InputHandler.getValidChoice(Set.of(4, 3, 2, 1, 0));

        switch (choice) {
            case 1 -> {
                boolean stayingInAddMenu = true;
                while (stayingInAddMenu) {

                    // 1. Perform the action FIRST
                    customer.buyMedicine();
                    // 2. Then ask what to do next
                    UIManager.clear();
                    asciiTable = new AsciiTable();
                    asciiTable.addRule();
                    asciiTable.addRow("What action would you like to perform next?");
                    asciiTable.addRule();
                    asciiTable.setTextAlignment(TextAlignment.CENTER);

                    String[] options = {"1. Buy Again", "2. Back to Menu"};

                    for (String label : options) {
                        AT_Cell cell = asciiTable.addRow(label).getCells().get(0);
                        cell.getContext().setPadding(1).setPaddingLeft(7);
                        cell.getContext().setTextAlignment(TextAlignment.LEFT);
                    }
                    asciiTable.addRule();

                    System.out.println(asciiTable.render());

                    int subChoice = InputHandler.getValidChoice(Set.of(2, 1));

                    if (subChoice == 2) {
                        stayingInAddMenu = false;
                        UIManager.displayCustomerMenu(customer);
                    }
                }
            }
            case 2 -> {
                customer.viewAccountDetails();
                UIManager.displayCustomerMenu(customer);
            }
            case 3 -> {
                boolean stayingInAddMenu = true;
                while (stayingInAddMenu) {

                    // 1. Perform the action FIRST
                    customer.depositFunds();
                    // 2. Then ask what to do next
                    UIManager.clear();
                    asciiTable = new AsciiTable();
                    asciiTable.addRule();
                    asciiTable.addRow("What action would you like to perform next?");
                    asciiTable.addRule();
                    asciiTable.setTextAlignment(TextAlignment.CENTER);

                    String[] options = {"1. Deposit Again", "2. Back to Menu"};

                    for (String label : options) {
                        AT_Cell cell = asciiTable.addRow(label).getCells().get(0);
                        cell.getContext().setPadding(1).setPaddingLeft(7);
                        cell.getContext().setTextAlignment(TextAlignment.LEFT);
                    }
                    asciiTable.addRule();

                    System.out.println(asciiTable.render());

                    int subChoice = InputHandler.getValidChoice(Set.of(2, 1));

                    if (subChoice == 2) {
                        stayingInAddMenu = false;
                        UIManager.displayCustomerMenu(customer);
                    }
                }
            }
            case 0 ->
                System.out.println("\nExiting...");
        }
    }

    public static void displayPharmacyMenu(Pharmacy pharmacy) {
        while (true) {

            // Clear screen at start of every loop
            UIManager.clear();

            // Reset table (Critical to prevent "ghost" rows)
            asciiTable = new AsciiTable();

            // --- Header Construction ---
            asciiTable.addRule();
            asciiTable.addRow("+ Pharmacy Menu +");
            asciiTable.addRule();
            asciiTable.setTextAlignment(TextAlignment.CENTER);

            String[] rows = {
                "1. Add Medicine",
                "2. Show List of Medicines",
                "3. Update Medicine Amount",
                "4. Update Medicine Price",
                "5. Delete Medicine",};

            for (String label : rows) {
                AT_Cell cell = asciiTable.addRow(label).getCells().get(0);
                cell.getContext().setPadding(1).setPaddingLeft(7);
                cell.getContext().setTextAlignment(TextAlignment.LEFT);
            }

            asciiTable.addRule();
            AT_Row row = asciiTable.addRow("0. Logout");
            row.setPadding(1).setPaddingLeft(7);
            asciiTable.addRule();

            String rend = asciiTable.render();
            System.out.println(rend);

            // Initialize choice
            int mainChoice = InputHandler.getValidChoice(Set.of(5, 4, 3, 2, 1, 0));

            if (mainChoice == 0) {
                System.out.println("\nLogging out...");
                UIManager.clear();
                break;
            }

            // Handle Main Menu Actions
            switch (mainChoice) {
                case 1 -> {
                    // --- SUB MENU LOOP ---
                    boolean stayingInAddMenu = true;
                    while (stayingInAddMenu) {

                        // 1. Perform the action FIRST
                        pharmacy.addMedicine();
                        // 2. Then ask what to do next
                        UIManager.clear();
                        asciiTable = new AsciiTable();
                        asciiTable.addRule();
                        asciiTable.addRow("What action would you like to perform next?");
                        asciiTable.addRule();
                        asciiTable.setTextAlignment(TextAlignment.CENTER);

                        String[] options = {"1. Add another medicine", "2. Back to menu"};

                        for (String label : options) {
                            AT_Cell cell = asciiTable.addRow(label).getCells().get(0);
                            cell.getContext().setPadding(1).setPaddingLeft(7);
                            cell.getContext().setTextAlignment(TextAlignment.LEFT);
                        }
                        asciiTable.addRule();

                        System.out.println(asciiTable.render());

                        int subChoice = InputHandler.getValidChoice(Set.of(2, 1));

                        if (subChoice == 2) {
                            stayingInAddMenu = false;
                        }
                    }
                }
                case 2 -> {
                    // display all medicines once
                    List<Medicine> medicines = pharmacy.getMedicines();
                    displayMedicineTable(medicines);

                    do {
                        System.out.println("Search medicine by name or enter 'q' to exit.");
                        String targetName = InputHandler.readInput("Enter: ");
                        if (targetName.equalsIgnoreCase("q")) break;
                        medicines = pharmacy.searchMedicine(targetName);

                        if (medicines == null) {
                            System.out.println("No results found");
                        } else {
                            displayMedicineTable(medicines);
                        }
                    } while (true);
                    // UIManager.clear();
                }
                case 3, 4, 5 -> {
                    do {
                        List<Medicine> medicines = pharmacy.getMedicines();
                        displayMedicineTable(medicines);

                        System.out.println("Instructions: ");
                        System.out.println("- Select medicine by entering its position number.");
                        System.out.println("- Search medicine by name or enter 'q' to exit.");

                        String input = InputHandler.readInput("Enter input: ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) {
                            break;
                        }

                        // do not allow double for position
                        String doublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)";
                        if (input.matches(doublePattern)) {
                            System.out.println("[ERROR]: Enter a valid position");
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
                                System.out.println("No results found");
                            } else {
                                medicines = result;
                            }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("[ERROR]: Medicine not found at position " + pos);
                            continue;
                        }

                        if (mainChoice == 3) {
                            int amount = InputHandler.readInt("Enter amount: ", true);
                            pharmacy.updateMedicineAmount(targetName, amount);
                        } else if (mainChoice == 4) {
                            double amount = InputHandler.readDouble("Enter new price: ");
                            pharmacy.updateMedicinePrice(targetName, amount);
                        } else {
                            System.out.println("Are you sure you want to delete " + targetName + "?");
                            String confirmation = InputHandler.readInput("(y/n): ");
                            if (confirmation.equalsIgnoreCase("y")) { pharmacy.deleteMedicine(targetName); }
                            medicines = pharmacy.getMedicines(); // update list
                        }
                    } while (true);
                }
            }
        }
    }

    public static void displayAdminMenu(Admin admin) {
        String menu = """
                ==============================================
                |               + Admin Menu +               |
                ==============================================
                |                                            |
                |   1. Register a customer                   |
                |   2. Show list of customers                |
                |   3. Edit customer credentials             |
                |   4. Edit pharmacy credentials             |
                |   5. Delete customer                       |
                |                                            |
                |   0. Logout                                |
                |                                            |
                ==============================================
            """;
            // Clear screen at start of every loop
            UIManager.clear();

        boolean running = true;
        do {
            System.out.print(menu);

            // Valid choices
            int choice = InputHandler.getValidChoice(Set.of(8, 7, 6, 5, 4, 3, 2, 1, 0));
            
            switch (choice) {
                case 1 -> admin.addCustomerAccount();
                case 2 -> {
                    List<Customer> customers = admin.getCustomers();
                    
                    // display once
                    displayCustomerTable(customers);
                    do {
                        System.out.println("Search customer by name or enter 'q' to exit.");
                        String targetName = InputHandler.readInput("Enter: ");
                        if (targetName.equalsIgnoreCase("q")) break;
                        customers = admin.searchCustomer(targetName);

                        if (customers == null) {
                            System.out.println("No results found");
                        } else {
                            displayCustomerTable(customers);
                        }
                    } while (true);
                }
                // Customer update or delete
                case 3, 5 -> {
                    List<Customer> customers = admin.getCustomers();

                    do {
                        displayCustomerTable(customers);

                        System.out.println("Instructions: ");
                        System.out.println("- Select a customer by entering its position number.");
                        System.out.println("- Search customer by name or enter 'q' to exit.");

                        String input = InputHandler.readInput("Enter input: ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) break;

                        // do not allow double for position
                        String doublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)"; 
                        if (input.matches(doublePattern)) {
                            System.out.println("[ERROR]: Enter a valid position");
                            continue;
                        }
                        
                        // if number then select customer, else search
                        int pos = 0;
                        String targetName;
                        try {
                            pos = Integer.parseInt(input);
                            targetName = customers.get(pos).getName();
                        } catch (NumberFormatException e) {
                            List<Customer> result = admin.searchCustomer(input);
                            if (result == null) { System.out.println("No results found"); }
                            else { customers = result; }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("[ERROR]: Customer not found at position " + pos);
                            continue;
                        }

                        if (choice == 3) { 
                            admin.updateCustomerDetails(targetName); 
                        } else {
                            System.out.println("Are you sure you want to delete " + targetName + "?");
                            String confirmation = InputHandler.readInput("(y/n): ");
                            if (confirmation.equalsIgnoreCase("y")) { admin.deleteCustomer(targetName); }
                            customers = admin.getCustomers(); // update list
                        }
                    } while (true);
                }
                case 4 -> {
                    admin.updatePharamacyDetails();
                }
                case 0 -> {
                    System.out.println("\nExiting...");
                    running = false;
                }
            }
        } while (running);
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

    public static void buyMedicineUI() {
        asciiTable = new AsciiTable();

        asciiTable.addRule();
        asciiTable.addRow("+ Buy Medicine +");
        asciiTable.setTextAlignment(TextAlignment.CENTER);
        asciiTable.addRule();
        AT_Cell cell = asciiTable.addRow("> Which medicine would you like to buy?").getCells().get(0);
        cell.getContext().setPadding(1);
        cell.getContext().setTextAlignment(TextAlignment.CENTER);
        asciiTable.addRule();

        asciiTable.getContext().setWidth(166);

        String rend = asciiTable.render();
        System.out.println(rend);
    }
}
