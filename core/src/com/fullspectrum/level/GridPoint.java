package com.fullspectrum.level;

public class GridPoint {

	public int row;
	public int col;
	
	public GridPoint() {
	}
	
	public GridPoint(GridPoint point) {
		row = point.row;
		col = point.col;
	}
	
	public GridPoint(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public void set(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public void set(GridPoint point) {
		row = point.row;
		col = point.col;
	}
	
	@Override
	public String toString() {
		return "(" + row + ", " + col + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GridPoint other = (GridPoint) obj;
		if (col != other.col) return false;
		if (row != other.row) return false;
		return true;
	}
	
}
