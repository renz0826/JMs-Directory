package com.example.classes;

import java.util.Set;

// UI class for design
class UIManager {
    
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

        System.out.print(menu);

        // Valid choices
        int choice = InputHandler.getValidChoice(Set.of(3, 2, 1, 0));

        Account c;
        
        switch (choice){
            case 1:
                c = new Customer(); //TODO: customer must not be parameterized
                c.login();
                break;
            case 2:
                //s = new Customer("a", "b");
                break;
            case 3:
                //s = new Customer("a", "b");
                break;
            case 0:
                System.out.println("\nExiting...");
                break;
        }
    }

    public static void displayCustomerMenu(){
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

        Account c;
        
        switch (choice){
            case 1:
                c = new Customer(); //TODO: customer must not be parameterized
                c.login();
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

    public static void displayPharmacyMenu(){
        // Menu display
        String menu = """
                =============================================
                |             + Pharmacy Menu +             |
                =============================================
                |                                           |
                |   1. Add Medicine                         |
                |   2. Search Medicine                      |
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
        Pharmacy p = new Pharmacy();
        int choice;
        boolean running = true;

        // Get user choice
        do {
            System.out.print(menu);

            switch (choice = InputHandler.getValidChoice(Set.of(4, 3, 2, 1, 0))){
                case 1 -> {
                    do {
                        p.addMedicine();
                        System.out.println("1. Add another medicine");
                        System.out.println("2. Back to menu");
                        System.out.print("Enter option: ");
                        choice = InputHandler.getValidChoice(Set.of(1, 2));
                    } while (choice != 2);
                }
                case 2 -> p.searchMedicine();
                case 3 -> p.updateMedicineAmount();
                case 4 -> p.updateMedicinePrice(); 
                case 5 -> p.deleteMedicine();
                case 0 -> {
                    System.out.println("\nExiting...");
                    running = false;
                }
            };
        } while (running);
    }

    public static void displayAdminMenu(){
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

        Account c;
        
        switch (choice){
            case 1:
                c = new Customer(); //TODO: customer must not be parameterized
                c.login();
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
}
