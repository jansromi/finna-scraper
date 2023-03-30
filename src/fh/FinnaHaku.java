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

import org.json.JSONObject;

/**
 * 
 * Class for Finna-library queries with an ISBN-code. FinnaHaku-object contains relevant information
 * about the book after the second query. 
 * 
 * Uses the FinnaParser-class for parsing JSON-objects for clarity, but this could be done with the JSON-parser
 * 
 * @author Jansromi
 * @version 30.3.2023
 *
 */
public class FinnaHaku {
	private static final String isbnUrl = "https://api.finna.fi/v1/search?lookfor=";
	private static final String finnaUrl = "https://api.finna.fi/v1/record?id=";
	
	/**
	 * Params in finnish:
	 * authors = kirjan kirjoittajat
	 * title = kirjan kokonimi
	 * publishers = julkaisija
	 * publicationDates = julkaisuvuosi
	 * classification = kirjastoluokitus
	 * subjects = tunnisteet
	 * year = julkaisuvuosi
	 * prettyPrint = JSONissa rivinvaihdot
	 */
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
	
	private String rawResponse;
	private JSONObject bookData;
	
	/**
	 * Constructor with isbn. Does a query with 
	 * given isbn and sets the FinnaID for the book.
	 * @param isbn
	 */
	public FinnaHaku(String isbn) {
		this.isbn = isbn;
		query(true);
		finnaId = FinnaParser.parseId(rawResponse);
	}
	
	/**
	 * Sets the relevant data for the book
	 */
	public void fetchBookData() {
		if (finnaId == null) {
			System.err.println("FinnaID not found! ");
			return;
		}
		query(false);
		bookData = FinnaParser.parseFirstRecord(rawResponse);
		bookTitle = FinnaParser.parseBookTitle(bookData);
		bookWriter = FinnaParser.parseWriter(bookData);
		bookSubjects = FinnaParser.parseSubjects(bookData);
		bookLanguages = FinnaParser.parseLanguage(bookData);
		
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
	public List<String> getBookWriter() {
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
	public List<String> getBookYKLClasses() {
		return bookYKLClasses;
	}
	
	/**
	 * @return the bookLanguages
	 */
	public List<String> getBookLanguages() {
		return bookLanguages;
	}

	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
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
}
