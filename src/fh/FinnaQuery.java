package fh;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Class for Finna-library queries with an ISBN-code. FinnaHaku-object contains relevant information
 * about the book after the second query. 
 * 
 * 
 * TODO: Implement major refactoring to class
 * 		- Instead of having an instance to each query,
 * 		  make finnahaku a book-generator, that returns queried books
 * 		- FinnaHaku would be initialized with an ISBN, or list of ISBNs,
 * 	      and it would return those books as a list of books.
 *      - returns with yield or something similar would be cool
 * 
 * TODO: TESTS!
 * 
 * @author Jansromi
 * @version 25.4.2023
 *
 */
public class FinnaQuery {
	private static final String isbnUrl = "https://api.finna.fi/v1/search?lookfor=";
	private static final String finnaUrl = "https://api.finna.fi/v1/record?id=";
	
	
	private static final String finnaParams = "&field[]=authors&field[]=title&field[]=publishers&field[]=publicationDates"
            						+ "&field[]=classifications&field[]=subjects&field[]=year&field[]=languages&"
            						+ "field[]=summary&prettyPrint=0";
	private String isbn;
	private String finnaId;
	private String bookTitle;
	
	private List<String> bookWriter = new ArrayList<String>();
	private List<String> bookSubjects = new ArrayList<String>();
	private List<String> bookYKLClasses = new ArrayList<String>();
	private List<String> bookLanguages = new ArrayList<String>();
	
	// Need to figure what to do, if API gives many values for bookPublisher.
	// Right now we return the first item.
	private List<String> bookPublisher = new ArrayList<String>();
	private List<String> bookPublicationDates = new ArrayList<String>();
	
	private String rawResponse;
	private JSONObject bookData;
	
	public static class BookNotFoundException extends Exception {
        private static final long serialVersionUID = 1L;
        public BookNotFoundException(String msg) {
            super(msg);
        }
    }
	
	/**
	 * Constructor with isbn. Does a query with 
	 * given isbn and sets the FinnaID for the book.
	 * @param isbn
	 */
	public FinnaQuery(String isbn) {
		this.isbn = isbn;
		query(true);
		finnaId = FinnaParser.parseId(rawResponse);
	}
	
	/**
	 * Sets the relevant data for the book
	 * @throws BookNotFoundException 
	 */
	public void fetchBookData() throws BookNotFoundException {
		if (finnaId == null) {
			System.err.println("FinnaID not found!");
			throw new BookNotFoundException("Book was not found");
		}
		query(false);
		bookData = FinnaParser.parseFirstRecord(rawResponse);
		bookTitle = FinnaParser.parseBookTitle(bookData);
		bookWriter = FinnaParser.parseWriter(bookData);
		bookSubjects = FinnaParser.parseSubjects(bookData);
		bookLanguages = FinnaParser.parseLanguage(bookData);
		try {
			bookYKLClasses = FinnaParser.parseYKL(bookData);
		} catch (JSONException e) {
			System.err.println("YKL-class not found: " + e.getMessage());
		}
		bookPublisher = FinnaParser.parsePublishers(bookData);
		bookPublicationDates = FinnaParser.parsePublicationDates(bookData);
	}
	
	/**
	 * Executes a varying Finna-query, depending on the boolean.
	 * On the first query (firstSearch = true), raw response contains a JSON of all the books found with the ISBN.
	 * After that, (firstSearch = false), raw response contains a JSON of all the params defined in finnaParams.
	 * 
	 * @param firstSearch = true, if searching with an ISBN
	 * 						false, if searching with an FinnaID
	 */
	public void query(boolean firstSearch) {
		String url;
		if (firstSearch && this.isbn != null) url = isbnUrl + this.isbn;
		else if (this.finnaId != null) url = finnaUrl + this.finnaId + finnaParams;
		else return;
		
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
		    .build();
		try {
    	    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
    	    this.rawResponse = response.body();
    	} catch (MalformedURLException e) {
    		System.err.println(e.getMessage());
    	} catch (IOException e) {
    		System.err.println( e.getLocalizedMessage());
    	} catch (InterruptedException e) {
    		System.err.println(e.getLocalizedMessage());
    	}
	
	}
	
	/**
	 * @return the bookTitle
	 */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
	 * @return the bookWriter
	 */
	public String getBookWriter() {
		StringBuilder writers = new StringBuilder();
		for (String s : bookWriter) {
			writers.append(s + "; ");
		}
		return writers.toString();
	}
	
	/**
	 * @return the bookWriter
	 */
	public List<String> getBookWriters() {
		return bookWriter;
	}

	/**
	 * @return the bookSubjects
	 */
	public List<String> getBookSubjects() {
		return bookSubjects;
	}

	/**
	 * @return the bookYKLClasses
	 */
	public String getBookYKLClasses() {
		StringBuilder classes = new StringBuilder();
		if (bookYKLClasses == null) return "";
		for (String s : bookYKLClasses) {
			classes.append(s + "; ");
		}
		return classes.toString();
	}
	

	public List<String> getBookYKLClassesArray() {
		if (bookYKLClasses.size() == 0) return null;
		return bookYKLClasses;
	}
	
	/**
	 * @return the bookLanguages
	 */
	public String getBookLanguages() {
		StringBuilder langs = new StringBuilder();
		for (String s : bookLanguages) {
			langs.append(s + "; ");
		}
		return langs.toString();
	}

	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
	}
	
	/**
	 * @return first (and usually only) item of bookPublisher-list.
	 */
	public String getPublisher() {
		if (bookPublisher.size() == 0) return null;
		return bookPublisher.get(0);
	}
	
	/**
	 * @return first (and usually only) item of bookPublicationDates-list.
	 */
	public String getPublicationDates() {
		return bookPublicationDates.get(0);
	}

	/**
	 * @return the finnaId
	 */
	public String getFinnaId() {
		return finnaId;
	}

	/**
	 * @return the rawResponse
	 */
	public String getRawResponse() {
		return rawResponse;
	}

	public static void main(String[] args) throws BookNotFoundException {
		FinnaQuery haku = new FinnaQuery("9789524831420");
		haku.fetchBookData();
		System.out.println(haku.getBookTitle());
	}
}
