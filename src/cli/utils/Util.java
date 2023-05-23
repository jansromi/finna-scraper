package cli.utils;

public class Util {

	public static String sanitizeInput(String entry) {
	    return entry.replace(";", "").stripTrailing();
	}
}
