package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class RogueComponent implements Component, Poolable{
	
	public boolean doThrowingAnim = false;
	
	@Override
	public void reset() {
		doThrowingAnim = false;
	}

}
