package com.cpubrew.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.FacingComponent;
import com.cpubrew.component.FrameMovementComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.VelocityComponent;
import com.cpubrew.component.FrameMovementComponent.Frame;
import com.cpubrew.game.GameVars;

public class FrameMovementSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	private final static Family frameFamily = Family.all(FrameMovementComponent.class, FacingComponent.class).one(BodyComponent.class, VelocityComponent.class).get();

	private ArrayMap<Entity, Boolean> gravMap;
	
	public FrameMovementSystem() {
		super(frameFamily);
		gravMap = new ArrayMap<Entity, Boolean>();
	}
	
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(frameFamily, new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				if(Mappers.body.get(entity) != null && Mappers.body.get(entity).body != null){
					BodyComponent bodyComp = Mappers.body.get(entity);
					bodyComp.body.setGravityScale(1.0f);
					bodyComp.body.setLinearVelocity(0.0f, gravMap.get(entity) ? bodyComp.body.getLinearVelocity().y : 0.0f);
				}
				gravMap.removeKey(entity);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				FrameMovementComponent fmc = Mappers.frameMovement.get(entity);

				if(Mappers.body.get(entity) != null && Mappers.body.get(entity).body != null) {
					BodyComponent bodyComp = Mappers.body.get(entity);
					bodyComp.body.setGravityScale(fmc.useGravity ? 1.0f : 0.0f);
					bodyComp.body.setLinearVelocity(0.0f, fmc.useGravity ? bodyComp.body.getLinearVelocity().y : 0.0f);
				}

				gravMap.put(entity, fmc.useGravity);
				fmc.elapsed = 0.0f;
				fmc.index = 0;
				fmc.frameTimer = 0.0f;
			}
		});
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		FrameMovementComponent fmc = Mappers.frameMovement.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		VelocityComponent velComp = Mappers.velocity.get(entity);
		FacingComponent facingComp = Mappers.facing.get(entity);
		
		boolean hasBody = bodyComp != null && bodyComp.body != null;
		
		fmc.elapsed += deltaTime;
		int frameNumber  = (int)(fmc.elapsed / GameVars.ANIM_FRAME);
		
		if(fmc.index >= fmc.frames.size) {
			fmc.frameTimer += deltaTime;
			if(fmc.frameTimer >= GameVars.ANIM_FRAME){
				fmc.frameTimer = 0.0f;
				
				if(hasBody) {
					bodyComp.body.setLinearVelocity(0.0f, fmc.useGravity ? bodyComp.body.getLinearVelocity().y : 0.0f);
				} else {
					velComp.set(0.0f, fmc.useGravity ? velComp.dy : 0.0f);
				}
			}
			return;
		}
		
		Frame currFrame = fmc.frames.get(fmc.index);
		int number = currFrame.getNumber();
		
		if(frameNumber == number){
			// Move entity
			float x = currFrame.getX() * GameVars.PPM_INV;
			float y = currFrame.getY() * GameVars.PPM_INV;
			x = facingComp.facingRight ? x : -x;
			
			Vector2 vel = new Vector2(x / GameVars.ANIM_FRAME, fmc.useGravity ? (hasBody ? bodyComp.body.getLinearVelocity().y : velComp.dy) : y / GameVars.ANIM_FRAME);
			
			if(hasBody) {
				bodyComp.body.applyLinearImpulse(
						vel.sub(bodyComp.body.getLinearVelocity()), 
						new Vector2(bodyComp.body.getWorldCenter().x, bodyComp.body.getWorldCenter().y), 
						true);
			} else {
				velComp.set(vel);
			}
			
			fmc.index++;
			fmc.frameTimer = 0.0f;
		} else{
			fmc.frameTimer += deltaTime;
			if(fmc.frameTimer >= GameVars.ANIM_FRAME){
				fmc.frameTimer = 0.0f;
				
				if(hasBody) {
					bodyComp.body.setLinearVelocity(0.0f, fmc.useGravity ? bodyComp.body.getLinearVelocity().y : 0.0f);
				} else {
					velComp.set(0.0f, fmc.useGravity ? velComp.dy : 0.0f);
				}
			}
		}
	}		
}
