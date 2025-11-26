package com.jmpharmacyims.classes;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsciiTableBuilder {
    private String header;
    private List<String> bodyRows = new ArrayList<>();
    private String footer;

    public AsciiTableBuilder() {
    }

    public AsciiTableBuilder setHeader(String content) {
        this.header = content;
        return this;
    }

    public AsciiTableBuilder setRow(String content) {
        this.bodyRows.add(content);
        return this;
    }

    public AsciiTableBuilder setRows(String... contents) {
        this.bodyRows.addAll(Arrays.asList(contents));
        return this;
    }

    public AsciiTableBuilder setFooter(String content) {
        this.footer = content;
        return this;
    }

    public String buildGenericMenuTable() {
        // Validation checks
        if (header == null) {
            throw new IllegalStateException("Menu table is missing a header!");
        }
        if (bodyRows.isEmpty()) {
            throw new IllegalStateException("Menu table is missing rows!");
        }
        if (footer == null) {
            throw new IllegalStateException("Menu table is missing a footer!");
        }
  
        AsciiTable at = new AsciiTable();

        // Add header
        at.addRule();
        at.addRow(header)
          .setTextAlignment(TextAlignment.CENTER);
        at.addRule();

        // Add body rows
        for (String content : bodyRows) {
            at.addRow(content)
              .setTextAlignment(TextAlignment.LEFT)
              .setPadding(1)  // General padding
              .setPaddingLeft(7);  // Override left padding
        }
        at.addRule();

        // Add footer
        at.addRow(footer)
          .setPadding(1)
          .setPaddingLeft(7);
        at.addRule();

        // Return rendered table
        return at.render();
    }

    public String buildGenericPopUpMenu() {
        // Validation checks
        if (header == null) {
            throw new IllegalStateException("Menu table is missing a header!");
        }
        if (bodyRows.isEmpty()) {
            throw new IllegalStateException("Menu table is missing rows!");
        }
        
        AsciiTable at = new AsciiTable();

        at.addRule();
        at.addRow(header).setTextAlignment(TextAlignment.CENTER);
        at.addRule();
        
        at.addRow(bodyRows.getFirst())
            .setPadding(1)
            .setPaddingLeft(7)
            .setTextAlignment(TextAlignment.LEFT);
        at.addRule();

        return at.render();
    }

    public static String buildSingleRow(String content) {
        AsciiTable at = new AsciiTable();

        at.addRule();
        at.addRow(content);
        at.addRule();
        at.setTextAlignment(TextAlignment.CENTER);

        return at.render();
    } 

    public static String buildMedicineTable(List<Medicine> medicines) {
        AsciiTable asciiTable = new AsciiTable();

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

        return asciiTable.render();
    }

    public static String buildCustomerTable(List<Customer> customers) {
        AsciiTable asciiTable = new AsciiTable();

        asciiTable.addRule();
        asciiTable.addRow("Position #", "Name", "Username", "Password", "Funds");
        asciiTable.addRule();

        int indexCounter = 0;
        for (Customer customer : customers) {
            asciiTable.addRow(
                indexCounter, customer.getName(), customer.getUsername(), 
                customer.getPassword(), customer.getFunds()
            );
            asciiTable.addRule();
            indexCounter++;
        }

        return asciiTable.render();
    }

    public static String buildCustomerAccountDetails(Customer customer) {
        AsciiTable at = new AsciiTable();

        at.addRule();
        at.addRow("Name", customer.getName());
        at.addRule();
        at.addRow("Username", customer.getUsername());
        at.addRule();
        at.addRow("Current Balance", String.format("PHP %,.2f", customer.getFunds()));
        at.addRule();

        // Styling for Data
        at.setTextAlignment(TextAlignment.LEFT);
        at.setPadding(1);

        return at.render();
    }
}
