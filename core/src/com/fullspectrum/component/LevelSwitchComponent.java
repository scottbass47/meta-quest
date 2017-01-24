package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LevelSwitchComponent implements Component, Poolable{

	public String data;
	
	public LevelSwitchComponent set(String data){
		this.data = data;
		return this;
	}

	@Override
	public void reset() {
		data = null;
	}
}
