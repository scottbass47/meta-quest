package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.fsm.transition.Transition;

public class EntityStateMachine extends StateMachine<StateIdentifier, EntityState> {

	// State
	private EntityState currentState;
	private Entity entity;

	// Animation
	public float animationTime;
	private AnimState currentAnimation;

	public EntityStateMachine(Entity entity) {
		super(entity);
		this.entity = entity;
		this.states = new ArrayMap<StateIdentifier, EntityState>();
	}

	@Override
	public void changeState(StateIdentifier identifier) {
		EntityState newState = states.get(identifier);
		if (newState == currentState) return;
		if (currentState != null) {
			for (Transition t : currentState.getTransitions()) {
				t.getSystem().removeEntity(entity);
			}
			for (Component c : currentState.getComponents()) {
				entity.remove(c.getClass());
			}
		}
		for (Transition t : newState.getTransitions()) {
			t.getSystem().addEntity(entity);
		}
		for (Component c : newState.getComponents()) {
			entity.add(c);
		}
		animationTime = 0;
		currentAnimation = newState.getCurrentAnimation();
		currentState = newState;
	}
}