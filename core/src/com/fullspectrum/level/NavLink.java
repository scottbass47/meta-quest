package com.fullspectrum.level;

public class NavLink {

	public final LinkType type;
	public final Node fromNode;
	public final Node toNode;
	public final float cost;
	protected NavLink fromLink;
	
	public NavLink(LinkType type, Node fromNode, Node toNode, float cost){
		this.type = type;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.cost = cost;
	}
	
	public NavLink increaseCost(float amount){
		NavLink link = new NavLink(type, fromNode, toNode, cost + amount);
		link.fromLink = fromLink;
		return link;
	}
	
	public enum LinkType {
		JUMP,
		FALL,
		RUN
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
