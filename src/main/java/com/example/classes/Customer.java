package com.example.classes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.vandermeer.asciitable.AT_Cell;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

public class Customer extends Account {

    private static AsciiTable at;

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
        at = new AsciiTable();
        at.addRule();
        at.addRow("+ Buy Medicine +");
        at.setTextAlignment(TextAlignment.CENTER);
        at.addRule();
        AT_Cell cell = at.addRow("> Which medicine would you like to buy?").getCells().get(0);
        cell.getContext().setPadding(1).setPaddingLeft(7);
        cell.getContext().setTextAlignment(TextAlignment.LEFT);
        at.addRule();
        String rend = at.render();
        System.out.println(rend);

        // 1. Load the specific Pharmacy instance
        Pharmacy targetPharmacy = Database.loadJmPharmacy("JmPharmacy.json");

        if (targetPharmacy == null) {
            System.out.println("[ERROR]: Failed to load Jm Pharmacy data.");
            return;
        }

        List<Medicine> currentDisplayList = targetPharmacy.getMedicines();

        do {
            System.out.println("\n--- Current Balance: $" + this.funds + " ---");
            UIManager.displayData(currentDisplayList, true);

            System.out.println("Instructions: ");
            System.out.println("- Select medicine by entering its **position number**.");
            System.out.println("- Search medicine by name or enter 'q' to exit.");

            String input = InputHandler.readNonEmptyLine("Enter input: ");

            if (input.equalsIgnoreCase("q")) {
                break;
            }

            try {
                int pos = Integer.parseInt(input);
                if (pos >= 0 && pos < currentDisplayList.size()) {

                    Medicine selectedMedicine = currentDisplayList.get(pos);

                    if (selectedMedicine.getAmount() <= 0) {
                        System.out.println("[ERROR]: Item is out of stock.");
                        continue;
                    }

                    int quantity = InputHandler.readInt("How many units to buy? ", true);

                    if (quantity > selectedMedicine.getAmount()) {
                        System.out.println("[ERROR]: Only " + selectedMedicine.getAmount() + " units available.");
                        continue;
                    }

                    double totalCost = quantity * selectedMedicine.getPrice();

                    if (this.funds < totalCost) {
                        System.out.println("[ERROR]: Insufficient funds.");
                        continue;
                    }

                    System.out.println("Confirm purchase for $" + totalCost + "? (y/n)");
                    String confirmation = InputHandler.readNonEmptyLine("(y/n): ");

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

                        System.out.println("\n[SUCCESS]: Purchased.");
                        currentDisplayList = targetPharmacy.getMedicines(); // Refresh list
                    }
                } else {
                    System.out.println("[ERROR]: Invalid position.");
                }
            } catch (NumberFormatException e) {
                List<Medicine> searchResult = targetPharmacy.searchMedicine(input);
                if (searchResult == null) {
                    System.out.println("No results found.");
                    currentDisplayList = targetPharmacy.getMedicines();
                } else {
                    currentDisplayList = searchResult;
                }
            }
        } while (true);
    }

    public void viewAccountDetails() {
        // ==========================================
        // PART 1: The Title (1 Column Table)
        // ==========================================
        AsciiTable titleTable = new AsciiTable();
        titleTable.addRule();
        titleTable.addRow("+ Account Details +");
        titleTable.setTextAlignment(TextAlignment.CENTER);
        titleTable.addRule();
        System.out.println(titleTable.render());

        // ==========================================
        // PART 2: The Data (2 Column Table)
        // ==========================================
        at = new AsciiTable(); // <--- CRITICAL: Create a NEW table for 2 columns

        at.addRule();
        at.addRow("Name", this.getName());
        at.addRule();
        at.addRow("Username", this.getUsername());
        at.addRule();
        at.addRow("Current Balance", String.format("PHP %,.2f", this.funds));
        at.addRule();

        // Styling for Data
        at.setTextAlignment(TextAlignment.LEFT);
        at.setPadding(1);

        System.out.println(at.render());
        System.out.println();

        // ==========================================
        // PART 3: Medicine Cabinet Header (1 Column)
        // ==========================================
        AsciiTable headerTable = new AsciiTable();

        headerTable.addRule();
        headerTable.addRow("+ Medicine Cabinet +");
        headerTable.setTextAlignment(TextAlignment.CENTER);
        headerTable.addRule();

        System.out.println(headerTable.render());

        // ==========================================
        // PART 4: Inventory List
        // ==========================================
        List<Medicine> myMedicines = getMedicines();

        if (myMedicines == null || myMedicines.isEmpty()) {
            System.out.println("No items purchased yet.");
        } else {
            UIManager.displayData(myMedicines);
        }

        // Pause
        System.out.println("\n(Press Enter to return to menu)");
        try {
            System.in.read(); // Correctly waits for Enter key
        } catch (Exception e) {
            // Ignore errors
        }
    }

    public void depositFunds() {

        at = new AsciiTable();

        at.addRule();
        at.addRow("+ Deposit Funds +");
        at.setTextAlignment(TextAlignment.CENTER);
        at.addRule();
        AT_Cell cell = at.addRow("> How much would you like to deposit?").getCells().get(0);
        cell.getContext().setPadding(1).setPaddingLeft(7);
        cell.getContext().setTextAlignment(TextAlignment.LEFT);
        at.addRule();
        String rend = at.render();

        System.out.println(rend);

        double value = InputHandler.getDoubleChoice();

        this.funds += value;

        System.out.println("Funds: " + getFunds());

        Database.save(this);
    }
}
