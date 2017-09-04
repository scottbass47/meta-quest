package com.cpubrew.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.cpubrew.component.KnockBackComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.WeightComponent;

public class KnockBackEffect extends Effect{

	private float distance;
	private float speed;
	private float angle;
	
    public KnockBackEffect(Entity toEntity, float distance, float angle) {
    	super(toEntity, 0.0f, false);
    	this.distance = calculateDistance(distance);
    	this.speed = calculateSpeed();
    	this.angle = angle;
    	this.duration = getTime();
    }
    
    private float calculateDistance(float distance) {
    	WeightComponent weightComp = Mappers.weight.get(toEntity);
    	return distance / (weightComp.weight / 10.0f);
    }
    
    private float calculateSpeed(){
//    	if(distance <= 5.0f){
//    		return 15.0f;
//    	} else if(distance <= 10.0f){
//    		return 25.0f;
//    	} else if(distance <= 15.0f){
//    		return 35.0f;
//    	}
//    	return 45.0f;
    	return 4f * distance + 7.5f;
    }
    
    private float getTime(){
    	// vf^2 = v0^2 + 2ax
    	// (vf^2 - v0^2) / (2x) = a
    	float vf = 0.0f;
    	float v0 = speed;
    	float accel = ((float)Math.pow(vf, 2) - (float)Math.pow(v0, 2)) / (2 * distance);
    	
    	// vf = v0 + at
    	// t = (vf - v0) / a
    	return (vf - v0) / accel;
    }
    
	@Override
	public void give() {
		toEntity.add(Mappers.engine.get(toEntity).engine.createComponent(KnockBackComponent.class).set(distance, speed, angle));
	}
	
	@Override
	public EffectType getType() {
		return EffectType.KNOCKBACK;
	}

	@Override
	public void cleanUp() {
		Body body = Mappers.body.get(toEntity).body;
		if(Mappers.groundMovement.get(toEntity) != null){
			body.setLinearVelocity(0.0f, body.getLinearVelocity().y);
			Effects.giveEase(toEntity, 0.5f, 10.0f);
		}
		else {
			body.setLinearVelocity(0.0f, 0.0f);
		}
		toEntity.remove(KnockBackComponent.class);
	}
	
	@Override
	public String getName() {
		return "knockback";
	}
	
	public float getDistance() {
		return distance;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public float getAngle() {
		return angle;
	}


}
