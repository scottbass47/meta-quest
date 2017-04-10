package com.fullspectrum.movement;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;

public interface Movement {
	
	public Vector2 getVelocity(Entity entity, float elapsed, float delta);

}