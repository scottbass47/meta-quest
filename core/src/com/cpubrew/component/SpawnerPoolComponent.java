package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.entity.EntityIndex;

public class SpawnerPoolComponent implements Component, Poolable {

	private Array<SpawnItem> pool;
	private float count;
	
	public SpawnerPoolComponent(){
		pool = new Array<SpawnItem>();
	}
	
	public SpawnerPoolComponent add(EntityIndex index, float chance){
		count += chance;
		if(count > 1.0f) throw new IllegalArgumentException("Pool overflow (odds must be <= 1.0)");
		pool.add(new SpawnItem(index, chance));
		return this;
	}
	
	public Array<SpawnItem> getPool(){
		return pool;
	}
	
	@Override
	public void reset() {
		pool = null;
		count = 0.0f;
	}
	
	public static class SpawnItem{
		public EntityIndex index;
		public float chance;
		
		public SpawnItem(EntityIndex index, float chance){
			this.index = index;
			this.chance = chance;
		}
	}

}
