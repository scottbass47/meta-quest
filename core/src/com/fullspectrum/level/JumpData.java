package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class JumpData extends LinkData{

	public final Array<Point2f> trajectory;
	public final float time;
	public final Node toNode;
	
	public JumpData(Array<Point2f> trajectory, float time, Node toNode){
		this.trajectory = trajectory;
		this.time = time;
		this.toNode = toNode;
	}
	
}
