package com.cpubrew.level;

import com.cpubrew.utils.Maths;

/**
 * Represents the span of air tiles that sit upon a flat, solid platform.
 * 
 * @author Scott
 */
public class Platform {

	// Start and end cols are inclusive
	private int startCol;
	private int endCol;
	private int row;
	
	public int getStartCol() {
		return startCol;
	}
	
	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}
	
	public int getEndCol() {
		return endCol;
	}
	
	public void setEndCol(int endCol) {
		this.endCol = endCol;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public boolean contains(float x, float y) {
		return contains(Maths.toGridCoord(y), Maths.toGridCoord(x));
	}
	
	public boolean contains(int row, int col) {
		return row == this.row && col >= startCol && col <= endCol;
	}
	
	@Override
	public String toString() {
		return "Col " + startCol + " to " + endCol + " @ Row " + row;
	}
	
}
