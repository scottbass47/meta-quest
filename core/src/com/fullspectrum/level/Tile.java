package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class Tile {

	private final int row;
	private final int col;
	private Array<Side> sidesOpen;
	private final TileType type;
	
	public Tile(int row, int col, TileType type){
		this.row = row;
		this.col = col;
		this.type = type;
		sidesOpen = new Array<Side>();
	}
	
	public int getRow() {
		return row;
	}

	public int getCol(){
		return col;
	}
	
	public int getIndex(int width){
		return row * width + col;
	}
	
	public void addSide(Side side){
		if(sidesOpen.contains(side, false)) return;
		sidesOpen.add(side);
	}
	
	public void removeSide(Side side){
		if(!sidesOpen.contains(side, false)) return;
		sidesOpen.removeValue(side, false);
	}
	
	public boolean isOpen(Side side){
		return sidesOpen.contains(side, false);
	}
	
	public TileType getType(){
		return type;
	}
	
	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col + ", Surrounded: " + (isSurrounded() ? "true" : "false");
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		result = prime * result + (isSurrounded() ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.name().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Tile other = (Tile) obj;
		if (col != other.col) return false;
		if (row != other.row) return false;
		if (sidesOpen == null) {
			if (other.sidesOpen != null) return false;
		}
		else if (!sidesOpen.equals(other.sidesOpen)) return false;
		if (type != other.type) return false;
		return true;
	}

	public boolean isSurrounded(){
		return sidesOpen.size > 0;
	}
	
	public boolean isSolid(){
		return type.isSolid;
	}
	
	public enum Side{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	public enum TileType{
		AIR(false),
		GROUND(true),
		LADDER(false),
		DECOR(false);
		
		private final boolean isSolid;

		private TileType(boolean isSolid){
			this.isSolid = isSolid;
		}
		
		public boolean isSolid(){
			return isSolid;
		}
		
		public static TileType getType(String name){
			for(TileType type : values()){
				if(type.name().equalsIgnoreCase(name)) return type;
			}
			return null;
		}
	}
}
