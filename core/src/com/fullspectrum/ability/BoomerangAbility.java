package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.factory.ProjectileFactory;
import com.fullspectrum.factory.ProjectileFactory.ProjectileData;
import com.fullspectrum.fsm.AnimationStateMachine;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;
import com.fullspectrum.movement.BoomerangCurveMovement;
import com.fullspectrum.movement.BoomerangLineMovement;
import com.fullspectrum.utils.PhysicsUtils;

public class BoomerangAbility extends Ability {

	private float throwAngle = 45.0f;
	private Phase currentPhase = Phase.OUT;
	private Entity boomerang;
	private boolean unlockedOtherAbilities = false;
	
	private int throwFrame = 2;
	private boolean hasThrown = false;
	private float elapsed;
	
	private float speed;
	private float turnSpeed = 180.0f;
	private float damage;
	private float distanceOut = 10.0f;
	private float timeOut;
	private float maxDuration;
	
	public BoomerangAbility(float cooldown, Actions input, float speed, float damage, float maxDuration){
		super(AbilityType.BOOMERANG, AssetLoader.getInstance().getRegion(Asset.BOOMERANG_ICON), cooldown, input, true);
		this.speed = speed;
		this.damage = damage;
		this.maxDuration = maxDuration;
		timeOut = distanceOut / speed;
	}

	@Override
	protected void init(Entity entity) {
		Mappers.asm.get(entity).get(EntityAnim.BOOMERANG_ARMS).changeState(EntityAnim.BOOMERANG_ARMS);
	}

	@Override
	protected void update(Entity entity, float delta) {
		elapsed += delta;
		
		int frame = (int)(elapsed / GameVars.ANIM_FRAME);
		if(frame == throwFrame && !hasThrown){
			hasThrown = true;
			ProjectileData data = ProjectileFactory.initProjectile(entity, 10f, -5f, throwAngle);
			boomerang = EntityFactory.createBoomerang(entity, data.x, data.y, speed, turnSpeed, data.angle, damage, Mappers.type.get(entity).type);
			EntityManager.addEntity(boomerang);
		}
		
		switch(currentPhase){
		case OUT:
			if(elapsed > timeOut){
				currentPhase = Phase.TURN;
				Mappers.controlledMovement.get(boomerang).changeMovement(Phase.TURN.ordinal());
				((BoomerangCurveMovement)Mappers.controlledMovement.get(boomerang).getCurrentMovement()).setCurrentAngle(throwAngle);
			}
			break;
		case TURN:
			// turn the boomerang
			Vector2 playerPos = PhysicsUtils.getPos(entity);
			Vector2 boomerangPos = PhysicsUtils.getPos(boomerang);
			Vector2 boomerangVel = Mappers.body.get(boomerang).body.getLinearVelocity();
			float threshold = 5.0f;
			
			float posAngle = MathUtils.radiansToDegrees * MathUtils.atan2(playerPos.y - boomerangPos.y, playerPos.x - boomerangPos.x);
			float velAngle = MathUtils.radiansToDegrees * MathUtils.atan2(boomerangVel.y, boomerangVel.x);
			
			if(Math.abs(posAngle - velAngle) < threshold){
				currentPhase = Phase.BACK;
				Mappers.controlledMovement.get(boomerang).changeMovement(Phase.BACK.ordinal());
				((BoomerangLineMovement)Mappers.controlledMovement.get(boomerang).getCurrentMovement()).setAngle(velAngle);
			}
			break;
		case BACK:
			
			break;
		default:
			break;
		}

		AnimationStateMachine upperBodyASM = Mappers.asm.get(entity).get(EntityAnim.IDLE_ARMS);
		if(!unlockedOtherAbilities && upperBodyASM.getCurrentAnimation() != EntityAnim.BOOMERANG_ARMS) {
			Mappers.ability.get(entity).unlockAllBlocking();
			unlockedOtherAbilities = true;
		}
		
		if(elapsed >= maxDuration) setDone(true);
	}

	@Override
	protected void destroy(Entity entity) {
		hasThrown = false;
		elapsed = 0.0f;
		unlockedOtherAbilities = false;
		currentPhase = Phase.OUT;
	}
	
	public Phase getCurrentPhase() {
		return currentPhase;
	}
	
	public enum Phase {
		OUT,
		TURN,
		BACK
	}
	
	
}
