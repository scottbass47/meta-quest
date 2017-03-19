package com.fullspectrum.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.FrameMovementComponent;
import com.fullspectrum.component.FrameMovementComponent.Frame;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.game.GameVars;

public class FrameMovementSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	private final static Family frameFamily = Family.all(BodyComponent.class, FrameMovementComponent.class, FacingComponent.class).get();
	
	public FrameMovementSystem() {
		super(frameFamily);
	}
	
	@Override
	public void addedToEngine(Engine engine) {
		super.addedToEngine(engine);
		engine.addEntityListener(frameFamily, new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				if(Mappers.body.get(entity) != null && Mappers.body.get(entity).body != null){
					Mappers.body.get(entity).body.setGravityScale(1.0f);
					Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
				}
			}
			
			@Override
			public void entityAdded(Entity entity) {
				Mappers.body.get(entity).body.setGravityScale(0.0f);
				Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);

				FrameMovementComponent fmc = Mappers.frameMovement.get(entity);
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
		FacingComponent facingComp = Mappers.facing.get(entity);
		
		fmc.elapsed += deltaTime;
		int frameNumber  = (int)(fmc.elapsed / GameVars.ANIM_FRAME);
		
		if(fmc.index >= fmc.frames.size) {
			fmc.frameTimer += deltaTime;
			if(fmc.frameTimer >= GameVars.ANIM_FRAME){
				fmc.frameTimer = 0.0f;
				bodyComp.body.setLinearVelocity(0.0f, 0.0f);
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
			
			Vector2 vel = new Vector2(x / GameVars.ANIM_FRAME, y / GameVars.ANIM_FRAME);
			bodyComp.body.applyLinearImpulse(
					vel.sub(bodyComp.body.getLinearVelocity()), 
					new Vector2(bodyComp.body.getWorldCenter().x, bodyComp.body.getWorldCenter().y), 
					true);
			fmc.index++;
			fmc.frameTimer = 0.0f;
		} else{
			fmc.frameTimer += deltaTime;
			if(fmc.frameTimer >= GameVars.ANIM_FRAME){
				fmc.frameTimer = 0.0f;
				bodyComp.body.setLinearVelocity(0.0f, 0.0f);
			}
		}
	}		
}
