package cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import fh.FinnaHaku;
import fh.FinnaHaku.BookNotFoundException;
import cli.services.*;

public class FinnaMain {
	static int bookId = 1;
	
	public static void help() {
		System.out.println("-s = subjects");
		System.out.println("USAGE: <isbn> <type>");
		System.out.println("EXAMP: 9780743273565 -s");
	}
	

	public static void main(String[] args){
		File file = new File("input.txt");
	    try (Scanner scanner = new Scanner(file)) {
	        while (scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            FinnaHaku fh = new FinnaHaku(line);
	            try {
					fh.fetchBookData();
				} catch (BookNotFoundException e) {
					System.err.println("Book with isbn: " + line + " was not found");
					e.printStackTrace();
					continue;
				}
	            DataProcessor.processBookEntry(fh, bookId);
	            bookId++;
	        }
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}

}
