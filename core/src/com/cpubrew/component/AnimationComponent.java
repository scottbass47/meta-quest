package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.fsm.State;

public class AnimationComponent implements Component, Poolable{

	public ArrayMap<State, Animation<TextureRegion>> animations = new ArrayMap<State, Animation<TextureRegion>>();
	
	public AnimationComponent addAnimation(State key, Animation<TextureRegion> value){
		animations.put(key, value);
		return this;
	}

	@Override
	public void reset() {
		animations = new ArrayMap<State, Animation<TextureRegion>>();
	}
}
