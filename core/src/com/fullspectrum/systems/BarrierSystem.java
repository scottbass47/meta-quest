package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.BarrierComponent;

public class BarrierSystem extends IteratingSystem{

	public BarrierSystem(){
		super(Family.all(BarrierComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BarrierComponent barrierComp = Mappers.barrier.get(entity);
		
		if(barrierComp.locked) return;
		barrierComp.timeElapsed += deltaTime;
		
		if(barrierComp.timeElapsed > barrierComp.delay){
			barrierComp.barrier = MathUtils.clamp(barrierComp.barrier + barrierComp.rechargeRate * deltaTime, 0, barrierComp.maxBarrier);
		}
		
	}
	
	
	
}
