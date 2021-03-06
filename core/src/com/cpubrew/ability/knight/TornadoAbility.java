package com.cpubrew.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.OnGroundConstraint;
import com.cpubrew.ability.TimedAbility;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.ControlledMovementComponent;
import com.cpubrew.component.HealthComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.StatusComponent;
import com.cpubrew.component.InvincibilityComponent.InvincibilityType;
import com.cpubrew.debug.DebugRender;
import com.cpubrew.effects.EffectType;
import com.cpubrew.effects.Effects;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.game.GameVars;
import com.cpubrew.handlers.DamageHandler;
import com.cpubrew.input.Actions;
import com.cpubrew.level.EntityGrabber;
import com.cpubrew.movement.Movement;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.PhysicsUtils;

public class TornadoAbility extends TimedAbility{
	
	private ObjectSet<Entity> pulled; // entities that have controlled movement
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
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryInvincibilities(InvincibilityType.ALL);
		lockFacing();
		pulled = new ObjectSet<Entity>();
		hit = new ObjectSet<Entity>();
	}

	@Override
	protected void init(Entity entity) {
		Mappers.body.get(entity).body.setGravityScale(0.0f);
		Mappers.esm.get(entity).get(EntityStates.TORNADO).changeState(EntityStates.TORNADO);
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
				if(Mappers.status.get(me).same(Mappers.status.get(other))) return false;

				return PhysicsUtils.getDistanceSqr(me, other) <= range * range && !pulled.contains(other);
			}
			
			@Override
			public Family componentsNeeded() {
				return Family.all(StatusComponent.class, HealthComponent.class).get();
			}
		});
		
		for(Entity e : toPull){
			EntityUtils.add(e, ControlledMovementComponent.class).set(new Movement() {
				@Override
				public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
					// Get the player
					Entity player = EntityUtils.getPlayer();
					
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
				if(Mappers.status.get(me).same(Mappers.status.get(other))) return false;

				Vector2 myPos = PhysicsUtils.getPos(me).add(0.0f, yOff);
				Vector2 entityPos = PhysicsUtils.getPos(other);
				return PhysicsUtils.getDistanceSqr(myPos, entityPos) <= 0.5f && !hit.contains(other);
			}
			
			@Override
			public Family componentsNeeded() {
				return Family.all(StatusComponent.class, HealthComponent.class).get();
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
		
		for(Entity e : pulled){
			e.remove(ControlledMovementComponent.class);
		}
		pulled.clear();
		hit.clear();
	}

	
	
}
