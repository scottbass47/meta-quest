package com.cpubrew.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.component.EaseComponent;
import com.cpubrew.component.Mappers;

public class EaseEffect extends Effect{

	private float accel;
	
	public EaseEffect(Entity toEntity, float duration, float accel) {
		super(toEntity, duration, false);
		this.accel = accel;
	}

	@Override
	protected void give() {
		Vector2 vel = Mappers.body.get(toEntity).body.getLinearVelocity(); 
		toEntity.add(Mappers.engine.get(toEntity).engine.createComponent(EaseComponent.class).set(accel, vel.x, vel.y));
	}

	@Override
	protected void cleanUp() {
		toEntity.remove(EaseComponent.class);
	}
	
	@Override
	public EffectType getType() {
		return EffectType.EASE;
	}

	@Override
	public String getName() {
		return "ease";
	}

}
