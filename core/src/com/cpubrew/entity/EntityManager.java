package com.cpubrew.entity;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.DeathComponent;
import com.cpubrew.component.EngineComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.WorldComponent;
import com.cpubrew.effects.Effect;
import com.cpubrew.fsm.State;
import com.cpubrew.fsm.StateChangeDef;
import com.cpubrew.fsm.StateMachine;
import com.cpubrew.fsm.StateObject;
import com.cpubrew.physics.BodyBuilder;
import com.cpubrew.physics.PhysicsDef;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.PhysicsUtils;

public class EntityManager {

	private static Array<Entity> toDie = new Array<Entity>();
	private static Array<Entity> toAdd = new Array<Entity>();
	private static Array<PhysicsDef> toLoadPhysics = new Array<PhysicsDef>();
	private static Array<BodyBuilder> bodyDefinitions = new Array<BodyBuilder>();
	private static Array<StateChangeDef> stateChanges = new Array<StateChangeDef>();
	private static Array<Effect> effects = new Array<Effect>();
	private static Array<DelayedAction> actions = new Array<DelayedAction>();
	
	public static void cleanUp(Entity entity) {
		EngineComponent engineComp = Mappers.engine.get(entity);
		WorldComponent worldComp = Mappers.world.get(entity);
		Engine engine = engineComp.engine;

		BodyComponent bodyComp = Mappers.body.get(entity);

		if (bodyComp != null && bodyComp.body != null && worldComp != null && worldComp.world != null) {
			worldComp.world.destroyBody(bodyComp.body);
		}
		for (Component c : entity.getComponents()) {
			if (c instanceof Poolable) {
				((Poolable) c).reset();
			}
		}
		if (engine != null) {
			engine.removeEntity(entity);
		}
	}
	
	public static void addEntity(Entity entity){
		toAdd.add(entity);
	}
	
	public static void addPhysicsLoad(PhysicsDef def){
		toLoadPhysics.add(def);
	}
	
	public static void addBodyDefinition(BodyBuilder builder) {
		bodyDefinitions.add(builder);
	}
	
	public static void sendToDie(Entity entity){
		toDie.add(entity);
	}
	
	public static void addStateChange(StateMachine<? extends State, ? extends StateObject> machine, State state){
		stateChanges.add(new StateChangeDef(machine, state));
	}
	
	public static void addEffect(Effect effect){
		effects.add(effect);
	}
	
	public static void addDelayedAction(DelayedAction action){
		actions.add(action);
	}
	
	public static void update(float delta){
		// Delayed death
		for(Iterator<Entity> iter = toDie.iterator(); iter.hasNext();){
			Entity entity = iter.next();
			DeathComponent deathComp = Mappers.death.get(entity);
			deathComp.triggerDeath();
			iter.remove();
		}
		
		// Delayed physics loading
		for(Iterator<PhysicsDef> iter = toLoadPhysics.iterator(); iter.hasNext();){
			PhysicsDef def = iter.next();
			Entity entity = def.getEntity();
			entity.add(Mappers.engine.get(entity).engine.createComponent(BodyComponent.class).set(PhysicsUtils.createPhysicsBody(def)));
			iter.remove();
		}
		
		for(Iterator<BodyBuilder> iter = bodyDefinitions.iterator(); iter.hasNext();){
			BodyBuilder def = iter.next();
			Entity entity = def.getEntity();
			entity.add(Mappers.engine.get(entity).engine.createComponent(BodyComponent.class).set(def.build()));
			iter.remove();
		}
		
		// Delayed adding
		for(Iterator<Entity> iter = toAdd.iterator(); iter.hasNext();){
			Entity entity = iter.next();
			Mappers.engine.get(entity).engine.addEntity(entity);
			iter.remove();
		}
		
		// Delayed state changes
		for(Iterator<StateChangeDef> iter = stateChanges.iterator(); iter.hasNext();){
			StateChangeDef def = iter.next();
			if(EntityUtils.isValid(def.getMachine().getEntity())) def.getMachine().changeState(def.getState());
			iter.remove();
		}
		
		// Delayed effects
		for(Iterator<Effect> iter = effects.iterator(); iter.hasNext();){
			Effect effect = iter.next();
			if(EntityUtils.isValid(effect.getEntity())){
				if(effect.apply()){
					Mappers.effect.get(effect.getEntity()).add(effect);
				}
			}
			iter.remove();
		}
		
		// Delayed actions
		for(Iterator<DelayedAction> iter = actions.iterator(); iter.hasNext();){
			DelayedAction action = iter.next();
			if(EntityUtils.isValid(action.getEntity())) action.onAction();
			iter.remove();
		}
	}
	

}
