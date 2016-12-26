package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimerComponent;

public class TimerSystem extends IteratingSystem{

	public TimerSystem(){
		super(Family.all(TimerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TimerComponent timerComp = Mappers.timer.get(entity);
		timerComp.elapsed += deltaTime;
		if(timerComp.elapsed >= timerComp.time){
			timerComp.listener.onTime(entity);
			if(!timerComp.looping){
				entity.remove(TimerComponent.class);
				return;
			}
			timerComp.elapsed -= timerComp.time;
		}
	}
	
}
