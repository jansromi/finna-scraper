package cli;

import java.util.List;

import org.json.JSONArray;

import fh.FinnaHaku;
import fh.FinnaHaku.BookNotFoundException;

public class FinnaMain {
	
	public static void help() {
		System.out.println("-s = subjects");
		System.out.println("USAGE: <isbn> <type>");
		System.out.println("EXAMP: 9780743273565 -s");
	}
	
	/**
	 * Func for showing book subjects
	 * @param fh
	 */
	public static void subjects(FinnaHaku fh) {
		List<String> list = fh.getBookSubjects();
		for (String string : list) {
			JSONArray arr = new JSONArray(string);
			String s = arr.getString(0).replace(",", "");
			System.out.println(s);
		}
	}

	public static void main(String[] args){
		if (args[0] == "-h" || args[0] == "--help") help();
		
		if (args.length < 2) {
			System.out.println("Set isbn and type for search");
			return;
		}
		
		FinnaHaku fh = new FinnaHaku(args[0]);
		
		
		try {
			fh.fetchBookData();
		} catch (BookNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		if (args[1].equalsIgnoreCase("-s")) subjects(fh);

	}

}
