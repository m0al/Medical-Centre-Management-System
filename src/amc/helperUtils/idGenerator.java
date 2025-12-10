package amc.helperUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * This class creates next ids like U001, A001, P001, or F001.
 * It provides methods to generate unique identifiers with a given prefix,
 * ensuring sequential numbering and optionally checking against a list of existing IDs
 * to prevent collisions.
 */
public final class idGenerator {
    // Private constructor to prevent instantiation, as this is a utility class with static methods.
    private idGenerator() {}

    /**
     * Returns the next id with the same prefix and a 3-digit number.
     * This method is synchronized to ensure thread-safe ID generation.
     * It reads the last used ID from a JSON file, increments it, and updates the file.
     *
     * @param prefix The prefix for the ID (e.g., "U", "A", "P", "F", "R").
     * @return The newly generated unique ID.
     */

    public static synchronized String nextId(String prefix) {
        Map<String, String> lastIds = new HashMap<>();
        try {
            // Read existing IDs from the temporary ID file.
            // If the file doesn't exist or is empty, a new HashMap will be used.
            lastIds = (Map<String, String>) JsonStore.readObject(DataPaths.temporaryIdFilePath, HashMap.class);
            if (lastIds == null) {
                // Initialize if no data was read (e.g., file not found or empty).
                lastIds = new HashMap<>();
            }
        } catch (Exception e) {
            // If file doesn't exist or is corrupted, start with an empty map.
            // This ensures the application can proceed even if the ID file is problematic.
            System.err.println("Error reading temporary ID file, initializing new map: " + e.getMessage());
            lastIds = new HashMap<>();
        }

        // Get the last used ID for the given prefix, or start with "000" if none exists.
        String lastId = lastIds.getOrDefault(prefix, prefix + "000");
        // Extract the numeric part of the last ID.
        int currentNumber = extractNumber(lastId);
        // Increment the number to get the next sequential ID.
        int nextNumber = currentNumber + 1;
        // Format the new ID with the prefix and a 3-digit padded number (e.g., F001, F010, F100).
        String newId = prefix + String.format("%03d", nextNumber);

        // Update the map with the new ID and write it back to the temporary ID file.
        // This persists the last generated ID for future use.
        lastIds.put(prefix, newId);
        try {
            JsonStore.writeObject(DataPaths.temporaryIdFilePath, lastIds);
        } catch (Exception e) {
            // Log the error if writing fails. Depending on the application's robustness requirements,
            // this might warrant throwing a runtime exception or more severe logging.
            System.err.println("Error writing temporary ID file: " + e.getMessage());
        }

        // Return the newly generated ID.
        return newId;
    }

    /**
     * Extracts the numeric part from the end of an ID string.
     * If no numeric part is found, it returns 0.
     *
     * @param id The ID string from which to extract the number (e.g., "F001", "U123").
     * @return The integer number extracted from the ID, or 0 if no number is present.
     */
    private static int extractNumber(String id) {
        try {
            // Remove all non-digit characters from the ID string.
            String digits = id.replaceAll("\\D+", "");
            if (digits.length() == 0) {
                // If no digits are found, return 0.
                return 0;
            }
            // Parse the remaining digits string into an integer.
            return Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            // Catch NumberFormatException if parsing fails (e.g., digits string is too large).
            // Return 0 in case of an error during number extraction.
            System.err.println("Error parsing number from ID: " + id + " - " + ex.getMessage());
            return 0;
        } catch (Exception ex) {
            // Catch any other unexpected exceptions.
            System.err.println("Unexpected error extracting number from ID: " + id + " - " + ex.getMessage());
            return 0;
        }
    }

    /**
     * Returns the next id with the same prefix and a 3-digit number, ensuring it is unique
     * among a given list of existing IDs. This method is synchronized to ensure thread-safe
     * ID generation and uniqueness checking. It repeatedly generates new IDs until one is
     * found that is not present in the provided list of existing IDs.
     *
     * @param prefix The prefix for the ID (e.g., "U", "A", "P", "F", "R").
     * @param existingIds A list of existing IDs that the newly generated ID must not match.
     * @return The newly generated unique ID.
     */
    public static synchronized String nextId(String prefix, List<String> existingIds) {
        String newId;
        // Loop until a unique ID is generated.
        do {
            // Generate a new ID using the existing nextId(String) method.
            // This ensures sequential generation and persistence of the last used ID.
            newId = nextId(prefix);
        } while (existingIds.contains(newId)); // Keep generating until an ID not in existingIds is found.
        return newId;
    }
}
