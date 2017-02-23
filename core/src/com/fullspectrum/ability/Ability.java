package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.fsm.transition.InputTransitionData;

public abstract class Ability {

	// Data
	private final AbilityType type;
	private TextureRegion icon;
	private float cooldown = 0.0f;
	private float elapsed = 0.0f;
	private InputTransitionData inputData;
	
	// Flags
	private boolean done = false;
	private boolean locked = false;
	
	public Ability(AbilityType type, TextureRegion icon, float cooldown, InputTransitionData inputData) {
		this.type = type;
		this.icon = icon;
		this.cooldown = cooldown;
		this.inputData = inputData;
	}
	
	/** Called once when ability is first used */
	public abstract void init(Entity entity);
	
	/** Called once every update cycle as long as ability is active */
	public abstract void update(Entity entity, float delta);
	
	/** Called once the ability is finished */
	public abstract void destroy(Entity entity);
	
	public AbilityType getType(){
		return type;
	}
	
	public boolean isReady(){
		return elapsed >= cooldown;
	}
	
	public void lock(){
		locked = true;
	}
	
	public void unlock(){
		locked = false;
	}
	
	public boolean isLocked(){
		return locked;
	}
	
	public void setDone(boolean done){
		this.done = done;
	}
	
	public boolean isDone(){
		return done;
	}
	
	public void addTime(float time){
		this.elapsed += time;
	}
	
	public void setTimeElapsed(float elapsed){
		this.elapsed = elapsed;
	}
	
	public void resetTimeElapsed(){
		elapsed = 0.0f;
	}
	
	public float getTimeElapsed(){
		return elapsed;
	}
	
	public float getCooldown(){
		return cooldown;
	}
	
	public TextureRegion getIcon(){
		return icon;
	}

	public InputTransitionData getInputData() {
		return inputData;
	}

}