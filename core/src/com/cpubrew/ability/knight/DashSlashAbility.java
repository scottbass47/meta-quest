package com.cpubrew.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.TimedAbility;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.FacingComponent;
import com.cpubrew.component.ForceComponent;
import com.cpubrew.component.HealthComponent;
import com.cpubrew.component.LevelComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.StatusComponent;
import com.cpubrew.component.InvincibilityComponent.InvincibilityType;
import com.cpubrew.debug.DebugRender;
import com.cpubrew.effects.EffectType;
import com.cpubrew.effects.Effects;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.handlers.DamageHandler;
import com.cpubrew.input.Actions;
import com.cpubrew.level.EntityGrabber;

public class DashSlashAbility extends TimedAbility{

	private ObjectSet<Entity> hitEntities;
	private float distance;
	private float damage;
	private float knockUp;
	
	public DashSlashAbility(float cooldown, Actions input, float duration, float distance, float damage, float knockUp) {
		super(AbilityType.DASH_SLASH, AssetLoader.getInstance().getRegion(Asset.DASH_SLASH_ICON), cooldown, input, duration, true);
		this.distance = distance;
		this.damage = damage;
		this.knockUp = knockUp;
		addTemporaryImmunties(EffectType.KNOCKBACK, EffectType.STUN);
		addTemporaryInvincibilities(InvincibilityType.ALL);
		lockFacing();
		hitEntities = new ObjectSet<Entity>();
	}

	@Override
	protected void init(Entity entity) {
		Mappers.body.get(entity).body.setGravityScale(0.0f);
		Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
		Mappers.esm.get(entity).get(EntityStates.DASH).changeState(EntityStates.DASH);
		float speed = distance / duration;
		entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class).set(Mappers.facing.get(entity).facingRight ? speed : -speed, 0.0f));
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		LevelComponent levelComp = Mappers.level.get(entity);
		Array<Entity> entities = levelComp.levelHelper.getEntities(new EntityGrabber() {
			@Override
			public boolean validEntity(Entity me, Entity other) {
				if(Mappers.status.get(me).same(Mappers.status.get(other))) return false;
				if(hitEntities.contains(other)) return false;
				
				FacingComponent facingComp = Mappers.facing.get(me);
				Body myBody = Mappers.body.get(me).body;
				Body otherBody = Mappers.body.get(other).body;
				
				float myX = myBody.getPosition().x;
				float myY = myBody.getPosition().y;
				float otherX = otherBody.getPosition().x;
				float otherY = otherBody.getPosition().y;
				
				float minX = 0.0f;
				float maxX = minX + 1.0f;
				float yRange = 0.65f;
				
				// Construct box in front of you
				float closeX = facingComp.facingRight ? myX + minX : myX - minX;
				float farX = facingComp.facingRight ? myX + maxX : myX - maxX;
				float top = myY + yRange;
				float bottom = myY - yRange;
				
				DebugRender.setType(ShapeType.Line);
				DebugRender.setColor(Color.CYAN);
				DebugRender.rect(facingComp.facingRight ? closeX : farX, bottom, Math.abs(farX - closeX), top - bottom, 1.0f);
				
				return (((facingComp.facingRight && otherX >= closeX && otherX <= farX) || (!facingComp.facingRight && otherX >= farX && otherX <= closeX)) && otherY <= top && otherY >= bottom);
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public Family componentsNeeded() {
				return Family.all(StatusComponent.class, HealthComponent.class).get();
			}
		});
		
		for(Entity e : entities){
			hitEntities.add(e);
			Effects.giveKnockBack(e, knockUp, 90.0f);
			DamageHandler.dealDamage(entity, e, damage);
		}
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.body.get(entity).body.setGravityScale(1.0f);
		Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
		Mappers.esm.get(entity).get(EntityStates.DASH).changeState(EntityStates.IDLING);
		hitEntities.clear();
	}

}
