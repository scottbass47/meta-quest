package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class TrajectoryData extends LinkData{

	public final Array<Point2f> trajectory;
	public final float time;
	public final Node toNode;
	public final float speed;
	public final float jumpForce;
	
	public TrajectoryData(Array<Point2f> trajectory, float time, Node toNode, float speed, float jumpForce){
		this.trajectory = trajectory;
		this.time = time;
		this.toNode = toNode;
		this.speed = speed;
		this.jumpForce = jumpForce;
	}
	
}
