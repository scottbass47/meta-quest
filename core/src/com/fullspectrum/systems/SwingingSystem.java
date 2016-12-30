package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.SwordComponent;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.game.GameVars;

public class SwingingSystem extends IteratingSystem{

	public SwingingSystem(){
		super(Family.all(SwingComponent.class, SwordComponent.class, FacingComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SwordComponent swordComp = Mappers.sword.get(entity);
		SwingComponent swingComp = Mappers.swing.get(entity);
		
		if(swordComp == null || !EntityUtils.isValid(swordComp.sword)){
			Gdx.app.log("Swinging System", "sword isn't a valid entity.");
			return;
		}
		
		swingComp.time += deltaTime;
		if(swingComp.time > swingComp.duration){
			swingComp.time = 0;
			return;
		}
		
		BodyComponent swordBodyComp = Mappers.body.get(swordComp.sword);
		
		if(swordBodyComp == null || swordBodyComp.body == null){
			Gdx.app.log("Swinging System", "sword doesn't have a valid physics body.");
		}
		
		Body swordBody = swordBodyComp.body;
		if(!swordBody.isActive()) {
			swordBody.setActive(true);
		}
		FacingComponent facingComp = Mappers.facing.get(entity);
		
		float degrees = 0.0f;
		if(facingComp.facingRight){
			degrees = swingComp.startAngle;
			degrees -= swingComp.time * (swingComp.rotationAmount / swingComp.duration);
		}
		else{
			degrees = 180 - swingComp.startAngle;
			degrees += swingComp.time * (swingComp.rotationAmount / swingComp.duration);
		}
		degrees *= MathUtils.degreesToRadians;
		swordBody.setTransform(swordBody.getPosition(), degrees);
		
		float dx = facingComp.facingRight ? -20 * GameVars.PPM_INV * 0.5f : -(-20 * GameVars.PPM_INV * 0.5f);
		float dy = 0;
		float radius = (float)Math.sqrt(dx * dx + dy * dy);
		swordBody.setTransform(swordBody.getPosition().x + dx + radius * MathUtils.cos(degrees), swordBody.getPosition().y + dy + radius * MathUtils.sin(degrees), degrees);
		
	}
	
}
