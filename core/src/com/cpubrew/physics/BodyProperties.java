package com.cpubrew.physics;

public class BodyProperties {

	private float gravityScale = 1.0f;
	private boolean sleepingAllowed = true;
	private boolean active = true;
	
	public float getGravityScale(){
		return gravityScale;
	}
	
	public boolean isSleepingAllowed(){
		return sleepingAllowed;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public static class Builder{
		
		private BodyProperties properties;
		
		public Builder(){
			properties = new BodyProperties();
		}
		
		public Builder setGravityScale(float gravityScale){
			properties.gravityScale = gravityScale;
			return this;
		}
		
		public Builder setSleepingAllowed(boolean sleepingAllowed){
			properties.sleepingAllowed = sleepingAllowed;
			return this;
		}
		
		public Builder setActive(boolean active){
			properties.active = active;
			return this;
		}
		
		public BodyProperties build(){
			return properties;
		}
	}
}
