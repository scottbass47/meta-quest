package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.level.FlowField;

public class FlowFieldComponent implements Component, Poolable{

	public FlowField field;
	
	public FlowFieldComponent set(FlowField field){
		this.field = field;
		return this;
	}
	
	@Override
	public void reset() {
		field = null;
	}

}
