package com.cpubrew.ai;

import com.cpubrew.level.NavLink;

public class PathData {

	private NavLink fromLink;
	private float cost;
	
	public PathData(){
		this(null, Float.MAX_VALUE);
	}
	
	public PathData(NavLink fromLink, float cost){
		this.setFromLink(fromLink);
		this.setCost(cost);
	}
	
	public void reset(){
		this.fromLink = null;
		this.cost = Float.MAX_VALUE;
	}

	public NavLink getFromLink() {
		return fromLink;
	}

	public void setFromLink(NavLink fromLink) {
		this.fromLink = fromLink;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		return fromLink + " - Cost: " + cost;
	}
	
}
