package cli.utils;

public class Util {

	
	public static String sanitizeInput(String entry) {
	    return entry.replace(";", "").stripTrailing();
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * 
	 * TODO: How to handle inputs with multiple authors, array?
	 * 
	 * @example
	 * <pre name="test">
	 * sanitizeAuthors("Austen, Jane, kirjoittaja") === "Austen, Jane";
	 * sanitizeAuthors("Burroughs, William S., 1914-1997 kirjoittaja") === "Burroughs, William S.";
	 * sanitizeAuthors("Leary, Timothy Francis, 1920-1996, kirjoittaja Burroughs, William S., 1914-1997, kirjoittaja") === "Leary, Timothy Francis";
	 * </pre>
	 */
	public static String sanitizeAuthors(String input) {
		return "";
	}
}
