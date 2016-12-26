package com.fullspectrum.component;

import com.badlogic.ashley.core.Entity;

public interface TimeListener {

	/**
	 * Called after a specified amount of time has elapsed
	 * @param entity
	 */
	public void onTime(Entity entity);
	
}
