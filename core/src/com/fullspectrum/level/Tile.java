package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class Tile {

	private final int row;
	private final int col;
	private Array<Side> sidesOpen;
	private final boolean isAir;
	
	public Tile(int row, int col, boolean isAir){
		this.row = row;
		this.col = col;
		this.isAir = isAir;
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
		if (isSurrounded() != other.isSurrounded()) return false;
		return true;
	}
	
	public boolean isSurrounded(){
		return sidesOpen.size > 0;
	}
	
	public boolean isAir(){
		return isAir;
	}
	
	public enum Side{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
}
