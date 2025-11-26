package com.example.classes;

import java.util.ArrayList;
import java.util.List;

public class MessageLog {
    private static List<String> errorMessages = new ArrayList<>();

    /**
     * Method to add an error message to message List
     * 
     * @param message the error message to add
     */
    public static void addMessage(String message) {
        if (!message.isEmpty()) { errorMessages.add(message); }
    }

    /**
     * Method to display all error messages and clears the list
     * 
     */
    public static void displayAll() {
        if (!errorMessages.isEmpty()) {
            List<String> toRemove = new ArrayList<>();

            for (String message : errorMessages) {
                System.err.println(message);
                toRemove.add(message);
            }

            errorMessages.removeAll(toRemove);
        }
    }

    /**
     * Method to display the next error message and removes it immediately from the list
     * 
     */
    public static void displayNext() {
        if (!errorMessages.isEmpty()) {
            String next = errorMessages.getFirst();
            System.err.println(next);
            errorMessages.remove(next);
        }
    }

    /**
     * Method to display a given message using System.err.out without storing it in the list
     * 
     */
    public static void display(String errorMessage) {
        System.err.println(errorMessage);
    }
}
