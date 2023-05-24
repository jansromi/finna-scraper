package cli.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	/**
	 * ^([^,]+,[^,]+)          = match start of the string followed by 1+
	 *                           chars that aren't a comma, then a comma, and 
	 *                           another 1+ chars that aren't a comma.
	 *                           This matches the author name that is most commonly in the form
	 *                           "Surname, Firstname Middlename".
	 *
	 * (?:,\\s*([0-9]{1,4}))?  = matches an optional sequence that starts with a comma 
	 *                           followed by any amount of whitespace and 1 to 4 digits. 
	 *                           This matches the optional birthdate.
	 *
	 * (?:-([0-9]{1,4}))?      = matches an optional sequence that starts with a hyphen 
	 *                           and is followed by 1 to 4 digits. This matches the optional 
	 *                           date of death.
	 */
	private static final Pattern AUTHOR_PATTERN = Pattern.compile("^([^,]+,[^,]+)(?:,\\s*([0-9]{1,4})(?:-([0-9]{1,4}))?)?");
	
	public static String sanitizeInput(String entry) {
	    return entry.replace(";", "").stripTrailing();
	}
	
	/**
	 * @param entry
	 * @return 	[0] = Authors name
	 * 			[1] = Authors DOB, if availible
	 * 			[2] = Authors DOB, if availible.
	 * 
	 * @example
	 * <pre name="test">
	 * String[] s1 = parseAuthor("Leary, Timothy Francis, 1920-1996, kirjoittaja");
	 * s1[0] === "Leary, Timothy Francis";
	 * s1[1] === "1920";
	 * s1[2] === "1996";
	 * String[] s2 = parseAuthor("Forss, Timo Kalevi, 1967- kirjoittaja");
	 * s2[0] === "Forss, Timo Kalevi";
	 * s2[1] === "1967";
	 * s2[2] === null;
	 * String[] s3 = parseAuthor("Ginsberg, Allen, kirjoittaja");
	 * s3[0] === "Ginsberg, Allen";
	 * s3[1] === null;
	 * s3[2] === null;
	 * </pre>
	 */
	public static String[] parseAuthor(String entry) {
	    
	    // Initialize an array for storing the author's name, DOB, and DOD
	    String[] authorInfo = new String[3];
	    
	    // Create a matcher for the entry string
	    Matcher matcher = AUTHOR_PATTERN.matcher(entry);
	    
	    // Check if the matcher found a match
	    if (matcher.find()) {
	        // Store the author's name, DOB, and DOD in the array
	        authorInfo[0] = matcher.group(1).trim();
	        if (matcher.group(2) != null) {
	        	authorInfo[1] = matcher.group(2).trim();
	        	if (matcher.group(3) != null) {
	        		authorInfo[2] = matcher.group(3).trim();
	        	}
	        }
	    } else {
	        // If no match was found, the author's name is the entire entry string
	        authorInfo[0] = entry;
	    }
	    
	    // hate this hack. This has to do with Finnas inconsistent data formatting
	    authorInfo[0] = authorInfo[0].replaceAll("(?i)kirjoittaja", "").trim();
	    
	    // Return the array
	    return authorInfo;
	}
}
