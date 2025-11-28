package com.jmpharmacyims.classes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jmpharmacyims.classes.TextColor.Color;

public class Customer extends Account implements CanEditCredentials {

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

    public double getFunds() {
        return funds;
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    // -----------------------------------------------------------
    public void buyMedicine() {
        // 1. Check if Pharmacy can be loaded
        Pharmacy targetPharmacy = Database.load(Database.getPharmacyFilePath(), Pharmacy.class);

        if (targetPharmacy == null) {
            return;
        }

        List<Medicine> currentDisplayList = targetPharmacy.getMedicines();
        do {
            // 3. Render the prompt table and start point of the operation
            UIManager.clearScreen();
            UIManager.displayPopUp("+ Buy Medicine +", "Which medicine would you like to buy?");
            UIManager.displayMedicineTable(currentDisplayList);
            MessageLog.displayAll();
            System.out.println(TextColor.apply("\n[ Current Balance: Php " + getFunds() + " ]", Color.WHITE));

            System.out.println(TextColor.apply("\nInstructions: ", Color.WHITE));
            System.out.println(TextColor.apply("- Search medicine by position number", Color.LIGHT_YELLOW));
            System.out.println(TextColor.apply("- Search medicine by name", Color.LIGHT_YELLOW));
            System.out.println(TextColor.apply("- Enter 'q' to exit.", Color.LIGHT_RED));

            String input = InputHandler.readInput("\nEnter input: >> ");

            if (input.equalsIgnoreCase("q")) {
                return;
            }

            try {
                int pos = Integer.parseInt(input);
                if (pos >= 0 && pos < currentDisplayList.size()) {

                    Medicine selectedMedicine = currentDisplayList.get(pos);

                    if (selectedMedicine.getAmount() <= 0) {
                        MessageLog.logError("Item is out of stock.");
                        continue;
                    }

                    System.out.println("\nHow many units would you like?");
                    int quantity = InputHandler.readInt("\nUnits >> ");

                    if (quantity > selectedMedicine.getAmount()) {
                        MessageLog.logError("Only " + selectedMedicine.getAmount() + " units available.");
                        continue;
                    } else if (quantity <= 0) {
                        MessageLog.logError("Quantity cannot be 0.");
                        continue;
                    }

                    double totalCost = quantity * selectedMedicine.getPrice();

                    if (this.funds < totalCost) {
                        MessageLog.logError("Insufficient funds.");
                        continue;
                    }

                    String message = "Confirm purchase for Php " + totalCost + "? (y/n)";
                    System.err.println();
                    System.out.println(AsciiTableBuilder.buildSingleRow(message));

                    if (InputHandler.promptYesOrNo()) {

                        // 1. Deduct funds
                        this.funds -= totalCost;
                        UIManager.loading("Processing purchase");

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

                        String unitsOf;

                        if (quantity == 1) {
                            unitsOf = " unit of ";
                        } else {
                            unitsOf = " units of ";
                        }

                        MessageLog.logSuccess(quantity + unitsOf + selectedMedicine.getName() + " has been successfully bought for Php " + totalCost
                        );
                    }
                } else {
                    MessageLog.logError("Invalid position.");
                }
            } catch (NumberFormatException e) {
                List<Medicine> searchResult = targetPharmacy.searchMedicine(input);
                if (searchResult == null) {
                    MessageLog.logError("No results found.");
                    currentDisplayList = targetPharmacy.getMedicines();
                    continue;
                } else {
                    MessageLog.logSuccess("Found " + searchResult.size() + " results.");
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

        System.out.print("\n(Press Enter to return to menu)");
        InputHandler.readInput("", true);
    }

    public void depositFunds() {
        UIManager.clearScreen();
        UIManager.displayPopUp("+ Deposit Funds +", "How much would you like to deposit?");

        System.out.println(TextColor.apply("\n[ Current Balance: Php " + getFunds(), Color.WHITE) + " ]");

        double amount = InputHandler.readDouble("\nEnter amount >> ");

        UIManager.loading("Processing transaction");

        MessageLog.logSuccess("Php " + amount + " has been successfully added in your account.");

        this.funds += amount;

        MessageLog.displayNext();

        System.out.println(TextColor.apply("\n[ Updated Balance: Php " + getFunds(), Color.WHITE) + " ]");

        Database.save(this);
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
