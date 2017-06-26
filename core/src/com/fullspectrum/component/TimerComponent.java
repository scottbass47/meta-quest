package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TimerComponent implements Component, Poolable{

	public ArrayMap<String, Timer> timers;
	
	public TimerComponent(){
		timers = new ArrayMap<String, Timer>();
	}
	
	public TimerComponent add(String name, float time, boolean looping, TimeListener listener){
		timers.put(name.toLowerCase(), new Timer(time, looping, listener));
		return this;
	}
	
	public Timer get(String name){
		return timers.get(name.toLowerCase());
	}
	
	@Override
	public void reset() {
		timers = null;
	}
	
	public void remove(String name){
		timers.removeKey(name.toLowerCase());
	}
	
	public static class Timer{
		private float time;
		private boolean looping;
		private float elapsed = 0.0f;
		private TimeListener listener;
		private boolean paused = false;
		
		public Timer(float time, boolean looping, TimeListener listener){
			this.time = time;
			this.looping = looping;
			this.listener = listener;
		}
		
		public boolean isDone(){
			return elapsed >= time;
		}
		
		public void pause(){
			paused = true;
		}
		
		public void unpause(){
			paused = false;
		}
		
		public boolean isPaused(){
			return paused;
		}
		
		/** DOES NOT SET ELAPSED TO 0. This method effectively does this: elapsed = elapsed - time */
		public void resetElapsed(){
			elapsed -= time;
		}
		
		public void addTime(float amount){
			elapsed += amount;
		}
		
		public void setElapsed(float time){
			this.elapsed = time;
		}
		
		public boolean isLooping(){
			return looping;
		}
		
		public void onTime(Entity entity){
			listener.onTime(entity);
		}
		
		public float getElapsed(){
			return elapsed;
		}
		
		public float getTotalTime(){
			return time;
		}
	}
	
}
