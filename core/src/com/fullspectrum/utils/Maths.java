package com.fullspectrum.utils;

public class Maths {

	private Maths() {}
	
	public static float getOverflow(float amount, float max){
		if(amount <= max) return 0.0f;
		return amount - max;
	}
	
}
