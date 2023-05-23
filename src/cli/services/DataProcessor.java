package cli.services;

import java.util.List;

import org.json.JSONArray;

import cli.utils.Util;
import fh.FinnaHaku;

public class DataProcessor {

	public static void processBookEntry(FinnaHaku fh, int bookId) {
	    String author = Util.sanitizeInput(fh.getBookWriter());
	    DatabaseService.processAuthor(author, bookId);
	    
	    List<String> genres = fh.getBookYKLClassesArray();
	    // if (genre == null)
	    
	    for (String genre : genres) {
			DatabaseService.processGenre(genre, bookId);
		}
	    
	    List<String> topics = fh.getBookSubjects();
	    
	    for (String topicJSONArray : topics) {
			JSONArray arr = new JSONArray(topicJSONArray);
			
			// Feeling uneasy about this inner loop
			for (int i = 0; i < arr.length(); i++) {
			    String topic = arr.getString(i).replace(",", "");
			    DatabaseService.processTopic(topic, bookId);
			}
			
		}
	    String publisher = fh.getPublisher();
	    if (publisher == null) {
	    	publisher = "NULL";
	    }
	    else {
	    	 publisher = Util.sanitizeInput(publisher);
	    }
	    int pub = DatabaseService.processPublisher(publisher, bookId);
	    DatabaseService.processBook(fh, bookId, pub);
	}
}
