package fh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides several methods for parsing JSON data returned by the Finna API.
 * The class has no instance variables, and all methods are static, so they can be called without instantiating the class.
 * 
 * @author Jansromi
 * @version 25.4.2023
 */
public final class FinnaParser {
	
	/**
	 * Search result parser.
	 * @param content JSON search data from Finna API
	 * @return First (most relevant) search result
	 */
	public static JSONObject parseFirstRecord(String content) {
		try {
			JSONObject obj = new JSONObject(content);
			JSONObject firstRecord = obj.getJSONArray("records").getJSONObject(0);
			return firstRecord;
		} catch (org.json.JSONException e) {
			System.err.println("Record not found! " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * FinnaID parser
	 * @param obj JSON search data from Finna API
	 * @return ID of the search result
	 */
	public static String parseId(String response){
		JSONObject obj = parseFirstRecord(response);
		if (obj == null) return null;
		return obj.getString("id");
	}
	
	/**
	 * Parses the book title.
	 * @param obj JSON search data from Finna API
	 * @return The book title
	 */
	public static String parseBookTitle(JSONObject obj) {
		try {
			return obj.getString("title");
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
			JSONArray s = obj.getJSONArray("languages");
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
			writers = obj.getJSONObject("authors").getJSONObject("primary");
		} catch (JSONException e) {
			try {
				writers = obj.getJSONObject("authors").getJSONObject("secondary");
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
		JSONArray s = obj.getJSONArray("subjects");
		return getArrKeys(s);
	}
	
	/**
	 * Returns te publishers as a list.
	 * @param obj JSON search data from Finna API
	 * @return List of publishers
	 */
	public static List<String> parsePublishers(JSONObject obj){
		JSONArray s = obj.getJSONArray("publishers");
		return getArrKeys(s);
	}
	
	/**
	 * Returns the publication dates as a list
	 * @param obj JSON search data from Finna API
	 * @return List of publication dates
	 */
	public static List<String> parsePublicationDates(JSONObject obj){
		JSONArray s = obj.getJSONArray("publicationDates");
		return getArrKeys(s);
	}
	
	/**
	 * @param content JSON search data from Finna API
	 * @throws JSONException if ykl classification is not found
	 * @return
	 */
	public static List<String> parseYKL(JSONObject obj) throws JSONException {
		JSONObject classifications = obj.getJSONObject("classifications");
		JSONArray yklArray = classifications.getJSONArray("ykl");
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