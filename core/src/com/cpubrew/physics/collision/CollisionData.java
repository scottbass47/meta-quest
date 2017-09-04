package com.cpubrew.physics.collision;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.physics.FixtureType;

public class CollisionData {

	private ArrayMap<FixtureType, FixtureInfo> fixtureMap;

	public CollisionData() {
		fixtureMap = new ArrayMap<FixtureType, FixtureInfo>();
	}

	public void registerDefault(FixtureType type, Entity entity) {
		fixtureMap.put(type, type.getDefaultInfo(entity));
	}

	public void setFixtureInfo(FixtureType type, FixtureInfo info){
		fixtureMap.put(type, info);
	}
	
	public FixtureInfo getFixtureInfo(FixtureType type) {
		return fixtureMap.get(type);
	}
}