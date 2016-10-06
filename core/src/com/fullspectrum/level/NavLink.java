package com.fullspectrum.level;

public class NavLink {

	public final Type type;
	public final Node toNode;
	
	public NavLink(Type type, Node toNode){
		this.type = type;
		this.toNode = toNode;
	}
	
	public enum Type {
		JUMP,
		FALL,
		RUN
	}
}
