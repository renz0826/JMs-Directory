import java.util.Scanner;
import java.util.Set;

// UI class for design
class UIManager extends Validation {
    
    // Login choice method
    public static void displayLoginChoice(){
        Scanner sc = new Scanner(System.in);

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
        int choice = getValidChoice(sc, Set.of(3, 2, 1, 0));

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

        sc.close();
    }

    public static void displayCustomerMenu(){
        Scanner sc = new Scanner(System.in);

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
        int choice = getValidChoice(sc, Set.of(4, 3, 2, 1, 0));

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
        
        sc.close();
    }

    public static void displayPharmacyMenu(){
        Scanner sc = new Scanner(System.in);

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

        System.out.print(menu);

        // Valid choices
        int choice = getValidChoice(sc, Set.of(4, 3, 2, 1, 0));
        Pharmacy p = new Pharmacy();
        switch (choice){
            case 1 -> p.addMedicine();
            case 2 -> p.searchMedicine();
            case 3 -> p.updateMedicineAmount();
            case 4 -> p.updateMedicinePrice(); 
            case 5 -> p.deleteMedicine();
            case 0 -> {
                System.out.println("\nExiting...");
                break;
            }
        }
        
        sc.close();
    }

    public static void displayAdminMenu(){
        Scanner sc = new Scanner(System.in);

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
        int choice = getValidChoice(sc, Set.of(8, 7, 6, 5, 4, 3, 2, 1, 0));

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
        
        sc.close();
    }
    
    
}
