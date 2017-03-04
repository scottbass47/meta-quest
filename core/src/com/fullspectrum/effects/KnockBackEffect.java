package com.fullspectrum.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.Mappers;

// INCOMPLETE Knockback doesn't work properly on flying enemies and on ladders
public class KnockBackEffect extends Effect{

	public static final float SPEED = 15.0f;
	private float distance;
	private float angle;
	
    public KnockBackEffect(Entity toEntity, float distance, float angle) {
    	super(toEntity, 0.0f);
    	this.distance = distance;
    	this.angle = angle;
    	this.duration = getTime();
    }
    
    private float getTime(){
    	// vf^2 = v0^2 + 2ax
    	// (vf^2 - v0^2) / (2x) = a
    	float vf = SPEED * 0.0f;
    	float v0 = SPEED;
    	float accel = ((float)Math.pow(vf, 2) - (float)Math.pow(v0, 2)) / (2 * distance);
    	
    	// vf = v0 + at
    	// t = (vf - v0) / a
    	return (vf - v0) / accel;
    }
    
	@Override
	public void give() {
		toEntity.add(Mappers.engine.get(toEntity).engine.createComponent(KnockBackComponent.class).set(distance, SPEED, angle));
	}
	
	@Override
	public void cleanUp() {
		Body body = Mappers.body.get(toEntity).body;
		body.setLinearVelocity(0.0f, body.getLinearVelocity().y); // CLEANUP Temporary (flying enemies, ladders, etc...)
		Effects.giveEase(toEntity, 0.5f, 10.0f);
		toEntity.remove(KnockBackComponent.class);
	}
	
	@Override
	public String getName() {
		return "knockback";
	}
	
	public float getDistance() {
		return distance;
	}
	
	public float getAngle() {
		return angle;
	}


}
