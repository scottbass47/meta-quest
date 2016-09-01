package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.ArrayMap;

public class AnimationComponent implements Component{

	public ArrayMap<IAnimState, Animation> animations;
	
	public AnimationComponent(){
		animations = new ArrayMap<IAnimState, Animation>();
	}
	
	public AnimationComponent addAnimation(IAnimState key, Animation value){
		animations.put(key, value);
		return this;
	}
}
