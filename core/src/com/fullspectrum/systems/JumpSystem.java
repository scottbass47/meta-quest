package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

public class JumpSystem extends IteratingSystem{

	private static final Family jumpFamily = Family.all(JumpComponent.class, BodyComponent.class).get();
	
	private ArrayMap<Entity, JumpInfo> jumpMap;
	
	public JumpSystem(){
		super(jumpFamily);
		jumpMap = new ArrayMap<Entity, JumpInfo>();
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(jumpFamily, new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				if(jumpMap.get(entity).requiresCleanup){
					JumpComponent jumpComp = jumpMap.get(entity).jumpComp;
					BodyComponent bodyComp = Mappers.body.get(entity);
					
					if(bodyComp != null && bodyComp.body != null) {
						jump(Math.min(jumpComp.floatAmount, bodyComp.body.getLinearVelocity().y), bodyComp.body);
					}
					jumpComp.jumpReady = true;
					jumpComp.timeDown = 0.5f;
				}
				jumpMap.removeKey(entity);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				jumpMap.put(entity, new JumpInfo(true, Mappers.jump.get(entity)));
			}
		});
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		Body body = Mappers.body.get(entity).body;
		JumpComponent jumpComp = Mappers.jump.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);
		
		float desiredVel = 0.0f;
		boolean removeImmediately = false;
		
		// For the player, we do variable jump height based on input
		// For ai, jump height is calculated using multiplier
		if(inputComp != null && inputComp.input instanceof GameInput) {
			if(jumpComp.jumpReady) {
				jumpComp.jumpReady = false;
				jumpComp.timeDown = 0.5f;
			}
			desiredVel = jumpComp.timeDown * jumpComp.maxForce;
			jumpComp.timeDown -= deltaTime;
		} else {
			removeImmediately = true; // for ai, we want to remove the jump component after applying the force initially
			desiredVel = jumpComp.maxForce * jumpComp.multiplier;
		}
		
		boolean hitCeiling = body.getLinearVelocity().y < 0.1f && jumpComp.timeDown < 0.45f;
		if(removeImmediately || jumpComp.timeDown <= 0.0f || (inputComp.input != null && !inputComp.input.isPressed(Actions.JUMP)) || hitCeiling) {
			if(removeImmediately) { 
				jump(desiredVel, body);
			} else {
				if(hitCeiling) {
					jump(0, body);
				} else {
					jump(Math.min(jumpComp.floatAmount, body.getLinearVelocity().y), body);
				}
			}
			jumpComp.jumpReady = true;
			jumpComp.timeDown = 0.5f;
			jumpMap.get(entity).requiresCleanup = false;
			entity.remove(JumpComponent.class);
		} else {
			jump(desiredVel, body);
		}
	}
	
	private void jump(float desiredVel, Body body) {
		body.applyLinearImpulse(0, desiredVel - body.getLinearVelocity().y, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
	
	private static class JumpInfo {
		
		private boolean requiresCleanup;
		private JumpComponent jumpComp;
		
		private JumpInfo(boolean requiresCleanup, JumpComponent jumpComp) {
			this.requiresCleanup = requiresCleanup;
			this.jumpComp = jumpComp;
		}
	}
}
