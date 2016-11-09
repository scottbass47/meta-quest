package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityManager;

public class SwordComponent implements Component, Poolable{

	public Entity sword;
	
	public SwordComponent set(Entity sword){
		this.sword = sword;
		return this;
	}

	@Override
	public void reset(){
		EntityManager.cleanUp(sword);
	}
	
}
