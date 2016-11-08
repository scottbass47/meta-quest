package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.StaminaComponent;

public class StaminaSystem extends IteratingSystem{

	public StaminaSystem(){
		super(Family.all(StaminaComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		StaminaComponent staminaComp = Mappers.stamina.get(entity);
		
		if(staminaComp.locked) return;
		staminaComp.timeElapsed += deltaTime;
		
		if(staminaComp.timeElapsed > staminaComp.delay){
			staminaComp.stamina = MathUtils.clamp(staminaComp.stamina + staminaComp.rechargeRate * deltaTime, 0, staminaComp.maxStamina);
		}
		
	}
	
	
	
}
