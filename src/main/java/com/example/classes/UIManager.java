package com.example.classes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.vandermeer.asciitable.AsciiTable;

// UI class for design
class UIManager {
    private static AsciiTable asciiTable;

    // Login choice method
    public static void displayLoginChoice(){
        String menu = """
                ===============================================
                |           + Select Account Type +           |
                ===============================================
                |                                             |
                |   1. Customer                               |
                |   2. Pharmacy                               |
                |   3. Admin                                  |
                |                                             |
                |   0. Exit                                   |
                |                                             |
                ===============================================

                Please Select an Account Type.
                """;
        Account authenticated = null;
        AccountType accountType;

        // Login loop
        while (true) {
            // display menu
            System.out.print(menu);
            
            // ask user which account to login
            accountType = switch(InputHandler.getValidChoice(Set.of(3, 2, 1, 0))) {
                case 1 -> AccountType.CUSTOMER;
                case 2 -> AccountType.PHARMACY;
                case 3 -> AccountType.ADMIN;
                case 0 -> {
                    System.out.println("\nExiting...");
                    yield null;
                }
                default -> null;
            };

            if (accountType == null) break;

            // prompt user credentials
            while (authenticated == null) {
                System.out.println("====== Login System ======");
                String username = InputHandler.readNonEmptyLine("Enter username: ");
                String password = InputHandler.readNonEmptyLine("Enter password: ");
                
                // verify credentials
                authenticated = switch (accountType) {
                    case CUSTOMER -> AuthService.logInCustomer(username, password);
                    case PHARMACY -> AuthService.logInPharmacy(username, password);
                    case ADMIN -> AuthService.logInAdmin(username, password);
                };

                if (authenticated == null) {
                    System.out.println("Login failed. Enter anything to try again.");
                    String input = InputHandler.readNonEmptyLine("Enter 'q' to exit: ");
                    if (input.equals("q")) break;
                }
            }

            // Call respective Account menu
            if (authenticated instanceof Customer) displayCustomerMenu((Customer) authenticated);
            else if (authenticated instanceof Pharmacy) displayPharmacyMenu((Pharmacy) authenticated);
            else if (authenticated instanceof Admin) displayAdminMenu((Admin) authenticated);
            authenticated = null;
        }
    }

    public static void displayCustomerMenu(Customer customer){
        String menu = """
                =============================================
                |             + Customer Menu +             |
                =============================================
                |                                           |
                |   1. Buy Medicine                         |
                |   2. View Account Details                 |
                |   3. Deposit Funds                        |
                |   4. View Transactions                    |
                |                                           |
                |   0. Logout                               |
                |                                           |
                =============================================

                Please Choose an Option.
                """;

        System.out.print(menu);

        // Valid choices
        int choice = InputHandler.getValidChoice(Set.of(4, 3, 2, 1, 0));
        
        switch (choice){
            case 1:
                break;
            case 2:
                //s = new Customer("a", "b");
                break;
            case 3:
                //s = new Customer("a", "b");
                break;
            case 4:
                break;
            case 0:
                System.out.println("\nExiting...");
                break;
        }
    }

    public static void displayPharmacyMenu(Pharmacy pharmacy){
        // Menu display
        String menu = """
                =============================================
                |             + Pharmacy Menu +             |
                =============================================
                |                                           |
                |   1. Add Medicine                         |
                |   2. Show List of Medicines               |
                |   3. Update Medicine Amount               |
                |   4. Update Medicine Price                |
                |   5. Delete Medicine                      |
                |                                           |
                |   0. Logout                               |
                |                                           |
                =============================================

                Please Choose an Option.
                """;

        // Initialize necessary variables
        int choice;
        boolean running = true;

        // Get user choice
        do {
            System.out.print(menu);

            switch (choice = InputHandler.getValidChoice(Set.of(5, 4, 3, 2, 1, 0))){
                case 1 -> {
                    do {
                        pharmacy.addMedicine();
                        System.out.println("1. Add another medicine");
                        System.out.println("2. Back to menu");
                        System.out.print("Enter option: ");
                        choice = InputHandler.getValidChoice(Set.of(1, 2));
                    } while (choice != 2);
                }
                case 2 -> {
                    // display all medicines once
                    List<Medicine> medicines = pharmacy.getMedicines();
                    displayData(medicines);

                    do {
                        System.out.println("Search medicine by name or enter 'q' to exit.");
                        String targetName = InputHandler.readNonEmptyLine("Enter: ");
                        if (targetName.equalsIgnoreCase("q")) break;
                        medicines = pharmacy.searchMedicine(targetName);

                        if (medicines == null) {
                            System.out.println("No results found");
                        } else {
                            displayData(medicines);
                        }
                    } while (true);
                }
                case 3, 4 -> {
                    List<Medicine> medicines = pharmacy.getMedicines();
                    // for matching strings representing a double number
                    String realDoublePattern = "-?(\\d*\\.\\d+|\\d+\\.\\d*)"; 

                    do {
                        displayData(medicines, true);

                        System.out.println("Instructions: ");
                        System.out.println("- Select medicine by entering its position number.");
                        System.out.println("- Search medicine by name or enter 'q' to exit.");

                        String input = InputHandler.readNonEmptyLine("Enter input: ");

                        // exit if quit
                        if (input.equalsIgnoreCase("q")) break;

                        // do not allow double
                        if (input.matches(realDoublePattern)) {
                            System.out.println("[ERROR]: Enter a valid position");
                            continue;
                        }
                        
                        int pos = 0;
                        String targetName;

                        // if number then select medicine, else search
                        try {
                            pos = Integer.parseInt(input);
                            targetName = medicines.get(pos).getName();
                        } catch (NumberFormatException e) {
                            List<Medicine> result = pharmacy.searchMedicine(input);
                            if (result == null) { System.out.println("No results found"); }
                            else { medicines = result; }
                            continue;
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("[ERROR]: Medicine not found at position " + pos);
                            continue;
                        }

                        if (choice == 3) { 
                            int amount = InputHandler.readInt("Enter amount: ", true);
                            pharmacy.updateMedicineAmount(targetName, amount); 
                        }
                        else { 
                            double amount = InputHandler.readDouble("Enter new price: ");
                            pharmacy.updateMedicinePrice(targetName, amount); 
                        }

                    } while (true);
                }
                case 5 -> pharmacy.deleteMedicine();
                case 0 -> {
                    System.out.println("\nExiting...");
                    running = false;
                }
            };
        } while (running);
    }

    public static void displayAdminMenu(Admin admin){
        String menu = """
                ==============================================
                |               + Admin Menu +               |
                ==============================================
                |                                            |
                |   1. Add Customer                          |
                |   2. Add Pharmacy                          |
                |   3. View Customer                         |
                |   4. View Pharmacy                         |
                |   5. Edit Customer                         |
                |   6. Edit Pharmacy                         |
                |   7. Delete Customer                       |
                |   8. Delete Pharmacy                       |
                |                                            |
                |   0. Logout                                |
                |                                            |
                ==============================================

                Please Choose an Option.
                """;

        System.out.print(menu);

        // Valid choices
        int choice = InputHandler.getValidChoice(Set.of(8, 7, 6, 5, 4, 3, 2, 1, 0));
        
        switch (choice){
            case 1:
                break;
            case 2:
                //s = new Customer("a", "b");
                break;
            case 3:
                //s = new Customer("a", "b");
                break;
            case 4:
                break;
            case 0:
                System.out.println("\nExiting...");
                break;
        }
    }

    public static void displayData(Account account) {};

    public static void displayData(List<Medicine> medicines, boolean indexed) {
        asciiTable = new AsciiTable();
        
        // Header
        asciiTable.addRule();
        if (!indexed) { asciiTable.addRow("Name", "Price", "Amount", "Brand", "Expires At", "Purpose"); }
        else { asciiTable.addRow("Position #", "Name", "Price", "Amount", "Brand", "Expires At", "Purpose"); }
        asciiTable.addRule();

        // Insert data
        if (!indexed) {
            for (Medicine medicine : medicines) {
                asciiTable.addRow(
                    medicine.getName(), medicine.getPrice(), medicine.getAmount(),
                    medicine.getBrand(), medicine.getExpirationDate(), medicine.getPurpose()
                );
                asciiTable.addRule();
            }
        } else {
            int indexCounter = 0;
            for (Medicine medicine : medicines) {
                asciiTable.addRow(
                    indexCounter, medicine.getName(), medicine.getPrice(), medicine.getAmount(),
                    medicine.getBrand(), medicine.getExpirationDate(), medicine.getPurpose()
                );
                asciiTable.addRule();
                indexCounter++;
            }
        }

        // Render and print table to console
        String rend = asciiTable.render();
        System.out.println(rend);
    };

    public static void displayData(List<Medicine> medicines) {
        displayData(medicines, false);
    }
}
