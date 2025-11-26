package com.jmpharmacyims.classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Set;

import com.jmpharmacyims.classes.TextColor.Color;

import java.lang.Math;

// This class handles user input and input validation
public class InputHandler {
    private static final Scanner SCAN = new Scanner(System.in);
    private static String errorLabel = TextColor.apply("\n[ERROR]: ", Color.LIGHT_RED);

    // Private helper to check for EOF and exit gracefully
    private static void checkForEof() {
        if (!SCAN.hasNextLine()) {
            System.out.println(
                TextColor.apply("\n[INFO]: ", Color.LIGHT_YELLOW) + 
                "Input stream closed (EOF detected). Exiting...");
            close();
            System.exit(0); // Clean exit
        }
    }

    /**
     * Reads a line with EOF handling
     */
    private static String safeNextLine(String prompt) {
        System.out.print(prompt);
        checkForEof();
        return SCAN.nextLine().trim();
    }

    /**
     * Returns an integer based on the valid choices the user entered
     * 
     * Prompts the user to enter a valid choice. Empty inputs are not allowed.
     *  
     * @param validChoices the set of available choices
     * @return the valid choice as integer
     */
    public static int getValidChoice(Set<Integer> validChoices) {
        while (true) {
            // Validate inputs
            try {
                int choice = readInt("\nEnter Choice >> ");

                // Checks if integer is one of the allowed choices
                if (validChoices.contains(choice)) {
                    // Input is valid
                    return choice;
                } else {
                    // Input is numeric but not allowed
                    System.err.println(errorLabel + "Invalid choice. \nAllowed: " + validChoices + "");
                }
            } catch (NumberFormatException e) {
                // If input is not numeric
                System.err.println(errorLabel + "Invalid input. \nPlease enter an integer.");
            }
        }
    }

    /**
     * Reads a line of input from the user.
     *
     * Prompts the user with the given message and repeatedly reads input until
     * a string is provided. Input is trimmed of leading and trailing
     * whitespace before being returned.
     *
     * @param prompt the message displayed to the user before reading input
     * @param allowEmpty boolean value whether to allow empty input or not, it is false by default (not specified)
     * @return a trimmed string entered by the user
     */
    public static String readInput(String prompt, boolean allowEmpty) {
        String input;

        while (true) {
            input = safeNextLine(prompt);

            if (input.isEmpty() && !allowEmpty) {
                System.err.println(errorLabel + "Input cannot be empty");    
                continue;
            }
            return input;
        }
    }

    /**
     * Reads a non-empty line of input from the user
     * @see #readInput(String, boolean)
     */
    public static String readInput(String prompt) {
        return readInput(prompt, false);
    }
    
    /**
     * Reads an integer from the user.
     *
     * Prompts the user with the given message and repeatedly reads input until
     * a valid integer is provided. Input is validated to ensure it is a valid
     * integer. This method uses {@link #readNonEmptyLine(String)} to handle
     * input collection and trimming.
     *
     * @param prompt the message displayed to the user before reading input
     * @param allowNegative whether negatives are allowed or not
     * @return an integer entered by the user
     * @see #readNonEmptyLine(String)
     */
    public static int readInt(String prompt, boolean allowNegative) {
        String input;
        int i;

        while (true) {
            input = readInput(prompt);

            try {
                i = Integer.parseInt(input);

                if (i < 0 && !allowNegative) {
                    System.err.println(errorLabel + "Cannot be negative");
                    continue;
                }

                return i;
            } catch (NumberFormatException e) {
                System.err.println(errorLabel + "Input must be an integer.");
            } catch (Exception e) {
                System.err.println(errorLabel + e);
            }
        }
    }

    /**
     * Reads a non-negative integer from the user.
     * @see #readInt(String, boolean)
     */
    public static int readInt(String prompt) {
        return readInt(prompt, false);
    }

    /**
     * Reads a non-negative double from the user.
     *
     * Prompts the user with the given message and repeatedly reads input until
     * a valid non-negative double is provided. Input is validated to ensure it
     * is a valid double and is not negative. This method uses
     * {@link #readNonEmptyLine(String)} to handle input collection and
     * trimming.
     *
     * @param prompt the message displayed to the user before reading input
     * @return a non-negative double entered by the user
     * @see #readNonEmptyLine(String)
     */
    public static double readDouble(String prompt) {
        String input;
        double d;

        while (true) {
            input = readInput(prompt);

            try {
                d = Double.parseDouble(input);

                if (d < 0) {
                    System.err.println(errorLabel + "Cannot be negative.");
                    continue;
                }

                return Math.round(d * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                System.err.println(errorLabel + "Input must be a number.");
            } catch (Exception e) {
                System.err.println(errorLabel + e);
            }
        }
    }

    /**
     * Reads a date from the user in {@code d/MM/yyyy} format.
     *
     * Prompts the user with the given message and repeatedly reads input until
     * a valid date in {@code d/M/yyyy} format is provided. This method uses
     * {@link #readNonEmptyLine(String)} to handle input collection and
     * trimming.
     *
     * @param prompt the message displayed to the user before reading input
     * @return a date string in {@code d/M/yyyy} format entered by the user
     * @see #readNonEmptyLine(String)
     */
    public static String readMedicineDate(String prompt) {
        String input;
        LocalDate today = LocalDate.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yyyy");

        while (true) {
            input = readInput(prompt);

            try {
                LocalDate date = LocalDate.parse(input, format);

                // If the date given is before or equal to today then reject it and prompt the user again
                if (date.isEqual(today) || date.isBefore(today)) {
                    System.err.println(errorLabel + "Medicine is already expired.");
                    continue;
                }

                return date.format(format);
            } catch (DateTimeParseException e) {
                System.err.println(errorLabel + "Incorrect date format. Use d/M/yyyy");
            }
        }
    }


    /**
     * Returns true if the user inputs 'y' and false if 'n'.
     * 
     * Prompts the user a (y/n) input and compares against those characters (case-insensitive).
     * Valid inputs are either Y or N. Empty inputs are not allowed
     * 
     * @return true if y, false if n
     */
    public static boolean promptYesOrNo() {
        do {
            String confirmation = readInput("(y/n) >> ");
            if (confirmation.equalsIgnoreCase("y")) return true;
            else if (confirmation.equalsIgnoreCase("n")) return false;
            else continue;
        } while (true);
    }

    // Reminder: Call this on the very last line of the "main" program
    public static void close() {
        SCAN.close();
    }
}