package com.fullspectrum.physics;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.ArrayMap;

public class EntityFixtures {

	private ArrayMap<FixtureDef, Object> fixtures;
	private String name;
	
	public EntityFixtures(){
		fixtures = new ArrayMap<FixtureDef, Object>();
	}
	
	public void add(FixtureDef fdef, Object data){
		fixtures.put(fdef, data);
	}
	
	public ArrayMap<FixtureDef, Object> getFixtures(){
		return fixtures;
	}
	
	public Object getData(FixtureDef fdef){
		return fixtures.get(fdef);
	}
	
	public void setName(String name){
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EntityFixtures other = (EntityFixtures) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		return true;
	}
	
	
	
}
