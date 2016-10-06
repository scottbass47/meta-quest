package com.fullspectrum.level;

public class Node {
	
	protected int row;
	protected int col;
	
	protected Type type = Type.MIDDLE;
	
	public Node(){
		
	}
	
	public enum Type{
		LEFT_EDGE,
		RIGHT_EDGE,
		SOLO,
		MIDDLE
	}

	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col + ", Type: " + type;
	}
}
