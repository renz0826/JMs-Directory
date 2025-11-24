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
                String username = InputHandler.readNonEmptyLine(promptUsername);
                String password = InputHandler.readNonEmptyLine(promptPassword);

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
                    String input = InputHandler.readNonEmptyLine("\nEnter Choice >> ");
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
                        //pharmacy.addMedicine();
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
                    UIManager.clear();
                    displayData(pharmacy.getMedicines(), true);
                    //pharmacy.searchMedicine();
                }
                case 3 -> {

                }
                //pharmacy.updateMedicineAmount();
                case 4 -> {

                }
                //pharmacy.updateMedicinePrice();
                case 5 -> {

                }
                //pharmacy.deleteMedicine();
            }

        }
    }

    public static void displayAdminMenu(Admin admin) {
        while (true) {

            // Clear screen at start of every loop
            UIManager.clear();

            // Reset table (Critical to prevent "ghost" rows)
            asciiTable = new AsciiTable();

            // --- Header Construction ---
            asciiTable.addRule();
            asciiTable.addRow("+ Admin Menu +");
            asciiTable.addRule();
            asciiTable.setTextAlignment(TextAlignment.CENTER);

            String[] rows = {
                "1. Add Customer Account",
                "2. Add Pharmacy Account",
                "3. View Customer Account",
                "4. View Pharmacy Account",
                "5. Edit Customer Account",
                "6. Edit Pharmacy Account",
                "7. Delete Customer Account",
                "8. Delete Pharmacy Account"};

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
                    UIManager.clear();
                }
                case 3 -> {

                }
                case 4 -> {

                }
                case 5 -> {
                }
            }

        }
    }

    public static void displayData(Account account) {
    }

    public static void displayData(List<Medicine> medicines, boolean indexed) {
        asciiTable = new AsciiTable();

        // 2. [WIDTH] Use LongestLine renderer
        asciiTable.getRenderer().setCWC(new CWC_LongestLine());

        // Header
        asciiTable.addRule();
        AT_Row headerRow; // Variable to capture the row

        if (!indexed) {
            // [TRICK] The spaces ensure the columns are wide enough
            headerRow = asciiTable.addRow(
                    "          Name          ",
                    "   Price   ",
                    "  Amount  ",
                    "     Brand     ",
                    "   Expires At   ",
                    "                    Purpose                    "
            );
        } else {
            headerRow = asciiTable.addRow(
                    " Pos # ",
                    "          Name          ",
                    "   Price   ",
                    "  Amount  ",
                    "     Brand     ",
                    "   Expires At   ",
                    "                    Purpose                    "
            );
        }

        // [CRITICAL FIX] Center ONLY the header row
        headerRow.setTextAlignment(TextAlignment.CENTER);

        asciiTable.addRule();

        // Insert Data (Will use the Global LEFT alignment)
        if (!indexed) {
            for (Medicine medicine : medicines) {
                asciiTable.addRow(
                        medicine.getName(),
                        medicine.getPrice(),
                        medicine.getAmount(),
                        medicine.getBrand(),
                        medicine.getExpirationDate(),
                        medicine.getPurpose()
                );
                asciiTable.addRule();
            }
        } else {
            int indexCounter = 0;
            for (Medicine medicine : medicines) {
                asciiTable.addRow(
                        indexCounter,
                        medicine.getName(),
                        medicine.getPrice(),
                        medicine.getAmount(),
                        medicine.getBrand(),
                        medicine.getExpirationDate(),
                        medicine.getPurpose()
                );
                asciiTable.addRule();
                indexCounter++;
            }
        }

        asciiTable.setPadding(2);

        // Render and print
        String rend = asciiTable.render();
        System.out.println(rend);
    }

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
