package com.fullspectrum.systems;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TimerComponent.Timer;

public class TimerSystem extends IteratingSystem{

	public TimerSystem(){
		super(Family.all(TimerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TimerComponent timerComp = Mappers.timer.get(entity);
		if(timerComp.timers.size == 0) return;
		for(Iterator<String> iter = timerComp.timers.keys().iterator(); iter.hasNext();){
			Timer timer = timerComp.get(iter.next());
			if(timer.isPaused()) continue;
			timer.addTime(deltaTime);
			if(timer.isDone()){
				timer.onTime(entity);
				if(!timer.isLooping()){
					iter.remove();
					continue;
				}
				timer.resetElapsed();
			}
		}
	}
	
}
