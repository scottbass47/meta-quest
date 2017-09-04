package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class ChildrenComponent implements Component, Poolable{

	public Array<Entity> children;
	
	public ChildrenComponent() {
		children = new Array<Entity>();
	}
	
	public ChildrenComponent add(Entity child){
		children.add(child);
		return this;
	}
	
	public Entity remove(Entity child){
		return children.removeIndex(children.indexOf(child, false));
	}
	
	public Array<Entity> getChildren(){
		return children;
	}
	
	@Override
	public void reset() {
		children = null;
	}
}
