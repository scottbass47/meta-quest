package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.entity.EntityManager;

public class WingComponent implements Component, Poolable{

	public Entity wings;
	
	public WingComponent set(Entity wings){
		this.wings = wings;
		return this;
	}
	
	@Override
	public void reset() {
		if(wings == null) return;
		EntityManager.cleanUp(wings);
		wings = null;
	}

}
