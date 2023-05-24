package fh;

import java.util.List;

import fh.FinnaHaku.BookNotFoundException;

public class TestiMain {
	
	public static void main(String[] args) throws BookNotFoundException {
		FinnaHaku fh = new FinnaHaku("9789526335728");
		fh.fetchBookData();
		System.out.println(fh.getBookTitle());
		/*
		List<String> nimet = fh.getBookWriters();
		for (String string : nimet) {
			System.out.println(string);
		}
		*/
	}
}
