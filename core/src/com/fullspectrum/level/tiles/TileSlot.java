package com.fullspectrum.level.tiles;

import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.level.tiles.MapTile.Side;
import static com.fullspectrum.level.tiles.MapTile.Side.*;

public enum TileSlot {

	SOLO (EAST, NORTH, SOUTH, WEST),
	MIDDLE (),
	RIGHT_MIDDLE (EAST),
	LEFT_MIDDLE (WEST),
	TOP_MIDDLE (NORTH),
	BOTTOM_MIDDLE (SOUTH),
	TOP_RIGHT (NORTH, EAST),
	TOP_LEFT (NORTH, WEST),
	BOTTOM_RIGHT (SOUTH, EAST),
	BOTTOM_LEFT (SOUTH, WEST),
	HORIZ_MIDDLE (NORTH, SOUTH),
	HORIZ_LEFT (NORTH, SOUTH, WEST),
	HORIZ_RIGHT (NORTH, SOUTH, EAST),
	VERT_MIDDLE (EAST, WEST),
	VERT_TOP (NORTH, EAST, WEST),
	VERT_BOTTOM (SOUTH, EAST, WEST),
	INSIDE;
	
	private ObjectSet<Side> sidesOpen;
	
	private TileSlot(Side... sides) {
		sidesOpen = new ObjectSet<Side>();
		sidesOpen.addAll(sides);
	}
	
	public String toString() {
		return name().toLowerCase();
	}
	
	public static TileSlot parse(String name) {
		for(TileSlot slot : values()) {
			if(slot.name().equalsIgnoreCase(name)) return slot;
		}
		return null;
	}
	
	public static TileSlot getSlot(Side... openSides) {
		for(TileSlot slot : values()) {
			if(slot == TileSlot.INSIDE) continue;
			if(slot.sidesOpen.size != openSides.length) continue;
			
			boolean matching = true;
			for(Side side : openSides) {
				if(!slot.sidesOpen.contains(side)){
					matching = false;
					break;
				}
			}
			
			if(matching) return slot;
		}
		return null;
	}
}
