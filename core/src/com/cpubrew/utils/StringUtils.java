package com.cpubrew.utils;

public class StringUtils {

	public static String toTitleCase(String input) {
	    StringBuilder titleCase = new StringBuilder();
	    boolean nextTitleCase = true;

	    for (char c : input.toCharArray()) {
	        if (Character.isSpaceChar(c)) {
	            nextTitleCase = true;
	        } else if (nextTitleCase) {
	            c = Character.toTitleCase(c);
	            nextTitleCase = false;
	        }else{
	        	c = Character.toLowerCase(c);
	        }
	        titleCase.append(c);
	    }
	    return titleCase.toString();
	}
	
}
