package com.fullspectrum.level;

public class Tile {

	public int row;
	public int col;
	public boolean surrounded = true;
	public Side side = Side.NONE;
	
	public int getIndex(int width){
		return row * width + col;
	}
	
	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col + ", Surrounded: " + (surrounded ? "true" : "false");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		result = prime * result + (surrounded ? 1231 : 1237);
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
		if (surrounded != other.surrounded) return false;
		return true;
	}
	
	public enum Side{
		NONE,
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	
}
