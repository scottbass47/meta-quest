package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.physics.EntityFixtures;

public class EntityStateMachine extends StateMachine<State, EntityState> {

	private ArrayMap<State, EntityFixtures> fixtures;
	private EntityFixtures currentFixtures;
	
	public EntityStateMachine(Entity entity) {
		super(entity, new EntityStateCreator());
		this.states = new ArrayMap<State, EntityState>();
		fixtures = new ArrayMap<State, EntityFixtures>();
	}
	
	@Override
	public void changeState(State identifier) {
		EntityState currState = currentState;
		super.changeState(identifier);
		EntityState newState = states.get(identifier);
		if (newState == currState) return;
		if (currState != null) {
			for (Component c : currState.getComponents()) {
				entity.remove(c.getClass());
			}
		}
		for (Component c : newState.getComponents()) {
			entity.add(c);
		}
		if(!fixtures.get(identifier).equals(currentFixtures)){
			currentFixtures = fixtures.get(identifier);
			changeBody();
		}
	}
	
	public void setFixture(State state, EntityFixtures fixtures){
		this.fixtures.put(state, fixtures);
	}
	
	private void changeBody(){
//		Gdx.app.debug("Entity State Machine", "changing body.");
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		for(Fixture fixture : body.getFixtureList()){
			body.destroyFixture(fixture);
		}
		for(FixtureDef fdef : currentFixtures.getFixtures()){
			body.createFixture(fdef);
		}
	}
	
	public float getAnimationTime(){
		return currentState.getAnimationTime();
	}
	
	public void addAnimationTime(float time){
		currentState.addAnimationTime(time);
	}
	
	public State getAnimation(){
		return currentState.getCurrentAnimation();
	}
	
	@Override
	public EntityState getCurrentState() {
		return currentState;
	}
}