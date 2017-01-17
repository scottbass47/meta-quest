package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;

public class FacingSystem extends IteratingSystem{

	public FacingSystem(){
		super(Family.all(FacingComponent.class, InputComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		FacingComponent facingComp = Mappers.facing.get(entity);
		InputComponent inputComp = Mappers.input.get(entity);
		float dx = inputComp.input.getValue(Actions.MOVE_LEFT) - inputComp.input.getValue(Actions.MOVE_RIGHT);
		facingComp.facingRight = MathUtils.isEqual(dx, 0.0f, 0.005f) ? facingComp.facingRight : dx < 0;
	}	
}
