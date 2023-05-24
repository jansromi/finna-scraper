package cli.services;

import java.util.List;

import org.json.JSONArray;

import cli.config.AppConfig;
import cli.utils.Util;
import fh.FinnaHaku;

/**
 * A class to process individual book entries. This class uses methods from the DatabaseService class
 * to process various aspects of a book entry like author, genre, topics, and publisher.
 */
public class DataProcessor {

	/**
	 * 
	 * @param fh The FinnaHaku object containing data of the book entry to be processed.
	 * @param bookId
	 */
	public static void processBookEntry(FinnaHaku fh, int bookId) {
		DatabaseService.processBook(fh, bookId);
		
		String publisher = fh.getPublisher();
	    
	    if (publisher != null) {
	    	publisher = Util.sanitizeInput(publisher);
	    	DatabaseService.processPublisher(publisher, bookId);
	    }
	    
	    List<String> authors = fh.getBookWriters();
	    
	    for (String author : authors) {
	    	String[] parsedAuthorData = Util.parseAuthor(author);
	    	DatabaseService.processAuthor(parsedAuthorData, bookId);
		}
	    
	    
	    List<String> genres = fh.getBookYKLClassesArray();
	    // if (genre == null)
	    
	    if (genres == null) {
	    	DatabaseService.processGenre(AppConfig.UNKNOWN_GENRE_DESC, bookId);
	    } else {
	    	
		    for (String genre : genres) {
				DatabaseService.processGenre(genre, bookId);
			}
		    
	    }
	    
	    List<String> topics = fh.getBookSubjects();
	    
	    for (String topicJSONArray : topics) {
			JSONArray arr = new JSONArray(topicJSONArray);
			
            // there might be multiple topics in a single topicJSONArray,
            // thus an inner loop is used to process each individual topic
			for (int i = 0; i < arr.length(); i++) {
			    String topic = arr.getString(i).replace(",", "");
			    DatabaseService.processTopic(topic, bookId);
			}
			
		}

	}
}
