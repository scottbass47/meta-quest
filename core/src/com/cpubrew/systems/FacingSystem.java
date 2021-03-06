package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.cpubrew.component.FacingComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.VelocityComponent;
import com.cpubrew.input.Actions;

public class FacingSystem extends IteratingSystem {

	public FacingSystem() {
		super(Family.all(FacingComponent.class).one(InputComponent.class, VelocityComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		FacingComponent facingComp = Mappers.facing.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);

		if(facingComp.locked) return;
		
		float dx = 0.0f;
		if (inputComp != null){
			dx = inputComp.input.getValue(Actions.MOVE_RIGHT) - inputComp.input.getValue(Actions.MOVE_LEFT);
		}else{
			dx = Mappers.velocity.get(entity).dx;
		}
		facingComp.facingRight = MathUtils.isEqual(dx, 0.0f, 0.005f) ? facingComp.facingRight : dx > 0;
	}
}
