package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.DropType;

public class DropTypeComponent implements Component, Poolable{

	public DropType type;

	public DropTypeComponent set(DropType type){
		this.type = type;
		return this;
	}
	
	@Override
	public void reset() {
		type = null;
	}
}
