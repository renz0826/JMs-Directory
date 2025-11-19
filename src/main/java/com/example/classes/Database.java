package com.example.classes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            

    // Function to safely write changes to file
    public static void saveToFile(Path temporary, Path permanent, Account data) {
        // XXX: The nested try-catch was created for fun, idk if its a good idea
        try {
            objectMapper.writeValue(temporary.toFile(), data); // write changes to temporary file
            Files.move(temporary, permanent, StandardCopyOption.REPLACE_EXISTING); // replace temporary file name to permanent file name

            System.out.println("Data successfully written to " + permanent.toAbsolutePath().toString());
        } catch (NoSuchFileException e) {
            System.err.println("[ERROR]: " + permanent + " does not exist");
            System.out.println("[SYSTEM]: Attempting to recreate " + permanent);
            try {
                Files.createDirectories(permanent);
                System.out.println("[SYSTEM]: " + permanent + " created. Please enter the information again.");
            } catch (IOException innerE) {
                System.err.println("[ERROR]: File cannot be created:\n" + e);
            }
        } catch (IOException e) {
            System.err.println("[ERROR]: A file operation error has occured:\n" + e);
        }
    }
    
    /**
     * 
     * loops through a given directory and returns an iterator containing the paths to the json files
     * within the given directory
     * 
     * @param directory - A directory storing json files 
     * @return 
     *  - A DirectoryStream<Path> iterator of the json files, or null if an error occurs
     */ 
    public static List<Path> getJsonFilePaths(Path directory) {
        List<Path> paths = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.json")) {
            for (Path path : stream) {
                paths.add(path);
            };

            return paths;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Loads a Customer object from a JSON file.
     * 
     * @param filePath - The path to the JSON file containing customer data
     * @return A Customer object deserialized from the file, or null if an error occurs
     */
    public static Customer loadCustomer(Path filePath) {
        try {
            return objectMapper.readValue(filePath.toFile(), Customer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }  

        return null;
    }

    /**
     * Loads an Admin object from a JSON file.
     * 
     * @param filePath - The path to the JSON file containing admin data
     * @return An Admin object deserialized from the file, or null if an error occurs
     */
    public static Admin loadAdmin(Path filePath) {
        try {
            return objectMapper.readValue(filePath.toFile(), Admin.class);
        } catch (Exception e) {
            e.printStackTrace();
        }  
        
        return null;
    }

    /**
     * Loads a Pharmacy object from a JSON file.
     * 
     * @param filePath - The path to the JSON file containing pharmacy data
     * @return A Pharmacy object deserialized from the file, or null if an error occurs
     */
    public static Pharmacy loadPharmacy(Path filePath) {
        try {
            return objectMapper.readValue(filePath.toFile(), Pharmacy.class);
        } catch (Exception e) {
            e.printStackTrace();
        }  
        
        return null;
    }

    // Getter
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
