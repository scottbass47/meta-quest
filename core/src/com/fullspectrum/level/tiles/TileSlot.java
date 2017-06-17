package com.fullspectrum.level.tiles;

public enum TileSlot {

	SOLO,
	MIDDLE,
	RIGHT_MIDDLE,
	LEFT_MIDDLE,
	TOP_MIDDLE,
	BOTTOM_MIDDLE,
	TOP_RIGHT,
	TOP_LEFT,
	BOTTOM_RIGHT,
	BOTTOM_LEFT,
	HORIZ_MIDDLE,
	HORIZ_LEFT,
	HORIZ_RIGHT,
	VERT_MIDDLE,
	VERT_TOP,
	VERT_BOTTOM,
	INSIDE;
	
	public String toString() {
		return name().toLowerCase();
	}
	
	public static TileSlot parse(String name) {
		for(TileSlot slot : values()) {
			if(slot.name().equalsIgnoreCase(name)) return slot;
		}
		return null;
	}
	
}
