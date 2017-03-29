package com.fullspectrum.physics.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Fixture;

public class CollisionInfo {

	private Entity me;
	private Entity other;
	private Fixture myFixture;
	private Fixture otherFixture;
	private CollisionBodyType otherCollisionType;
	
	public CollisionInfo(Entity me, Entity other, Fixture myFixture, Fixture otherFixture, CollisionBodyType otherCollisionType) {
		this.me = me;
		this.other = other;
		this.myFixture = myFixture;
		this.otherFixture = otherFixture;
		this.otherCollisionType = otherCollisionType;
	}

	public Entity getMe() {
		return me;
	}

	public Entity getOther() {
		return other;
	}

	public Fixture getMyFixture() {
		return myFixture;
	}

	public Fixture getOtherFixture() {
		return otherFixture;
	}
	
	public CollisionBodyType getOtherCollisionType() {
		return otherCollisionType;
	}
}
