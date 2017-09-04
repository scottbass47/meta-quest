package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BlinkComponent implements Component, Poolable{

	public Array<Blink> blinks;
	public float timeElapsed = 0.0f;
	
	public BlinkComponent(){
		blinks = new Array<Blink>();
	}
	
	public BlinkComponent addBlink(float duration, float interval){
		blinks.add(new Blink(duration, interval));
		return this;
	}
	
	@Override
	public void reset() {
		blinks = null;
		timeElapsed = 0.0f;
	}
	
	public class Blink{
		public final float duration;
		public final float interval;
		
		public Blink(float duration, float interval){
			this.duration = duration;
			this.interval = interval;
		}
	}

	
	
}
