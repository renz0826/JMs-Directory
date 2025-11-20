package com.example.classes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

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
        this.medicines = medicines;
        this.funds = funds;
    }

    public static void read(){
        AsciiTable at = new AsciiTable();
        

        at.addRule();
        at.addRow(null, "Account Details");
        at.setTextAlignment(TextAlignment.CENTER);
        at.addRule();
        at.addRow("row 2 col 1", "row 2 col 2");
        at.addRule();

        String rend = at.render();



        System.out.println(rend);

        
    }
}