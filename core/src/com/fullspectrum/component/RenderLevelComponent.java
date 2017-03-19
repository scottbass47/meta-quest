package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RenderLevelComponent implements Component, Poolable{

	public int renderLevel;

	public RenderLevelComponent set(int renderLevel){
		this.renderLevel = renderLevel;
		return this;
	}
	
	@Override
	public void reset() {
		renderLevel = 0;
	}
}