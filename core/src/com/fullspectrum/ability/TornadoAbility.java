package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.ControlledMovementComponent;
import com.fullspectrum.component.ControlledMovementComponent.Movement;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class TornadoAbility extends TimedAbility{
	
	private ObjectSet<Entity> pulled; // entites that have controlled movement
	private ObjectSet<Entity> hit; // entities that have been hit
	
	private float damage;
	private float knockback;
	private float range;
	private float yOff = 1.0f;
	private int startingFrame;

	public TornadoAbility(float cooldown, Actions input, float duration, float damage, float knockback, float range, int startingFrame) {
		super(AbilityType.TORNADO, AssetLoader.getInstance().getRegion(Asset.TORNADO_ICON), cooldown, input, duration, true);
		this.damage = damage;
		this.knockback = knockback;
		this.range = range;
		this.startingFrame = startingFrame;
		addTemporaryImmunties(EffectType.values());
		setAbilityConstraints(new AbilityConstraints() {
			@Override
			public boolean canUse(Ability ability, Entity entity) {
				return Mappers.collision.get(entity).onGround();
			}
		});
		pulled = new ObjectSet<Entity>();
		hit = new ObjectSet<Entity>();
	}

	@Override
	protected void init(Entity entity) {
		Mappers.body.get(entity).body.setGravityScale(0.0f);
		Mappers.esm.get(entity).get(EntityStates.TORNADO).changeState(EntityStates.TORNADO);
		Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
		Mappers.facing.get(entity).locked = true;
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		if((int)(elapsed / GameVars.ANIM_FRAME) < startingFrame) return;
		
		DebugRender.setColor(Color.CYAN);
		DebugRender.setType(ShapeType.Line);
		DebugRender.circle(PhysicsUtils.getPos(entity).x, PhysicsUtils.getPos(entity).y, range);
		
		// First get entities in radius to be sucked in
		Array<Entity> toPull = Mappers.level.get(entity).levelHelper.getEntities(new EntityGrabber() {
			@Override
			public boolean validEntity(Entity me, Entity other) {
				if(Mappers.type.get(me).same(Mappers.type.get(other))) return false;

				return PhysicsUtils.getDistanceSqr(me, other) <= range * range && !pulled.contains(other);
			}
			
			@Override
			public Family componentsNeeded() {
				return Family.all(TypeComponent.class, HealthComponent.class).get();
			}
		});
		
		for(Entity e : toPull){
			EntityUtils.add(e, ControlledMovementComponent.class).set(new Movement() {
				@Override
				public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
					// Get the player
					Entity player = EntityUtils.getPlayer(Mappers.engine.get(entity).engine);
					
					Vector2 entityPos = PhysicsUtils.getPos(entity);
					Vector2 playerPos = PhysicsUtils.getPos(player);
					playerPos.add(0.0f, yOff);
					
					float theta = MathUtils.atan2(playerPos.y - entityPos.y, playerPos.x - entityPos.x);
					float minSpeed = 1.0f;
					float maxSpeed = 35.0f;
					
					float speed = MathUtils.lerp(minSpeed, maxSpeed, Math.max(1 - (PhysicsUtils.getDistance(entity, player) / range), 0.2f));
					
					return new Vector2(speed * MathUtils.cos(theta), speed * MathUtils.sin(theta));
				}
			});
			pulled.add(e);
		}
		
		// Now, get entities within range to be hit
		Array<Entity> toHit = Mappers.level.get(entity).levelHelper.getEntities(new EntityGrabber() {
			@Override
			public boolean validEntity(Entity me, Entity other) {
				if(Mappers.type.get(me).same(Mappers.type.get(other))) return false;

				Vector2 myPos = PhysicsUtils.getPos(me).add(0.0f, yOff);
				Vector2 entityPos = PhysicsUtils.getPos(other);
				return PhysicsUtils.getDistanceSqr(myPos, entityPos) <= 0.5f && !hit.contains(other);
			}
			
			@Override
			public Family componentsNeeded() {
				return Family.all(TypeComponent.class, HealthComponent.class).get();
			}
		});
		
		for(Entity e : toHit){
			Effects.giveKnockBack(e, knockback, MathUtils.random(180.0f));
			DamageHandler.dealDamage(entity, e, damage);
			hit.add(e);
			e.remove(ControlledMovementComponent.class);
		}
		
		// See if hit entities are ready for round 2
		for(Entity e : hit){
			if(Mappers.knockBack.get(e) == null){
				pulled.remove(e);
				hit.remove(e);
			}
		}
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.body.get(entity).body.setGravityScale(1.0f);
		Mappers.esm.get(entity).get(EntityStates.TORNADO).changeState(EntityStates.FALLING);
		Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
		Mappers.facing.get(entity).locked = false;
		
		for(Entity e : pulled){
			e.remove(ControlledMovementComponent.class);
		}
		pulled.clear();
		hit.clear();
	}

	
	
}
