package com.fullspectrum.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.ability.Ability;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.effects.StunDef;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.input.Actions;

public class ParryAbility extends Ability{

	private float elapsed;
	private float maxTime;
	private boolean blocking = true;
	private float stunDuration = 0.0f;
	private boolean hasBlocked = false;
	private Entity entity;
	private float swingDuration = 0.0f;
	private SwingComponent swing;
	private boolean hadInv = false;
	
	public ParryAbility(float cooldown, Actions input, float maxTime, float stunDuration, Animation<TextureRegion> parrySwingAnimation, SwingComponent swing) {
		super(AbilityType.PARRY, AssetLoader.getInstance().getRegion(Asset.PARRY_ICON), cooldown, input, true);
		this.maxTime = maxTime;
		this.stunDuration = stunDuration;
		this.swingDuration = parrySwingAnimation.getAnimationDuration();
		this.swing = swing;
		swing.addEffect(new StunDef(stunDuration));
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryImmunties(EffectType.KNOCKBACK, EffectType.STUN);
		lockFacing();
	}

	@Override
	public void init(Entity entity) {
		this.entity = entity;
		Mappers.esm.get(entity).get(EntityStates.PARRY_BLOCK).changeState(EntityStates.PARRY_BLOCK);
	}

	@Override
	public void update(Entity entity, float delta) {
		elapsed += delta;
		
		// If nothing was blocked or the swing has ended, return entity to idle state
		if((elapsed >= maxTime && !hasBlocked) || (elapsed >= swingDuration & hasBlocked)){
			setDone(true);
			Mappers.esm.get(entity).get(EntityStates.PARRY_BLOCK).changeState(EntityStates.IDLING);
		}
	}
	
	public void parry(Entity blockedEntity){
		Body myBody = Mappers.body.get(entity).body;
		Body otherBody = Mappers.body.get(blockedEntity).body;
		
		Mappers.facing.get(entity).facingRight = otherBody.getPosition().x >= myBody.getPosition().x;
		
		if(Mappers.projectile.get(blockedEntity) != null) {
			Mappers.death.get(blockedEntity).triggerDeath();
		} else {
			Effects.giveStun(blockedEntity, stunDuration);
		}
		
		SwingComponent swingComp = Mappers.engine.get(entity).engine.createComponent(SwingComponent.class);
		swingComp.set(swing.rx, swing.ry, swing.startAngle, swing.endAngle, swing.delay, swing.damage, swing.knockback).setEffects(swing.effects);
		swingComp.shouldSwing = true;
		entity.add(swingComp);
		
		Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
		hadInv = true;
		
		elapsed = 0.0f;
		blocking = false;
		hasBlocked = true;
		Mappers.esm.get(entity).get(EntityStates.PARRY_SWING).changeState(EntityStates.PARRY_SWING);
	}
	
	public boolean readyToBlock(){
		return blocking;
	}

	@Override
	public void destroy(Entity entity) {
		elapsed = 0.0f;
		hasBlocked = false;
		blocking = true;
		entity.remove(SwingComponent.class);
		if(hadInv) Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
		hadInv = false;
	}

}
