package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class JumpLink extends NavLink{

	public final Array<Point2f> trajectory;
	public final float runSpeed;
	public final float jumpForce;
	
	public JumpLink(Node fromNode, Node toNode, float cost, Array<Point2f> trajectory, float runSpeed, float jumpForce) {
		super(LinkType.JUMP, fromNode, toNode, cost);
		this.trajectory = trajectory;
		this.runSpeed = runSpeed;
		this.jumpForce = jumpForce;
	}

	@Override
	public NavLink increaseCost(float amount) {
		JumpLink link = new JumpLink(fromNode, toNode, cost + amount, trajectory, runSpeed, jumpForce);
		link.fromLink = fromLink;
		return link;
	}
	
}
