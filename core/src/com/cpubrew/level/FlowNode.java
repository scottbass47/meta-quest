package com.cpubrew.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FlowNode {

	public int row;
	public int col;
	public int distance = 0;
	public Array<FlowNode> adjacentNodes;
	public FlowNode parent;
	public Vector2 vec;
	
	public FlowNode(int row, int col){
		this.row = row;
		this.col = col;
		adjacentNodes = new Array<FlowNode>();
		vec = new Vector2();
	}
	
	public float getAngle(){
		return MathUtils.atan2(vec.y, vec.x);
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
		FlowNode other = (FlowNode) obj;
		if (col != other.col) return false;
		if (row != other.row) return false;
		return true;
	}



	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col + ", " + "Distance: " + distance;
	}
	
}
