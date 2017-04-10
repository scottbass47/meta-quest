package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.BobComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.physics.FixtureType;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class HomingKnivesAbility extends AnimationAbility{

	private int frameRightThrow = 4;
	private int frameLeftThrow = 7;
	private int frameCenterThrow = 11;
	private boolean thrownRight = false;
	private boolean thrownLeft = false;
	private boolean thrownUp = false;
	
	private float clusterDistance = 3.0f;
	private float clusterRadius = 2.0f;
	private int knivesPerCluster;

	private float shootOutTime = 0.15f;
	private float damage;
	private float range;
	private float speed;
	
	private float bobTime = 1.0f;
	
	public HomingKnivesAbility(float cooldown, Actions input, Animation animation, int knivesPerCluster, float damage, float range, float speed){
		super(AbilityType.HOMING_KNIVES, AssetLoader.getInstance().getRegion(Asset.HOMING_KNIVES_ICON), cooldown, input, animation);
		this.knivesPerCluster = knivesPerCluster;
		this.damage = damage;
		this.range = range;
		this.speed = speed;
		addTemporaryImmunties(EffectType.values());
		setAbilityConstraints(new AbilityConstraints() {
			@Override
			public boolean canUse(Ability ability, Entity entity) {
				return Mappers.collision.get(entity).onGround();
			}
		});
	}

	@Override
	protected void init(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.HOMING_KNIVES).changeState(EntityStates.HOMING_KNIVES);
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		int frame = (int)(elapsed / GameVars.ANIM_FRAME);
		boolean facingRight = Mappers.facing.get(entity).facingRight;
		
		// Right Throw
		if(!thrownRight && ((frame == frameRightThrow && facingRight) || (frame == frameLeftThrow && !facingRight))){
			thrownRight = true;
			throwKnives(entity, 30.0f);
		}
		
		if(!thrownLeft && ((frame == frameLeftThrow && facingRight) || (frame == frameRightThrow && !facingRight))){
			thrownLeft = true;
			throwKnives(entity, 150.0f);
		}
		
		if(frame == frameCenterThrow && !thrownUp){
			thrownUp = true;
			throwKnives(entity, 90.0f);
		}
	}
	
	private void throwKnives(Entity entity, float angle){
		Vector2 playerPos = PhysicsUtils.getPos(entity);
		Vector2 clusterCenter = new Vector2();
		
		clusterCenter.add(playerPos);
		clusterCenter.add(clusterDistance * MathUtils.cosDeg(angle), clusterDistance * MathUtils.sinDeg(angle));
	
		float ang1 = 0;
		float ang2 = 0;
		float angInc = 360.0f / (float)knivesPerCluster;
		for(int i = 0; i < knivesPerCluster; i++){
			ang1 = i * angInc;
			ang2 = (i + 1) * angInc;
			
//			DebugRender.setColor(Color.SCARLET);
//			DebugRender.setType(ShapeType.Line);
//			DebugRender.arc(clusterCenter.x, clusterCenter.y, clusterRadius, ang1, ang2 - ang1, 1.0f);
			
			float angRand = MathUtils.random(ang1, ang2);
			float radiusRand = MathUtils.random(0.0f, clusterRadius);
			
			Vector2 knifePos = new Vector2(radiusRand * MathUtils.cosDeg(angRand), radiusRand * MathUtils.sinDeg(angRand));
			knifePos.add(clusterCenter);
			
//			DebugRender.setColor(Color.GREEN);
//			DebugRender.setType(ShapeType.Filled);
//			DebugRender.circle(knifePos.x, knifePos.y, 0.1f, 1.0f);
			
			spawnKnife(entity, knifePos, elapsed);
		}
	}
	
	private void spawnKnife(Entity entity, Vector2 knifePos, final float elapsed){
		Entity knife = EntityFactory.createHomingKnife(
				PhysicsUtils.getPos(entity), 
				knifePos, 
				shootOutTime, damage, Mappers.type.get(entity).type);
		
		Mappers.timer.get(knife).add("shoot_out", shootOutTime, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
				setupIdlePhase(entity, elapsed);
			}
		});
		
		EntityManager.addEntity(knife);
	}
	
	private void setupIdlePhase(Entity knife, float elapsed){
		EntityUtils.add(knife, BobComponent.class).set(1.0f, 0.5f);
		
		float time = frameCenterThrow * GameVars.ANIM_FRAME - elapsed + bobTime;
		Mappers.timer.get(knife).add("target_and_shoot", time, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				// Setup Proper Collision
				Mappers.collisionListener.get(entity).collisionData.setCollisionListener(FixtureType.BULLET, FixtureType.BULLET.getListener());
				
				// Target Enemy
				Array<Entity> targets = Mappers.level.get(entity).levelHelper.getEntities(new EntityGrabber() {
					@Override
					public boolean validEntity(Entity me, Entity other) {
						if(!Mappers.type.get(me).shouldCollide(Mappers.type.get(other))) return false;
						if(PhysicsUtils.getDistanceSqr(me, other) > range * range) return false;
						Vector2 myPos = PhysicsUtils.getPos(me);
						Vector2 otherPos = PhysicsUtils.getPos(other);
						return Mappers.level.get(me).level.performRayTrace(myPos.x, myPos.y, otherPos.x, otherPos.y);
					}
					
					@SuppressWarnings("unchecked")
					@Override
					public Family componentsNeeded() {
						return Family.all(TypeComponent.class, HealthComponent.class).get();
					}
				});
				
				float angle = 0;
				if(targets.size > 0){
					Entity target = targets.get(MathUtils.random(targets.size - 1));
					Vector2 targetPos = PhysicsUtils.getPos(target);
					Vector2 myPos = PhysicsUtils.getPos(entity);
					
					angle = MathUtils.atan2(targetPos.y - myPos.y, targetPos.x - myPos.x);
				} else {
					angle = MathUtils.random(MathUtils.PI2);
				}
				EntityUtils.add(entity, ForceComponent.class).set(speed * MathUtils.cos(angle), speed * MathUtils.sin(angle));
			}
		});
	}
	
	@Override
	protected void destroy(Entity entity) {
		thrownRight = false;
		thrownLeft = false;
		thrownUp = false;
		
		Mappers.esm.get(entity).get(EntityStates.HOMING_KNIVES).changeState(EntityStates.IDLING);
	}
}
