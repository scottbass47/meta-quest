package com.cpubrew.physics.collision.filter;

import com.cpubrew.physics.collision.BodyInfo;

public interface CustomFilter {

	public boolean passesFilter(BodyInfo me, BodyInfo other);
	
}
