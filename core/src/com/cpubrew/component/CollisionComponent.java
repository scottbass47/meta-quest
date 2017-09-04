package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class CollisionComponent implements Component, Poolable {

	public int bottomContacts = 0;
	public int topContacts = 0;
	public int rightContacts = 0;
	public int leftContacts = 0;
	public int ladderContacts = 0;

	public boolean onGround() {
		return bottomContacts > 0;
	}

	public boolean hittingCeiling() {
		return topContacts > 0;
	}

	public boolean onRightWall() {
		return rightContacts > 0;
	}

	public boolean onLeftWall() {
		return leftContacts > 0;
	}
	
	public boolean onLadder(){
		return ladderContacts > 0;
	}

	@Override
	public void reset() {
		bottomContacts = 0;
		topContacts = 0;
		rightContacts = 0;
		leftContacts = 0;
		ladderContacts = 0;
	}
}
