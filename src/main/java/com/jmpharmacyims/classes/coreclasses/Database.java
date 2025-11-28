package com.jmpharmacyims.classes.coreclasses;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jmpharmacyims.classes.uimanager.MessageLog;

public class Database {

    private static Path customersDatabasePath = Path.of("accounts", "customers");
    private static Path pharmacyFilePath = Path.of("accounts", "JmPharmacy.json");
    private static Path adminFilePath = Path.of("accounts", "admin.json");
    private static Map<Account, Path> fileMap = new HashMap<>();
    private static ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    /**
     *
     * loops through a given directory and returns an iterator containing the
     * paths to the json files within the given directory
     *
     * @param directory - A directory storing json files
     * @return - A DirectoryStream<Path> iterator of the json files, or null if
     * an error occurs
     */
    public static List<Path> getCustomerJsonFileList() {
        List<Path> paths = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(customersDatabasePath, "*.json")) {
            for (Path path : stream) {
                paths.add(path);
            };

            return paths;
        } catch (IOException e) {
            MessageLog.logError(getCustomersDatabasePath().toAbsolutePath() + " is missing!");
        }

        return null;
    }

    // Method to save an object of account
    public static void save(Account data) {
        Path path = fileMap.get(data);
        if (path == null) {
            throw new IllegalStateException("Unknown object");
        }

        serialize(data, path);
    }

    public static void delete(Customer customer) {
        Path file = fileMap.get(customer);
        if (file == null) {
            throw new IllegalStateException("Unknown object");
        }

        // file may already be gone
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            MessageLog.logError("File operation occured.");
        }

        // remove tracking either way
        fileMap.remove(customer);
    }

    public static void createNew(Customer data) {
        Path path = customersDatabasePath.resolve(data.getName() + ".json");

        if (Files.exists(path)) {
            MessageLog.logError(data.getName() + "'s account already exists!");
            return;
        } else {
            MessageLog.logSuccess(data.getName() + "'s account has been successfully registered.");
        }

        serialize(data, path);
    }

    // Method to load an object of account 
    public static <T extends Account> T load(Path filePath, Class<T> account) {
        T obj = deserialize(filePath, account);
        if (obj != null) {
            fileMap.put(obj, filePath);
        }
        return obj;
    }

    // Method to load JSON data to object
    private static <T extends Account> T deserialize(Path filePath, Class<T> account) {
        try {
            return objectMapper.readValue(filePath.toFile(), account);
        } catch (IOException e) {
            MessageLog.logError("Failed to parse JSON: " + filePath + " is empty or missing.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Method to safely write changes to file
    private static void serialize(Account data, Path permanent) {
        try {
            if (permanent.getParent() != null) {
                Files.createDirectories(permanent.getParent());
            }

            boolean exists = Files.isRegularFile(permanent);

            Path temporary = permanent.resolveSibling(permanent.getFileName().toString() + ".tmp");

            if (exists) {
                // write to temp then atomically replace
                objectMapper.writeValue(temporary.toFile(), data);
                Files.move(temporary, permanent, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                // MessageLog.addSuccess(permanent + " updated successfully!");
            } else {
                // new file: write directly
                objectMapper.writeValue(permanent.toFile(), data);
                // MessageLog.addSuccess(permanent + " created successfully!");
            }
        } catch (IOException e) {
            MessageLog.logError("Failed to write file:\n" + e);
            try {
                Files.deleteIfExists(permanent.resolveSibling(permanent.getFileName().toString() + ".tmp"));
            } catch (IOException ignored) {
            }
        }
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

    public static List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (Path path : Database.getCustomerJsonFileList()) {
            Customer customer = Database.load(path, Customer.class);
            customers.add(customer);
        }

        return customers;
    }

    /**
     * Rebuilds missing directories and removes expired medicines from customers and pharmacy
     */
    public static void validateInitialDataFiles() {
        rebuildMissingDirectories();
        removeExpiredCustomerMedicines();
        removeExpiredPharmacyMedicines();
    }

    /**
     * Removes expired medicines from customers
     */
    private static void removeExpiredCustomerMedicines() {
        List<Customer> customers = loadCustomers();
        if (customers.isEmpty()) {
            return;
        }
        
        int counter = 0;
        // Loop through each customer and get their medicines
        for (Customer customer : customers) {
            List<Medicine> medicines = customer.getMedicines();
            List<Medicine> updatedMedicines = new ArrayList<>(); // For storing unexpired medicines

            for (Medicine medicine : medicines) {
                LocalDate today = LocalDate.now();
                DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yyyy");

                LocalDate medicineDate = LocalDate.parse(medicine.getExpirationDate(), format);

                // Ignore medicines that are expired
                if (medicineDate.isEqual(today) || medicineDate.isBefore(today)) {
                    counter++;
                    continue;
                }

                // Add unexpired medicine
                updatedMedicines.add(medicine);
            }

            // Update customer medicines
            customer.setMedicines(updatedMedicines);
            save(customer);

        }

        if (counter > 0) {
            MessageLog.logSuccess("Removed a total of " + counter + " expired medicines across customer accounts.");
        }
    }

    private static void removeExpiredPharmacyMedicines() {
        Pharmacy p = Database.load(pharmacyFilePath, Pharmacy.class);
        if (p == null) { return; }

        List<Medicine> medicines = p.getMedicines();
        List<Medicine> updatedMedicines = new ArrayList<>();
        int counter = 0;

        for (Medicine medicine : medicines) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yyyy");

            LocalDate medicineDate = LocalDate.parse(medicine.getExpirationDate(), format);

            // Ignore medicines that are expired
            if (medicineDate.isEqual(today) || medicineDate.isBefore(today)) {
                counter++;
                continue;
            }

            // Add unexpired medicine
            updatedMedicines.add(medicine);
        }

        // Update customer medicines
        p.setMedicines(updatedMedicines);
        save(p);

        if (counter > 0) {
            MessageLog.logSuccess("Removed a total of " + counter + " expired medicines from JmPharmacy.");
        }
    }

    /**
     * Rebuilds missing directories if they don't exist and logs messages
     */
    private static void rebuildMissingDirectories() {
        try {
            if (!Files.exists(customersDatabasePath)) {
                Files.createDirectories(customersDatabasePath);
                MessageLog.logSuccess("Rebuilt missing " + customersDatabasePath);
            }
            if (!Files.exists(adminFilePath)) {
                Files.createDirectories(adminFilePath.getParent());
                Files.createFile(adminFilePath);
                serialize(new Admin("System Admin", "admin", "admin123"), adminFilePath);
                MessageLog.logSuccess("Rebuilt missing " + adminFilePath);
            }
            if (!Files.exists(pharmacyFilePath)) {
                Files.createDirectories(pharmacyFilePath.getParent());
                Files.createFile(pharmacyFilePath);
                serialize(new Pharmacy("JmPharmacy", "user", "password", new ArrayList<>()), pharmacyFilePath);
                MessageLog.logSuccess("Rebuilt missing " + pharmacyFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
