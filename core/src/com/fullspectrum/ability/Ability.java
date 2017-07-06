package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.component.ImmuneComponent;
import com.fullspectrum.component.InvincibilityComponent;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.input.Actions;

public abstract class Ability {

	// Data
	private final AbilityType type;
	private TextureRegion icon;
	private float cooldown = 0.0f;
	private float elapsed = 0.0f;
	private Actions input;
	private AbilityConstraints constraints;
	private ObjectSet<EffectType> tempImmunities;
	private ObjectSet<InvincibilityType> tempInvincibilities;
	
	// Flags
	private boolean done = false;
	private boolean locked = false;
	private boolean activated = true; // whether or not you have this ability selected
	protected boolean isBlocking = false; // whether or not the ability blocks
	private boolean inUse = false;
	private boolean lockFacing;
	
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
		tempImmunities = new ObjectSet<EffectType>();
		tempInvincibilities = new ObjectSet<InvincibilityType>();
	}
	
	public final void initAbility(Entity entity){
		// Init Immunities
		ImmuneComponent immuneComp = Mappers.immune.get(entity);
		immuneComp.addAll(tempImmunities);
		
		// Init Invincibilities
		InvincibilityComponent invincibilityComp = Mappers.inviciblity.get(entity);
		invincibilityComp.addAll(tempInvincibilities);
		
		// Lock Facing
		if(lockFacing) Mappers.facing.get(entity).locked = true;
		
		init(entity);
	}
	
	public final void updateAbility(Entity entity, float delta){
		update(entity, delta);
	}
	
	public final void destroyAbility(Entity entity){
		// Restore Immunities
		Mappers.immune.get(entity).removeAll(tempImmunities);
		Mappers.inviciblity.get(entity).removeAll(tempInvincibilities);
		
		// Unlock Facing
		Mappers.facing.get(entity).locked = false;
		
		destroy(entity);
	}
	
	/** Called once when ability is first used */
	protected abstract void init(Entity entity);
	
	/** Called once every update cycle as long as ability is active */
	protected abstract void update(Entity entity, float delta);
	
	/** Called once the ability is finished */
	protected abstract void destroy(Entity entity);
	
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
	
	protected void lockFacing(){
		lockFacing = true;
	}
	
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
	
	/** 
	 * Default behavior is to unblock other abilities onDestroy if this ability is blocking.
	 * Override this method if you already unblock before destroying.
	 * @return
	 */
	public boolean unblockOnDestroy(){
		return isBlocking;
	}
	
	public boolean canUse(Entity entity){
		return !locked && (constraints == null ? true : constraints.canUse(this, entity));
	}
	
	public void setAbilityConstraints(AbilityConstraints constraints){
		this.constraints = constraints;
	}
	
	public void addTemporaryImmunties(EffectType... types){
		tempImmunities.addAll(types);
	}
	
	public void addTemporaryInvincibilities(InvincibilityType... types){
		tempInvincibilities.addAll(types);
	}
	
}