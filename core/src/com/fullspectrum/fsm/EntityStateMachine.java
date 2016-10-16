package com.fullspectrum.fsm;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.utils.PhysicsUtils;

public class EntityStateMachine extends StateMachine<EntityStates, EntityState> {

	// Physics
	private String bodyPath;
	
	public EntityStateMachine(Entity entity, String bodyPath) {
		super(entity, new EntityStateCreator(), EntityStates.class, EntityState.class);
		this.states = new ArrayMap<EntityStates, EntityState>();
		this.bodyPath = bodyPath;
		
		// Setup Physics
		PositionComponent posComp = Mappers.position.get(entity);
		BodyComponent bodyComp = Mappers.body.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		assert bodyComp != null && posComp != null && worldComp != null : "Component can't be null.";
		bodyComp.body = PhysicsUtils.createPhysicsBody(Gdx.files.internal(bodyPath), worldComp.world, new Vector2(posComp.x, posComp.y), false);
	}
	
	@Override
	public EntityState createState(EntityStates key) {
		EntityState state = super.createState(key);
		state.fixtures = PhysicsUtils.getEntityFixtures(Gdx.files.internal(bodyPath), key);
		return state;
	}
	
	@Override
	public void changeState(State identifier) {
		if(!(identifier instanceof EntityStates)) throw new IllegalArgumentException("Invalid input. Must be of type EntityStates.");
		EntityState currState = currentState;
		super.changeState(identifier);
		EntityStates state = (EntityStates)identifier;
		EntityState newState = states.get(state);
		if (newState == currState) return;
		if (currState != null) {
			for (Component c : currState.getComponents()) {
				entity.remove(c.getClass());
			}
			states.getKey(currState, false).getStateSystem().removeEntity(entity);
		}
		for (Component c : newState.getComponents()) {
			entity.add(c);
		}
		if(states.getKey(newState, false) == EntityStates.RUNNING){
			System.out.println();
		}
		states.getKey(newState, false).getStateSystem().addEntity(entity);
		if(currState == null){
			changeBody(newState);
			return;
		}
		if(!newState.fixtures.equals(currState.fixtures)){
			changeBody(newState);
		}
	}
	
	private void changeBody(EntityState state){
//		Gdx.app.debug("Entity State Machine", "changing body.");
		BodyComponent bodyComp = Mappers.body.get(entity);
		Body body = bodyComp.body;
		for(Fixture fixture : body.getFixtureList()){
			body.destroyFixture(fixture);
		}
		for(FixtureDef fdef : state.fixtures.getFixtures()){
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