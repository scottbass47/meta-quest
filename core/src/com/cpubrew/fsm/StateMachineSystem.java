package com.cpubrew.fsm;

import java.util.Iterator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.component.AISMComponent;
import com.cpubrew.component.ASMComponent;
import com.cpubrew.component.ESMComponent;
import com.cpubrew.component.FSMComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.fsm.transition.TransitionObject;
import com.cpubrew.fsm.transition.TransitionSystem;
import com.cpubrew.utils.EntityUtils;

public class StateMachineSystem extends TransitionSystem {

	private static StateMachineSystem instance;
	private ObjectSet<StateMachine<? extends State, ? extends StateObject>> toRemove;
	private ObjectSet<StateMachine<? extends State, ? extends StateObject>> toAdd;

	private StateMachineSystem() {
		toRemove = new ObjectSet<StateMachine<? extends State, ? extends StateObject>>();
		toAdd = new ObjectSet<StateMachine<? extends State, ? extends StateObject>>();
	}

	public static StateMachineSystem getInstance() {
		if (instance == null) {
			instance = new StateMachineSystem();
		}
		return instance;
	}

	@Override
	public void update(float deltaTime) {
		// CLEANUP HACKY
		EntityUtils.engineUpdating = false;
		for (StateMachine<? extends State, ? extends StateObject> machine : machines) {
			if (machine.getCurrentStateObject() == null) continue;
			for (TransitionObject obj : machine.getCurrentStateObject().getAllTransitionObjects()) {
				if (obj.transition.shouldTransition(machine.getEntity(), obj, deltaTime)) {
					if (machine.changeState(obj)) break;
				}
			}
			machine.resetMultiTransitions();
		}
		updateMachines();
		EntityUtils.engineUpdating = true;
	}

	/**
	 * Adds / Removes machines sitting in the queue. 
	 */
	public void updateMachines() {
		// Only add/remove after all machines have updated
		for (Iterator<StateMachine<? extends State, ? extends StateObject>> iter = toRemove.iterator(); iter.hasNext();) {
			StateMachine<? extends State, ? extends StateObject> machine = iter.next();
			machines.remove(machine);
			iter.remove();
		}
		for (Iterator<StateMachine<? extends State, ? extends StateObject>> iter = toAdd.iterator(); iter.hasNext();) {
			StateMachine<? extends State, ? extends StateObject> machine = iter.next();
			machines.add(machine);
			iter.remove();
		}
	}

	@Override
	public void addStateMachine(StateMachine<? extends State, ? extends StateObject> machine) {
		addToEntity(machine);
		toAdd.add(machine);
	}

	public void addStateMachines(Array<StateMachine<? extends State, ? extends StateObject>> machines) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines)
			addStateMachine(machine);
	}

	@Override
	public void removeStateMachine(StateMachine<? extends State, ? extends StateObject> machine) {
		removeFromEntity(machine);
		if (toAdd.contains(machine)) {
			toAdd.remove(machine);
			return;
		}
		toRemove.add(machine);
	}

	public void removeStateMachines(Array<StateMachine<? extends State, ? extends StateObject>> machines) {
		for (StateMachine<? extends State, ? extends StateObject> machine : machines)
			removeStateMachine(machine);
	}

	// CLEANUP Probably shouldn't be else/if spamming
	private void addToEntity(StateMachine<? extends State, ? extends StateObject> machine) {
		Entity entity = machine.getEntity();
		Engine engine = Mappers.engine.get(entity).engine;

		// Lazy creation
		if (machine instanceof AnimationStateMachine) {
			if (Mappers.asm.get(entity) == null) {
				entity.add(engine.createComponent(ASMComponent.class));
			}
			Mappers.asm.get(entity).add((AnimationStateMachine) machine);
		} else if (machine instanceof AIStateMachine) {
			if (Mappers.aism.get(entity) == null) {
				entity.add(engine.createComponent(AISMComponent.class));
			}
			Mappers.aism.get(entity).add((AIStateMachine) machine);
		} else if (machine instanceof EntityStateMachine) {
			if (Mappers.esm.get(entity) == null) {
				entity.add(engine.createComponent(ESMComponent.class));
			}
			Mappers.esm.get(entity).add((EntityStateMachine) machine);
		} else {
			if (Mappers.fsm.get(entity) == null) {
				entity.add(engine.createComponent(FSMComponent.class));
			}
			Mappers.fsm.get(entity).add(machine);
		}
	}

	private void removeFromEntity(StateMachine<? extends State, ? extends StateObject> machine) {
		Entity entity = machine.getEntity();
		if (machine instanceof AnimationStateMachine) {
			Mappers.asm.get(entity).remove((AnimationStateMachine) machine);
		} else if (machine instanceof AIStateMachine) {
			Mappers.aism.get(entity).remove((AIStateMachine) machine);
		} else if (machine instanceof EntityStateMachine) {
			Mappers.esm.get(entity).remove((EntityStateMachine) machine);
		} else {
			Mappers.fsm.get(entity).remove(machine);
		}
	}

}
