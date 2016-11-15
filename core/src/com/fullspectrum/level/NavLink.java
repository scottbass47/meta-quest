package com.fullspectrum.level;

public class NavLink {

	public final LinkType type;
	public final Node fromNode;
	public final Node toNode;
	public final float cost;
	public final LinkData data;
	protected NavLink fromLink;
	
	public NavLink(LinkType type, LinkData data, Node fromNode, Node toNode, float cost){
		this.type = type;
		this.data = data;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.cost = cost;
	}
	
	public NavLink increaseCost(float amount){
		NavLink link = new NavLink(type, data, fromNode, toNode, cost + amount);
		link.fromLink = fromLink;
		return link;
	}
	
	public enum LinkType {
		JUMP,
		JUMP_OVER,
		FALL,
		FALL_OVER,
		RUN,
		CLIMB
	}
	
	public boolean isDirRight(){
		return fromNode.col < toNode.col;
	}
	
	@Override
	public String toString() {
		return "Type: " + type + ", Cost: " + cost;
	}

	public NavLink getParent() {
		return fromLink;
	}

	public void setParent(NavLink fromLink) {
		this.fromLink = fromLink;
	}

}
