package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.input.Actions;

// IMPORTANT Add in temporary immunities that get added / removed when the ability is initialized and destroyed
public abstract class Ability {

	// Data
	private final AbilityType type;
	private TextureRegion icon;
	private float cooldown = 0.0f;
	private float elapsed = 0.0f;
	private Actions input;
	private AbilityConstraints constraints;
	
	// Flags
	private boolean done = false;
	private boolean locked = false;
	private boolean activated = true; // whether or not you have this ability selected
	protected boolean isBlocking = false; // whether or not the ability blocks
	private boolean inUse = false;
	
	public Ability(AbilityType type, TextureRegion icon, float cooldown, Actions input) {
		this(type, icon, cooldown, input, false, null);
	}
	
	public Ability(AbilityType type, TextureRegion icon, float cooldown, Actions input, boolean isBlocking) {
		this(type, icon, cooldown, input, isBlocking, null);
	}
	
	public Ability(AbilityType type, TextureRegion icon, float cooldown, Actions input, boolean isBlocking, AbilityConstraints constraints){
		this.type = type;
		this.icon = icon;
		this.cooldown = cooldown;
		this.elapsed = cooldown;
		this.input = input;
		this.isBlocking = isBlocking;
		this.constraints = constraints;
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
	
	public void activate(){
		activated = true;
	}
	
	public void deactivate(){
		activated = false;
	}
	
	public boolean isActivated() {
		return activated;
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

	public Actions getInput() {
		return input;
	}
	
	public void setInput(Actions input) {
		this.input = input;
	}
	
	public boolean inUse(){
		return inUse;
	}
	
	public boolean isBlocking(){
		return isBlocking;
	}
	
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	public boolean canUse(Entity entity){
		return !locked && (constraints == null ? true : constraints.canUse(this, entity));
	}
	
	public void setAbilityConstraints(AbilityConstraints constraints){
		this.constraints = constraints;
	}

}