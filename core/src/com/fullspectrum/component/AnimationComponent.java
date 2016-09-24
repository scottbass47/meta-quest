package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.fsm.State;

public class AnimationComponent implements Component{

	public ArrayMap<State, Animation> animations;
	
	public AnimationComponent(){
		animations = new ArrayMap<State, Animation>();
	}
	
	public AnimationComponent addAnimation(State key, Animation value){
		animations.put(key, value);
		return this;
	}
}
