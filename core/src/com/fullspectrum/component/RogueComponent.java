package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityAnim;

public class RogueComponent implements Component, Poolable{
	
	public boolean doThrowingAnim = false;
	public float animTime = 0.0f;
	public EntityAnim animState = null;
	public float facingDelay = 1.0f;
	public float facingElapsed = 1.0f;
	public boolean canDoubleJump = true;
	
	public RogueComponent set(float facingDelay) {
		this.facingDelay = facingDelay;
		facingElapsed = facingDelay;
		return this;
	}

	@Override
	public void reset() {
		doThrowingAnim = false;
		animTime = 0.0f;
		animState = null;
		facingDelay = 0.0f;
		facingElapsed = 0.0f;
		canDoubleJump = true;
	}

}
