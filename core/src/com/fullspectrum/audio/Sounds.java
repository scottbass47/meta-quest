package com.fullspectrum.audio;

public enum Sounds {

	COIN_PICKUP("sounds/coin.wav");
	
	private String filename;
	
	private Sounds(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
}
