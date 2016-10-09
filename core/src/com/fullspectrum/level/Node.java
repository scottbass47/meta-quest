package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class Node {
	
	protected int row;
	protected int col;
	
	public NodeType type = NodeType.MIDDLE;
	private Array<NavLink> links;
	
	public Node(){
		links = new Array<NavLink>();
	}
	
	public enum NodeType{
		LEFT_EDGE,
		RIGHT_EDGE,
		SOLO,
		MIDDLE
	}
	
	public void addLink(NavLink link){
		links.add(link);
	}
	
	public Array<NavLink> getLinks(){
		return links;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col + ", Type: " + type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Node other = (Node) obj;
		if (col != other.col) return false;
		if (row != other.row) return false;
		if (type != other.type) return false;
		return true;
	}

	
}
