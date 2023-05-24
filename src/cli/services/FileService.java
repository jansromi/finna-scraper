package cli.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * A class to handle file operations related to book entries.
 * 
 * Raison d'etre of this class is to help developing and testing the app and relations,
 * without actually using a database
 * @author Jansromi
 *
 */
public class FileService {
	
    /**
     * Appends a new entry to a given file.
     *
     * @param entryId The ID of the entry.
     * @param entry The entry to be appended.
     * @param filename The name of the file to which the entry should be appended.
     */
	public static void addEntryToFile(int entryId, String entry, String filename) {
	    File file = new File(filename);
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
	        writer.write(entryId + "|" + entry);
	        writer.newLine();
	        writer.flush();
	        System.out.println("The entry has been appended to " + filename + ".");
	    } catch (IOException e) {
	        System.out.println("An error occurred while writing to " + filename + ": " + e.getMessage());
	    }
	}
	
    /**
     * Retrieves the ID of a given entry from a given file.
     *
     * @param entry The entry whose ID should be retrieved.
     * @param filename The name of the file from which the entry's ID should be retrieved.
     * @return The ID of the given entry or -1 if the entry is not found.
     */
	public static int getEntryIdFromFile(String entry, String filename) {
	    File file = new File(filename);
	    try (Scanner scanner = new Scanner(file)) {
	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            if (line.isBlank()) continue;
	            String[] parts = line.split("\\|");
	            if (parts[1].equalsIgnoreCase(entry)) {
	                return Integer.parseInt(parts[0]);
	            }
	        }
	    } catch (FileNotFoundException e) {
	        try {
				file.createNewFile();
			} catch (IOException e1) {
				System.err.println("Failed to create new file to: " + file.getAbsolutePath());
			}
	    }
	    return -1;
	}
	
    /**
     * Generates a new ID for a new entry by finding the maximum existing ID in a given file
     * and adding 1 to it.
     *
     * @param filename The name of the file from which the maximum existing ID should be found.
     * @return The newly generated ID or -1 if an error occurs.
     */
	public static int generateNewEntryId(String filename) {
	    int maxEntryId = 0;
	    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            String[] parts = line.split("\\|");
	            if (line.isBlank()) continue;
	            int tempEntryId = Integer.parseInt(parts[0]);
	            if (tempEntryId > maxEntryId) {
	                maxEntryId = tempEntryId;
	            }
	        }
	    } catch (IOException e) {
	        System.out.println("An error occurred while reading " + filename + ": " + e.getMessage());
	        return -1;
	    }
	    return maxEntryId + 1;
	}
	
	/**
     * Retrieves the YKL ID of a given entry from a given file.
     * Because YKL-classificatio is a hierarchy, incrementing numbers cannot be used.
     * (30 | YLEINEN YHTEISKUNTATIEDE --> 30.1 | Sosiologia --> 30.11 | Sosiaaliset järjestelmät)
     * 
     * If an ykl-class is not found, -1 is assigned as an "unknown"-value.
     * 
     * @param entry The entry whose YKL ID should be retrieved.
     * @param filename The name of the file from which the entry's YKL ID should be retrieved.
     * @return The YKL ID of the given entry or "-1" if the entry is not found.
     */
	public static String getYKLIdFromFile(String entry, String filename) {
	    File file = new File(filename);
	    try (Scanner scanner = new Scanner(file)) {
	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            if (line.isBlank()) continue;
	            String[] parts = line.split("\\|");
	            if (parts[0].equalsIgnoreCase(entry)) {
	                return parts[0];
	            }
	        }
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    return "-1";
	}
}
