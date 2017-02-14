package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.fullspectrum.component.ASMComponent;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.StateComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.fsm.AnimationStateMachine;

public class AnimationSystem extends IteratingSystem {

	public AnimationSystem() {
		super(Family.all(TextureComponent.class, AnimationComponent.class).one(ASMComponent.class, StateComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		TextureComponent texComp = Mappers.texture.get(entity);
		AnimationComponent animComp = Mappers.animation.get(entity);
		ASMComponent asmComp = Mappers.asm.get(entity);
		StateComponent stateComp = Mappers.state.get(entity);

		if (asmComp != null) {
			for(int i = 0; i < asmComp.size(); i++){
				AnimationStateMachine machine = asmComp.get(i);
				machine.addTime(deltaTime);
				Animation animation = animComp.animations.get(machine.getCurrentAnimation());
				texComp.regions.set(i, animation == null ? null : animation.getKeyFrame(machine.getAnimationTime()));
			}
		}
		else {
			stateComp.time += deltaTime;
			texComp.set(animComp.animations.get(stateComp.state).getKeyFrame(stateComp.time));
		}
	}

}
