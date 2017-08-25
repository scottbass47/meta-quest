package com.fullspectrum.audio;

public enum Sounds {

	COIN_PICKUP ("coin.wav"),
	TRIP        ("grunt_gremlin_trip.mp3"),
	DYING		("grunt_gremlin_death.mp3"),
	DAMAGE		("grunt_gremlin_damage.mp3");
	
	
	private String filename;
	
	private Sounds(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
}
