package com.cpubrew.editor.mapobject.data;

import com.cpubrew.editor.mapobject.MapObjectData;
import com.cpubrew.entity.EntityIndex;

public class SpawnpointData implements MapObjectData{

	private EntityIndex index;
	
	public SpawnpointData() {
		index = null;
	}
	
	public SpawnpointData(EntityIndex index) {
		this.index = index;
	}
	
	@Override
	public MapObjectData createCopy() {
		return new SpawnpointData(index);
	}
	
	public EntityIndex getIndex() {
		return index;
	}
	
	public void setIndex(EntityIndex index) {
		this.index = index;
	}
	
}
