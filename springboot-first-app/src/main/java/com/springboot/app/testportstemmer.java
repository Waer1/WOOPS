package com.springboot.app;
import ca.rmen.porterstemmer.PorterStemmer;

public class testportstemmer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PorterStemmer stemmer = new PorterStemmer();
		String s1 = "wikipedia";
		String s2 = "hello";
		String s3 = "computer";
		String s4 = "having";
		String s5 = "traveled";
		
		System.out.println(stemmer.stemWord(s1));
		System.out.println(stemmer.stemWord(s2));
		System.out.println(stemmer.stemWord(s3));
		System.out.println(stemmer.stemWord(s4));
		System.out.println(stemmer.stemWord(s5));
		
	}

}
