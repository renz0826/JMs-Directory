package com.jmpharmacyims.classes.uimanager;

/**
 * Utility class for applying colors to console text
 */
public class TextColor {

    public enum Color {
        LIGHT_RED("\u001B[91m"),
        BRIGHT_BLACK("\u001b[90m"),
        LIGHT_GREEN("\u001B[38;2;22;218;114m"),
        LIGHT_YELLOW("\u001B[93m"),
        WHITE("\u001B[97m"),
        RESET("\u001B[0m");

        private final String code;

        Color(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    private TextColor() {
    }

    public static String apply(String message, Color color) {
        return color.getCode() + message + Color.RESET.getCode();
    }
}
