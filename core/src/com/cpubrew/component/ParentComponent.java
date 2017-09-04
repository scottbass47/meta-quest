package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ParentComponent implements Component, Poolable{

	public Entity parent;
	
	public ParentComponent set(Entity parent){
		this.parent = parent;
		return this;
	}
	
	@Override
	public void reset(){
		parent = null;
	}
	
}
