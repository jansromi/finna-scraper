package fh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FinnaParser {


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
	    JSONObject primaryWriters = obj.getJSONObject("authors").getJSONObject("primary");
	    return getObjKeys(primaryWriters);
	    
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
	 * TODO: org.json.JSONException: if ykl class is not found
	 * @param content JSON search data from Finna API
	 * @return
	 */
	public static List<String> parseYKL(JSONObject obj) {
		JSONArray s;
		try {
			s = obj.getJSONObject("classifications").getJSONArray("ykl");
			return getArrKeys(s);
		} catch (JSONException e) {
			
		}
		return null;
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
	 * Palauttaa listan alkiot
	 * @param list 
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