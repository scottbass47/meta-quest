package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;

public class InStateTask extends LeafTask<Entity>{

	private State state;
	private SMType type;
	
	public InStateTask(State state, SMType type) {
		this.state = state;
		this.type = type;
	}

	@Override
	public Status execute() {
		Entity entity = getObject();
		StateMachine<? extends State, ? extends StateObject> machine = null;
		
		switch(type) {
		case AISM:
			machine = Mappers.aism.get(entity).get(state);
			break;
		case ASM:
			machine = Mappers.asm.get(entity).get(state);
			break;
		case ESM:
			machine = Mappers.esm.get(entity).get(state);
			break;
		case FSM:
			machine = Mappers.fsm.get(entity).get(state);
			break;
		default:
			break;
		}
		
		if(machine == null) return Status.FAILED;
		
		return machine.getCurrentState() == state ? Status.SUCCEEDED : Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		InStateTask itask = (InStateTask) task;
		itask.state = state;
		itask.type = type;
		return itask;
	}
	
	public static enum SMType {
		FSM,
		ESM,
		ASM,
		AISM,
	}
	
}
