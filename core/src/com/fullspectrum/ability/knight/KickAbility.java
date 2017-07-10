package com.fullspectrum.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.StatusComponent;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;

public class KickAbility extends AnimationAbility{

	private float animDelay;
	private float range;
	private float knockback;
	private float damage;
	private boolean hasKicked = false;
	
	public KickAbility(float cooldown, Actions input, float animDelay, float range, float knockback, float damage, Animation<TextureRegion> kickAnimation) {
		super(AbilityType.KICK, AssetLoader.getInstance().getRegion(Asset.KICK_ICON), cooldown, input, kickAnimation);
		this.animDelay = animDelay;
		this.range = range;
		this.knockback = knockback;
		this.damage = damage;
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryImmunties(EffectType.KNOCKBACK, EffectType.STUN);
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		if(elapsed >= animDelay && !hasKicked){
			// Perform Kick
			Array<Entity> hitEntities = Mappers.level.get(entity).levelHelper.getEntities(new EntityGrabber() {
				@Override
				public boolean validEntity(Entity me, Entity other) {
					// Same type enemies aren't affected
					if(Mappers.status.get(me).same(Mappers.status.get(other))) return false;
					
					FacingComponent facingComp = Mappers.facing.get(me);
					Body myBody = Mappers.body.get(me).body;
					Body otherBody = Mappers.body.get(other).body;
					
					float xOff = facingComp.facingRight ? 0.5f : -0.5f;
					
					float myX = myBody.getPosition().x + xOff;
					float myY = myBody.getPosition().y;
					float otherX = otherBody.getPosition().x;
					float otherY = otherBody.getPosition().y;
					
					float minX = 0.0f;
					float maxX = minX + range;
					float yRange = 0.9f;
					
					// Construct box in front of you
					float closeX = facingComp.facingRight ? myX + minX : myX - minX;
					float farX = facingComp.facingRight ? myX + maxX : myX - maxX;
					float top = myY + yRange;
					float bottom = myY - yRange;
					
					DebugRender.setType(ShapeType.Line);
					DebugRender.setColor(Color.CYAN);
					DebugRender.rect(facingComp.facingRight ? closeX : farX, bottom, Math.abs(farX - closeX), top - bottom, 1.0f);
					
					Rectangle kick = new Rectangle(Math.min(closeX, farX), bottom, Math.abs(closeX - farX), top - bottom);
					Rectangle enemy = new Rectangle(Mappers.body.get(other).getAABB());
					enemy.x = otherX - enemy.width * 0.5f;
					enemy.y = otherY - enemy.height * 0.5f;
					
					return kick.overlaps(enemy);
//					return (((facingComp.facingRight && otherX >= closeX && otherX <= farX) || (!facingComp.facingRight && otherX >= farX && otherX <= closeX)) && otherY <= top && otherY >= bottom);
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public Family componentsNeeded() {
					return Family.all(HealthComponent.class, StatusComponent.class).get();
				}
			});
			
			for(Entity hitEntity : hitEntities){
				Effects.giveKnockBack(hitEntity, knockback, Mappers.facing.get(entity).facingRight ? 15.0f : 165.0f);
				DamageHandler.dealDamage(entity, hitEntity, damage);
			}
			hasKicked = true;
		}
	}

	@Override
	public void init(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.KICK).changeState(EntityStates.KICK);
		Mappers.facing.get(entity).locked = true;
	}

	@Override
	public void destroy(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.KICK).changeState(EntityStates.IDLING);
		Mappers.facing.get(entity).locked = false;
		hasKicked = false;
	}
}
