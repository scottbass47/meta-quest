package com.fullspectrum.input;

public enum XboxOneButtons {

	A,
	B,
	X,
	Y,
	LEFT_BUMPER,
	RIGHT_BUMPER,
	BACK,
	START,
	LEFT_STICK,
	RIGHT_STICK;
	
	
	public String getButtonName(){
		return name();
	}
	
	public int getCode(){
		return ordinal();
	}
	
	public static int getCode(String buttonName){
		for(XboxOneButtons b : XboxOneButtons.values()){
			if(buttonName.equals(b.getButtonName())){
				return b.getCode();
			}
		}
		return -1;
	}
}
