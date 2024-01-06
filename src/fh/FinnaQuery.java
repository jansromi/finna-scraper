package fh;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONObject;

/**
 * This class a provides a static method for querying the Finna API.
 * 
 * Its only public method fetchRecord() takes an ISBN as a parameter and returns a FinnaRecord object,
 * which contains the relevant information about the record.
 */
public class FinnaQuery {
    private static final String FINNA_ISBN_URL = "https://api.finna.fi/v1/search?lookfor=";
    private static final String FINNA_ID_URL = "https://api.finna.fi/v1/record?id=";
	
    private static final String[] DEFAULT_FIELDS = {
        "authors", "title", "publishers", "publicationDates",
        "classifications", "subjects", "year", "languages", "summary"
    };

    public static FinnaRecord fetchRecord(String isbn) {
        String records = queryWithIsbn(isbn);
        String finnaId = FinnaParser.parseFinnaId(records);
        String record = queryWithFinnaId(finnaId);
        JSONObject obj = FinnaParser.parseFirstRecord(record);
        if (obj != null) {
            return new FinnaRecord(obj, isbn, finnaId);
        }

        return null;
    }

    /**
     * Queries Finna API with the given ISBN and returns all the records that matched the query.
     * 
     * This will return a list of records that Finna-librares have available.
     * To get the relevant information, API needs to be queried again with the Finna-record id.
     * 
     * @param isbn of the record
     * @return FinnaID of the record
     */
    private static String queryWithIsbn(String isbn) {
        String url = FINNA_ISBN_URL + isbn;
        return query(url);
    }

	/**
	 * Queries Finna API with the given FinnaID.
	 * 
	 * This will return a record with relevant information.
	 * 
	 * @param finnaId
	 * @return Response body as a string
	 */
    private static String queryWithFinnaId(String finnaId) {
        String url = FINNA_ID_URL + finnaId + buildFinnaParams();
        return query(url);
    }

	/**
	 * Query an url and return the response body as a string.
	 * Method is used to abstract away the HTTP request and catching exceptions.
	 * @param url to be queried
	 * @return response body as a string or null if an error occurs
	 */
    private static String query(String url) {
        try {
            String response = sendRequest(url);
            return response;
        } catch (IOException | InterruptedException e) {
            System.err.println("Error while querying Finna API with URL: " + url);
            System.err.println(e.getMessage());
            e.printStackTrace();
			return null;
        }
    }

    /**
     * Send a HTTP GET request to the given URL and return the response body as a string.
     * 
     * @param url
     * @return response body as a string
     * @throws IOException if an I/O error occurs while sending the request
     * @throws InterruptedException if the request is interrupted
     */
    private static String sendRequest(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        return response.body();
    }

    private static String buildFinnaParams() {
        return buildFinnaParams(DEFAULT_FIELDS);
    }

	/**
	 * Builds the Finna API query parameters from the given fields.
	 * @param fields Parameters to be included in the query
	 * @return query string for Finna API
	 */
    private static String buildFinnaParams(String[] fields) {
        if (fields == null || fields.length == 0) {
            fields = DEFAULT_FIELDS;
        }

        StringBuilder builder = new StringBuilder();
        for (String field : fields) {
            builder.append("&field[]=");
            builder.append(field);
        }

        builder.append("&prettyPrint=0");

        return builder.toString();
    }
}
