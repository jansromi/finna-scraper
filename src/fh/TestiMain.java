package fh;

import java.util.List;

import fh.FinnaHaku.BookNotFoundException;

public class TestiMain {
	
	public static void main(String[] args) throws BookNotFoundException {
		FinnaHaku fh = new FinnaHaku("9789524951272");
		fh.fetchBookData();
		List<String> nimet = fh.getBookWriters();
		for (String string : nimet) {
			System.out.println(string);
		}
	}
}
