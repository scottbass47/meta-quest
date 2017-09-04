package com.cpubrew.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.cpubrew.component.BlinkComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.RenderComponent;
import com.cpubrew.component.BlinkComponent.Blink;

public class BlinkSystem extends IteratingSystem{

	public BlinkSystem(){
		super(Family.all(BlinkComponent.class).get());
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		BlinkComponent blinkComp = Mappers.blink.get(entity);
		RenderComponent renderComp = Mappers.render.get(entity);
		
		blinkComp.timeElapsed += deltaTime;
		
		float t = 0;
		Blink currentBlink = null;
		for(Blink blink : blinkComp.blinks){
			if(blinkComp.timeElapsed < blink.duration + t){
				currentBlink = blink;
				break;
			}
			t += blink.duration;
		}
		
		if(currentBlink == null){
			if(renderComp != null){
				entity.remove(RenderComponent.class);
			}
			return;
		}
		
		int interval = (int)((blinkComp.timeElapsed - t) / currentBlink.interval);
		if(interval % 2 == 0 && renderComp == null){
			entity.add(new RenderComponent());
		}
		else if(interval % 2 != 0 && renderComp != null){
			entity.remove(RenderComponent.class);
		}
	}
}
