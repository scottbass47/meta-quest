package com.cpubrew.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

//CLEANUP Bad name consider making it something clearer.
public interface EntityGrabber {
	public Family componentsNeeded();
	public boolean validEntity(Entity me, Entity other);
}