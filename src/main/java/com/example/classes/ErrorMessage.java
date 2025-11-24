package com.example.classes;

import java.util.ArrayList;
import java.util.List;

// NOTE: Consider renaming this class to LogHandler to clarify that it handles log messages 
public class ErrorMessage {
    // NOTE: Consider refactoring this into a Queue if message priority is truly necessary
    private static List<String> errorMessages = new ArrayList<>();

    // Method to add an error message
    public static void queueMessage(String message) {
        if (!message.isEmpty()) {
            errorMessages.add(message);
        }
    }

    // Method to display error messages and clears it from the list
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

    // Method to display the next error message and removes it immediately
    public static void displayNext() {
        if (!errorMessages.isEmpty()) {
            String next = errorMessages.getFirst();
            System.err.println(next);
            errorMessages.remove(next);
        }
    }

    // Method to display a given message using System.err.out
    // ONLY USE THIS FOR ERROR MESSAGES
    // Does not store the message in the list
    public static void display(String errorMessage) {
        System.err.println(errorMessage);
    }
}
