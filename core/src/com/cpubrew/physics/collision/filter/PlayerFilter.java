package com.cpubrew.physics.collision.filter;

import com.cpubrew.component.Mappers;
import com.cpubrew.physics.collision.BodyInfo;

public class PlayerFilter implements CustomFilter{

	@Override
	public boolean passesFilter(BodyInfo me, BodyInfo other) {
		return Mappers.player.get(other.getEntity()) != null;
	}

}
