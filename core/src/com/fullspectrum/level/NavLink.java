package com.fullspectrum.level;

public class NavLink {

	public final LinkType type;
	public final Node toNode;
	public final float cost;
	
	public NavLink(LinkType type, Node toNode, float cost){
		this.type = type;
		this.toNode = toNode;
		this.cost = cost;
	}
	
	public enum LinkType {
		JUMP,
		FALL,
		RUN
	}
}
