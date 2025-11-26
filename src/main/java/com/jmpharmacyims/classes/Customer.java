package com.jmpharmacyims.classes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jmpharmacyims.classes.TextColor.Color;

public class Customer extends Account {
    private List<Medicine> medicines;
    private double funds;

    @JsonCreator
    Customer(
            @JsonProperty("name") String name,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("medicines") List<Medicine> medicines,
            @JsonProperty("funds") double funds
    ) {
        super(name, username, password);
        // Ensure the list is not null to prevent crashes
        this.medicines = (medicines != null) ? medicines : new ArrayList<>();
        this.funds = funds;
    }

    // --- REQUIRED GETTERS (Jackson needs these to save data) ---
    public double getFunds() {
        return funds;
    }

    // [CRITICAL FIX]: You must have this method for medicines to appear in the JSON file
    public List<Medicine> getMedicines() {
        return medicines;
    }

    // -----------------------------------------------------------
    public void buyMedicine() {
        // 1. Check if Pharmacy can be loaded
        Pharmacy targetPharmacy = Database.load(Database.getPharmacyFilePath(), Pharmacy.class);

        if (targetPharmacy == null) return;

        // 2. Continue program if Pharmacy is ok
        String popUp = new AsciiTableBuilder()
                .setHeader("+ Buy Medicine +")
                .setRow("> Which medicine would you like to buy?")
                .buildGenericPopUpMenu();        
                
        List<Medicine> currentDisplayList = targetPharmacy.getMedicines();
        do {
            // 3. Render the prompt table and start point of the operation
            UIManager.clearScreen();
            System.out.println(popUp);
            UIManager.displayMedicineTable(currentDisplayList);
            MessageLog.displayAll();
            System.out.println("\n--- Current Balance: Php " + getFunds() + " ---");

            String instructions = """
                    \nInstructions: 
                    - Select medicine by entering its ** position number **.
                    - Search medicine by name or enter 'q' to exit.
                    """;
            System.out.println(TextColor.apply(instructions, Color.LIGHT_YELLOW));
            String input = InputHandler.readInput("\nEnter input: >> ");

            if (input.equalsIgnoreCase("q")) return;

            try {
                int pos = Integer.parseInt(input);
                if (pos >= 0 && pos < currentDisplayList.size()) {

                    Medicine selectedMedicine = currentDisplayList.get(pos);

                    if (selectedMedicine.getAmount() <= 0) {
                        MessageLog.addError("Item is out of stock.");
                        continue;
                    }

                    System.out.println("\nHow many units would you like?");
                    int quantity = InputHandler.readInt("\nUnits >> ");

                    if (quantity > selectedMedicine.getAmount()) {
                        MessageLog.addError("Only " + selectedMedicine.getAmount() + " units available.");
                        continue;
                    }

                    double totalCost = quantity * selectedMedicine.getPrice();

                    if (this.funds < totalCost) {
                        MessageLog.addError("Insufficient funds.");
                        continue;
                    }

                    System.out.println("\nConfirm purchase for Php " + totalCost + "? (y/n)");
                    String confirmation = InputHandler.readInput("(y/n): ");

                    if (confirmation.equalsIgnoreCase("y")) {

                        // 1. Deduct funds
                        this.funds -= totalCost;

                        // 2. Reduce Pharmacy Stock
                        targetPharmacy.updateMedicineAmount(selectedMedicine.getName(), -quantity);

                        // 3. Update Customer Inventory
                        boolean alreadyHas = false;
                        for (Medicine m : this.medicines) {
                            if (m.getName().equalsIgnoreCase(selectedMedicine.getName())
                                    && m.getBrand().equalsIgnoreCase(selectedMedicine.getBrand())) {
                                m.setAmount(m.getAmount() + quantity);
                                alreadyHas = true;
                                break;
                            }
                        }

                        if (!alreadyHas) {
                            // Create new medicine copying ALL fields from JmPharmacy.json
                            Medicine newMed = new Medicine(
                                    selectedMedicine.getName(),
                                    selectedMedicine.getBrand(),
                                    selectedMedicine.getPurpose(),
                                    selectedMedicine.getExpirationDate(),
                                    quantity,
                                    selectedMedicine.getPrice()
                            );
                            this.medicines.add(newMed);
                        }

                        // 4. SAVE CUSTOMER (This uses getMedicines() to write the file)
                        Database.save(this);
                        MessageLog.addSuccess("Purchased.");
                    }
                } else { MessageLog.addError("Invalid position."); }
            } catch (NumberFormatException e) {
                List<Medicine> searchResult = targetPharmacy.searchMedicine(input);
                if (searchResult == null) {
                    MessageLog.addSuccess("No results found.");
                    currentDisplayList = targetPharmacy.getMedicines();
                    continue;
                } else { 
                    MessageLog.addSuccess("Found " + searchResult.size() + " results.");
                    currentDisplayList = searchResult; 
                    continue;
                }
            }
        } while (true);
    }

    public void viewAccountDetails() {
        UIManager.clearScreen();
        UIManager.displayCustomerAccountDetails(this);
        List<Medicine> myMedicines = getMedicines();

        if (myMedicines == null || myMedicines.isEmpty()) {
            System.out.println("No items purchased yet.");
        } else {
            UIManager.displayMedicineTable(myMedicines);
        }

        // Pause
        System.out.print("\n(Press Enter to return to menu)");
        InputHandler.readInput("", true);
    }

    public void depositFunds() {
        UIManager.clearScreen();
        String popUp = new AsciiTableBuilder()
                .setHeader("+ Deposit Funds +")
                .setRow("> How much would you like to deposit?")
                .buildGenericPopUpMenu();
        System.out.println(popUp);

        this.funds += InputHandler.readDouble("Enter amount >> ");

        System.out.println("Funds: " + getFunds());

        Database.save(this);
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
