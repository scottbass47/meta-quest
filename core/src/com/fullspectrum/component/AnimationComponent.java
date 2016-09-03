package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.ArrayMap;

public class AnimationComponent implements Component{

	public ArrayMap<AnimState, Animation> animations;
	
	public AnimationComponent(){
		animations = new ArrayMap<AnimState, Animation>();
	}
	
	public AnimationComponent addAnimation(AnimState key, Animation value){
		animations.put(key, value);
		return this;
	}
}
