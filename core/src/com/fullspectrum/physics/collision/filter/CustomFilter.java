package com.fullspectrum.physics.collision.filter;

import com.fullspectrum.physics.collision.BodyInfo;

public interface CustomFilter {

	public boolean passesFilter(BodyInfo me, BodyInfo other);
	
}
