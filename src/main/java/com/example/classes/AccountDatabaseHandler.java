package com.example.classes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AccountDatabaseHandler {
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
    
    // Getter
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
