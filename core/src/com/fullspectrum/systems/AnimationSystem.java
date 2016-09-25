package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.StateComponent;
import com.fullspectrum.component.TextureComponent;

public class AnimationSystem extends IteratingSystem{

	@SuppressWarnings("unchecked")
	public AnimationSystem(){
		super(Family.all(TextureComponent.class, AnimationComponent.class).one(FSMComponent.class, StateComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TextureComponent texComp = Mappers.texture.get(entity);
		AnimationComponent animComp = Mappers.animation.get(entity);
		FSMComponent fsmComp = Mappers.fsm.get(entity);
		StateComponent stateComp = Mappers.state.get(entity);
		
		if(fsmComp != null){
			fsmComp.fsm.addAnimationTime(deltaTime);
			texComp.region = animComp.animations.get(fsmComp.fsm.getAnimation()).getKeyFrame(fsmComp.fsm.getAnimationTime());
		}
		else{
			stateComp.time += deltaTime;
			texComp.region = animComp.animations.get(stateComp.state).getKeyFrame(stateComp.time);
		}
	}
	
}
