package fh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This static class provides several methods for parsing JSON data returned by the Finna API.
 * 
 * @author Jansromi
 * @version 6.1.2024
 */
public final class FinnaParser {
	private final static String FINNA_RECORDS_ARRAY_KEY = "records";
	private final static String FINNA_RECORD_TITLE_KEY = "title";
	private final static String FINNA_RECORD_LANGUAGES_KEY = "languages";
	private final static String FINNA_RECORD_AUTHORS_KEY = "authors";
	private final static String FINNA_RECORD_PRIMARY_KEY = "primary";
	private final static String FINNA_RECORD_SECONDARY_KEY = "secondary";
	private final static String FINNA_RECORD_SUBJECTS_KEY = "subjects";
	private final static String FINNA_RECORD_PUBLISHERS_KEY = "publishers";
	private final static String FINNA_RECORD_PUBLICATION_DATES_KEY = "publicationDates";
	private final static String FINNA_RECROD_CLASSIFICATIONS_KEY = "classifications";
	private final static String FINNA_RECORD_YKL_KEY = "ykl";

	/**
	 * Parses the FinnaID from the search result.
	 * 
	 * Response from Finna API contains a list of records that match the query.
	 * The results may also contain irrelevant records or even records that do not even have the ISBN.
	 * This method returns the first record, which is (usually and hopefully!) the most relevant one.
	 * 
	 * @param response Response bodystring from Finna API-query
	 * @return FinnaID of the first search result
	 */
	public static String parseFinnaId(String response){
		JSONObject obj = parseFirstRecord(response);
		if (obj == null) return null;
		return obj.getString("id");
	}
	
	/**
	 * Search result parser.
	 * @param content JSON search data from Finna API
	 * @return First (most relevant) search result
	 */
	public static JSONObject parseFirstRecord(String content) {
		try {
			JSONObject obj = new JSONObject(content);
			JSONObject firstRecord = obj.getJSONArray(FINNA_RECORDS_ARRAY_KEY).getJSONObject(0);
			return firstRecord;
		} catch (org.json.JSONException e) {
			System.err.println("Record not found! " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Parses the book title.
	 * @param obj JSON search data from Finna API
	 * @return The book title
	 */
	public static String parseBookTitle(JSONObject obj) {
		try {
			return obj.getString(FINNA_RECORD_TITLE_KEY);
		} catch (org.json.JSONException e) {
			System.err.println("Title not found! " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Parses the language of the book
	 * @param obj JSON search data from Finna API
	 * @return List of book languages
	 */
	public static List<String> parseLanguage(JSONObject obj) {
		try {
			JSONArray s = obj.getJSONArray(FINNA_RECORD_LANGUAGES_KEY);
			return getArrKeys(s);
		} catch (org.json.JSONException e) {
			System.err.println("Languages not found!" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Parses the main writers into a list.
	 * @param obj JSON search data from Finna API
	 * @return List of writers
	 */
	public static List<String> parseWriter(JSONObject obj) {
		JSONObject writers;
		try {
			writers = obj.getJSONObject(FINNA_RECORD_AUTHORS_KEY).getJSONObject(FINNA_RECORD_PRIMARY_KEY);
		} catch (JSONException e) {
			try {
				writers = obj.getJSONObject(FINNA_RECORD_AUTHORS_KEY).getJSONObject(FINNA_RECORD_SECONDARY_KEY);
			} catch (JSONException e1) {
				return new ArrayList<String>();
			}
		}
	    return getObjKeys(writers);
	    
	}
	
	/**
	 * Returns the subject tags as a list.
	 * @param obj JSON search data from Finna API
	 * @return List of subject tags
	 */
	public static List<String> parseSubjects(JSONObject obj){
		JSONArray s = obj.getJSONArray(FINNA_RECORD_SUBJECTS_KEY);
		return getArrKeys(s);
	}
	
	/**
	 * Returns te publishers as a list.
	 * @param obj JSON search data from Finna API
	 * @return List of publishers
	 */
	public static List<String> parsePublishers(JSONObject obj){
		JSONArray s = obj.getJSONArray(FINNA_RECORD_PUBLISHERS_KEY);
		return getArrKeys(s);
	}
	
	/**
	 * Returns the publication dates as a list
	 * @param obj JSON search data from Finna API
	 * @return List of publication dates
	 */
	public static List<String> parsePublicationDates(JSONObject obj){
		JSONArray s = obj.getJSONArray(FINNA_RECORD_PUBLICATION_DATES_KEY);
		return getArrKeys(s);
	}
	
	/**
	 * @param content JSON search data from Finna API
	 * @throws JSONException if ykl classification is not found
	 * @return
	 */
	public static List<String> parseYKL(JSONObject obj) throws JSONException {
		JSONObject classifications = obj.getJSONObject(FINNA_RECROD_CLASSIFICATIONS_KEY);
		JSONArray yklArray = classifications.getJSONArray(FINNA_RECORD_YKL_KEY);
		return getArrKeys(yklArray);
	}
	
	/**
	 * Returns the keys of a JSON object as a list.
	 * Can be used, for example, to add writers to a list.
	 * @param obj
	 * @return List of keys
	 */
	public static List<String> getObjKeys(JSONObject obj){
		List<String> keysList = new ArrayList<>();
	    Iterator<String> keys = obj.keys();
	    while (keys.hasNext()) {
	        String key = keys.next();
	        keysList.add(key);
	    }
	    return keysList;
	}
	
	/**
	 * Returns the keys of a JSON array as a list.
	 * @param arr
	 * @return List of keys
	 */
	public static List<String> getArrKeys(JSONArray arr){
		List<String> keysList = new ArrayList<>();
		for (Object obj : arr) {
			keysList.add(obj.toString());
		}
		return keysList;
	}
	
	/**
	 * Returns the list as a bar-delimited string
	 * @param list Containing string values
	 * @return
	 */
	public static String listToBarString(List<String> list) {
		String result = "";
		for (String string : list) {
			result = result + string + "|";
		}
		return result;
	}

}