package com.fullspectrum.physics.collision.behavior;

public class SensorBehavior extends CollisionBehavior{

	public SensorBehavior() {
		preSolveType = PreSolveType.DISABLE_CONTACT;
	}
	
}
