package com.cpubrew.editor.mapobject;

import com.cpubrew.editor.mapobject.data.SpawnpointData;

public enum MapObjectType {

	SPAWNPOINT {
		@Override
		public MapObject createDefault(MapObjectData data) {
			return MapObjectFactory.createSpawnpoint(null, (SpawnpointData)data);
		}
	};
	
	public abstract MapObject createDefault(MapObjectData data);
}
