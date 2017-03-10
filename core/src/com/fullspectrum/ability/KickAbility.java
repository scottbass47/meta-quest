package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;

public class KickAbility extends Ability{

	private float animDelay;
	private float range;
	private float knockback;
	private float damage;
	private float elapsed;
	private boolean hasKicked = false;
	
	public KickAbility(float cooldown, Actions input, float animDelay, float range, float knockback, float damage) {
		super(AbilityType.KICK, Assets.getInstance().getHUDElement(Assets.KICK_ICON), cooldown, input);
		this.animDelay = animDelay;
		this.range = range;
		this.knockback = knockback;
		this.damage = damage;
	}

	@Override
	public void update(Entity entity, float delta) {
		elapsed += delta;
		if(elapsed >= animDelay && !hasKicked){
			// Perform Kick
			Array<Entity> hitEntities = Mappers.level.get(entity).levelHelper.getEntities(new EntityGrabber() {
				@Override
				public boolean validEntity(Entity me, Entity other) {
					// Same type enemies aren't affected
					if(Mappers.type.get(me).same(Mappers.type.get(other))) return false;
					
					FacingComponent facingComp = Mappers.facing.get(me);
					Body myBody = Mappers.body.get(me).body;
					Body otherBody = Mappers.body.get(other).body;
					
					float myX = myBody.getPosition().x;
					float myY = myBody.getPosition().y;
					float otherX = otherBody.getPosition().x;
					float otherY = otherBody.getPosition().y;
					
					float minX = 0.0f;
					float maxX = minX + range;
					float yRange = 0.75f;
					
					// Construct box in front of you
					float closeX = facingComp.facingRight ? myX + minX : myX - minX;
					float farX = facingComp.facingRight ? myX + maxX : myX - maxX;
					float top = myY + yRange;
					float bottom = myY - yRange;
					
					DebugRender.setColor(Color.CYAN);
					DebugRender.rect(facingComp.facingRight ? closeX : farX, bottom, Math.abs(farX - closeX), top - bottom, 1.0f);
					
					return (((facingComp.facingRight && otherX >= closeX && otherX <= farX) || (!facingComp.facingRight && otherX >= farX && otherX <= closeX)) && otherY <= top && otherY >= bottom);
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public Family componentsNeeded() {
					return Family.all(HealthComponent.class, TypeComponent.class).get();
				}
			});
			
			for(Entity hitEntity : hitEntities){
				Effects.giveKnockBack(hitEntity, knockback, Mappers.facing.get(entity).facingRight ? 15.0f : 165.0f);
				DamageHandler.dealDamage(entity, hitEntity, damage);
			}
			hasKicked = true;
		}
		
		// Ability is over once the animation ends after kicking
		EntityStateMachine esm = Mappers.esm.get(entity).get(EntityStates.KICK);
		if(esm.getCurrentState() != EntityStates.KICK && hasKicked){
			setDone(true);
		}
	}

	@Override
	public void init(Entity entity) {
		Mappers.immune.get(entity).add(EffectType.KNOCKBACK).add(EffectType.STUN);
		Mappers.esm.get(entity).get(EntityStates.KICK).changeState(EntityStates.KICK);
	}

	@Override
	public void destroy(Entity entity) {
		Mappers.immune.get(entity).remove(EffectType.KNOCKBACK).remove(EffectType.STUN);
		hasKicked = false;
		elapsed = 0.0f;
	}

}