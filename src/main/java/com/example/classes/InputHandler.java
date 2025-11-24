package com.example.classes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Set;

// This class handles user input and input validation
public class InputHandler {

    private static final Scanner SCAN = new Scanner(System.in);

    // Method for retrieving and validating inputs
    public static int getValidChoice(Set<Integer> validChoices) {
        int choice;

        while (true) {
            String input = readInput("\nEnter Choice >> ");

            // Validate inputs
            try {
                choice = Integer.parseInt(input); // Convert strings to int

                // Checks if integer is one of the allowed choices
                if (validChoices.contains(choice)) {
                    // Input is valid
                    return choice;
                } else {
                    // Input is numeric but not allowed
                    System.out.println("\nInvalid choice. \nAllowed: " + validChoices + "");
                }
            } catch (NumberFormatException e) {
                // If input is not numeric
                System.out.println("\nInvalid input. \nPlease enter an integer.");
            }
        }
    }

    /**
     * Reads a non-empty line of input from the user.
     *
     * Prompts the user with the given message and repeatedly reads input until
     * a non-empty string is provided. Input is trimmed of leading and trailing
     * whitespace before being returned.
     *
     * @param prompt the message displayed to the user before reading input
     * @return a trimmed, non-empty string entered by the user
     */
    public static String readInput(String prompt, boolean allowEmpty) {
        String input;

        while (true) {
            System.out.print(prompt);
            input = SCAN.nextLine();
        
            if (input.isEmpty() && !allowEmpty) {
                System.err.println("\n[ERROR]: Input cannot be empty");    
                continue;
            }
            input = input.trim();
            return input;
        }
    }

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
                    System.err.println("\n[ERROR]: Cannot be negative");
                    continue;
                }

                return i;
            } catch (NumberFormatException e) {
                System.err.println("\n[ERROR]: Input must be an integer.");
            } catch (Exception e) {
                System.err.println("\n[ERROR]: " + e);
            }
        }
    }

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
                    System.err.println("\n[ERROR]: Cannot be negative.");
                    continue;
                }

                return d;
            } catch (NumberFormatException e) {
                System.err.println("\n[ERROR]: Input must be a number.");
            } catch (Exception e) {
                System.err.println("\n[ERROR]: " + e);
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
    public static String readDate(String prompt) {
        String input;
        LocalDate date;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yyyy");

        while (true) {
            input = readInput(prompt);

            try {
                date = LocalDate.parse(input, format);
                return date.format(format);
            } catch (DateTimeParseException e) {
                System.err.println("\n[ERROR]: Incorrect date format. Use d/M/yyyy");
            }
        }
    }

    // Reminder: Call this on the very last line of the "main" program
    public static void close() {
        SCAN.close();
    }
}
