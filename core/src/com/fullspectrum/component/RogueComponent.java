package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityAnim;

public class RogueComponent implements Component, Poolable{
	
	public boolean doThrowingAnim = false;
	public float animTime = 0.0f;
	public EntityAnim animState = null;
	
	@Override
	public void reset() {
		doThrowingAnim = false;
		animTime = 0.0f;
		animState = null;
	}

}
