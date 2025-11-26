package com.jmpharmacyims.classes;

import java.util.ArrayList;
import java.util.List;

import com.jmpharmacyims.classes.TextColor.Color;

/**
 * Formats messages into either success or error messages for logging purposes
 * 
 */
public class MessageLog {
    private static List<String> messages = new ArrayList<>();

    /**
     * Method to add an error message to message List
     * 
     * @param message the error message to add
     */
    public static void addError(String message) {
        if (!message.isEmpty()) { 
            String coloredLabel = TextColor.apply("\n[ERROR]: ", Color.LIGHT_RED);
            messages.add(coloredLabel + message); 
        }
    }

    /**
     * Method to add a success message to message List
     * 
     * @param message the success message to add
     */
    public static void addSuccess(String message) {
        if (!message.isEmpty()) { 
            String coloredLabel = TextColor.apply("\n[SUCCESS]: ", Color.LIGHT_GREEN);
            messages.add(coloredLabel + message); 
        }
    }

    /**
     * Method to display all message logs and clears the list
     * 
     */
    public static void displayAll() {
        if (!messages.isEmpty()) {
            List<String> toRemove = new ArrayList<>();

            for (String message : messages) {
                System.err.println(message);
                toRemove.add(message);
            }

            messages.removeAll(toRemove);
        }
    }

    /**
     * Method to display the next message log and removes it immediately from the list
     * 
     */
    public static void displayNext() {
        if (!messages.isEmpty()) {
            String next = messages.getFirst();
            System.err.println(next);
            messages.remove(next);
        }
    }
}
