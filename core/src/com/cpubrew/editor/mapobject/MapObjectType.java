package com.cpubrew.editor.mapobject;

public enum MapObjectType {

	SPAWNPOINT {
		@Override
		public MapObject createDefault() {
			return MapObjectFactory.createSpawnpoint(null, null);
		}
	};
	
	public abstract MapObject createDefault();
}
