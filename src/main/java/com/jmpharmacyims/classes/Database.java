package com.jmpharmacyims.classes;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Database {
    private static Path customersDatabasePath = Path.of("accounts", "customers");
    private static Path pharmacyFilePath = Path.of("accounts", "JmPharmacy.json");
    private static Path adminFilePath = Path.of("accounts", "admin.json");
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
    public static List<Path> getCustomerJsonFileList() {
        List<Path> paths = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(customersDatabasePath, "*.json")) {
            for (Path path : stream) {
                paths.add(path);
            };

            return paths;
        } catch (IOException e) {
            MessageLog.addError(getCustomersDatabasePath().toAbsolutePath() + " is missing!");
        }

        return null;
    }

    // Method to save an object of account
    public static void save(Account data) {
        Path path = objectFiles.get(data);
        if (path == null) throw new IllegalStateException("Unknown object");
        
        serialize(data, path);
    }

    public static void delete(Customer customer) {
        Path file = objectFiles.get(customer);
        if (file == null) throw new IllegalStateException("Unknown object");

        // file may already be gone
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            MessageLog.addError("File operation occured.");
        }

        // remove tracking either way
        objectFiles.remove(customer);
    }

    public static void createCustomer(Customer data) {
        createFile(data, customersDatabasePath);
    }

    // Method to load an object of account 
    public static <T extends Account> T load(Path filePath, Class<T> account) {
        T obj = deserialize(filePath, account);
        if (obj != null) {
            objectFiles.put(obj, filePath);
        }
        return obj;
    }

    // Method to load JSON data to object
    private static <T extends Account> T deserialize(Path filePath, Class<T> account) {
        try {
            return objectMapper.readValue(filePath.toFile(), account);
        } catch (IOException e) {
            MessageLog.addError("Failed to parse JSON: " + filePath + " is empty or missing.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Method to safely write changes to file
    private static void serialize(Account data, Path permanent) {
        try {
            // System.out.println("path=" + permanent + " exists=" + Files.exists(permanent) + " isRegularFile=" + Files.isRegularFile(permanent));
            if (permanent.getParent() != null) Files.createDirectories(permanent.getParent());

            boolean exists = Files.isRegularFile(permanent);

            Path temporary = permanent.resolveSibling(permanent.getFileName().toString() + ".tmp");

            if (exists) {
                // write to temp then atomically replace
                objectMapper.writeValue(temporary.toFile(), data);
                Files.move(temporary, permanent, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                MessageLog.addSuccess(permanent + " updated successfully!");
            } else {
                // new file: write directly
                objectMapper.writeValue(permanent.toFile(), data);
                MessageLog.addSuccess(permanent + " created successfully!");
            }
        } catch (IOException e) {
            MessageLog.addError("Failed to write file:\n" + e);
            try { Files.deleteIfExists(permanent.resolveSibling(permanent.getFileName().toString() + ".tmp")); }
            catch (IOException ignored) {}
        }
    }

    private static <T extends Account> void createFile(T data, Path basePath) {
        Path path = basePath.resolve(data.getName() + ".json");

        if (Files.exists(path)) {
            MessageLog.addError(path + " already exists!");
            return;
        }

        serialize(data, path);
    }

    // Getters
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    public static Path getCustomersDatabasePath() {
        return customersDatabasePath;
    }
    public static Path getAdminFilePath() {
        return adminFilePath;
    }
    public static Path getPharmacyFilePath() {
        return pharmacyFilePath;
    }
}
