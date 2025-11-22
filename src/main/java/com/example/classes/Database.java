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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static Map<Account, Path> objectFiles = new HashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            
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

    // Method to save an object of account
    public static void save(Account data) {
        Path permanent = objectFiles.get(data);
        if (permanent == null) throw new IllegalStateException("Unknown object");
        
        Path temporary = permanent.resolveSibling(".tmp");
        serialize(data, temporary, permanent);
    }

    // Method to load an object of account 
    public static <T extends Account> T load(Path filePath, Class<T> account) {
        T obj = deserialize(filePath, account);
        if (obj != null) objectFiles.put(obj, filePath);
        return obj;
    }
    
    private static <T extends Account> T deserialize(Path filePath, Class<T> account) {
        try {
            return objectMapper.readValue(filePath.toFile(), account);
        } catch (Exception e) {
            e.printStackTrace();
        }  

        return null;
    }

    // Method to safely write changes to file
    private static void serialize(Account data, Path temporary, Path permanent) {
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

    // Getter
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
