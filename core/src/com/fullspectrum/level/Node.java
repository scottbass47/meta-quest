package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class Node {
	
	protected int row;
	protected int col;
	
	protected Type type = Type.MIDDLE;
	private Array<NavLink> links;
	
	public Node(){
		links = new Array<NavLink>();
	}
	
	public enum Type{
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
	
	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col + ", Type: " + type;
	}
}
