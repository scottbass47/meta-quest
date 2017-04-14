package com.fullspectrum.physics.collision.filter;

import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.collision.BodyInfo;

public class PlayerFilter implements CustomFilter{

	@Override
	public boolean passesFilter(BodyInfo me, BodyInfo other) {
		return Mappers.player.get(other.getEntity()) != null;
	}

}
