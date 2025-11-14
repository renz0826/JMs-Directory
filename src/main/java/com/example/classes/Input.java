package com.example.classes;

import java.util.Scanner;

// Use this class if you want to use Scanner functions
public class Input {
    private static final Scanner SCAN = new Scanner(System.in);

    public static String readString() {
        return SCAN.nextLine();
    }

    public static int readInteger() {
        return SCAN.nextInt();
    }

    public static double readDouble() {
        return SCAN.nextDouble();
    }

    // Reminder: Call this on the very last line of the "main"
    public static void close() {
        SCAN.close();
    }

    // Add additional methods such as readBoolean if neccessary
}
