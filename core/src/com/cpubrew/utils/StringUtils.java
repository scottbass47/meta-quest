package com.cpubrew.utils;

import com.badlogic.gdx.math.Vector2;

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
	
	public static String vectorToString(Vector2 vec, int decimalPlaces) {
		String xRaw = "" + vec.x;
		String x = xRaw.substring(0, Math.min(xRaw.length(), decimalPlaces == 0 ? xRaw.indexOf('.') :  xRaw.indexOf('.') + decimalPlaces + 1));
		
		String yRaw = "" + vec.y;
		String y = yRaw.substring(0, Math.min(yRaw.length(), decimalPlaces == 0 ? yRaw.indexOf('.') :  yRaw.indexOf('.') + decimalPlaces + 1));
		
		return "(" + x + ", " + y + ")";
	}
	
}
