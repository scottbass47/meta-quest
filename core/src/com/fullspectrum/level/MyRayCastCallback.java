package com.fullspectrum.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.fullspectrum.physics.FixtureType;

public class MyRayCastCallback implements RayCastCallback {

	private boolean hitWall = false;

	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		FixtureType type = (FixtureType) fixture.getUserData();
		if (type == FixtureType.GROUND) {
			hitWall = true;
			return 0;
		}
		return -1;
	}

	public boolean hitWall() {
		return hitWall;
	}
}
