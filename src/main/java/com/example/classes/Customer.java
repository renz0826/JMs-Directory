package com.example.classes;

import java.nio.file.Path;
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

    private Path permanentFile = Path.of(Account.ROOT_DIRECTORY, "customers", "Alice.json");
    private Path temporaryFile = Path.of(permanentFile.toString() + ".tmp"); // temporary filepath

    @JsonCreator
    Customer(
            @JsonProperty("name") String name,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("medicines") List<Medicine> medicines,
            @JsonProperty("funds") double funds
    ) {
        super(name, username, password);
        this.medicines = medicines;
        this.funds = funds;
    }

    public double getFunds() {
        return funds;
    }

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

        Database.saveToFile(temporaryFile, permanentFile, this);
    }
}
