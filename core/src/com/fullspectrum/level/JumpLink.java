package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;

public class JumpLink extends NavLink{

	public final Array<Point2f> trajectory;
	public final float runMultiplier;
	public final float jumpMultiplier;
	
	public JumpLink(Node fromNode, Node toNode, float cost, Array<Point2f> trajectory, float runMultiplier, float jumpMultiplier) {
		super(LinkType.JUMP, fromNode, toNode, cost);
		this.trajectory = trajectory;
		this.runMultiplier = runMultiplier;
		this.jumpMultiplier = jumpMultiplier;
	}

	@Override
	public NavLink increaseCost(float amount) {
		JumpLink link = new JumpLink(fromNode, toNode, cost + amount, trajectory, runMultiplier, jumpMultiplier);
		link.fromLink = fromLink;
		return link;
	}
	
}
