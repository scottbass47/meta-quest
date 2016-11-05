package com.fullspectrum.entity;

import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.system.AttackSystem;
import com.fullspectrum.fsm.system.DivingSystem;
import com.fullspectrum.fsm.system.FallingSystem;
import com.fullspectrum.fsm.system.IdlingSystem;
import com.fullspectrum.fsm.system.JumpingSystem;
import com.fullspectrum.fsm.system.RunningSystem;
import com.fullspectrum.fsm.system.StateSystem;

public enum EntityStates implements State{

	IDLING {
		@Override
		public StateSystem getStateSystem() {
			return IdlingSystem.getInstance();
		}
	},
	RUNNING {
		@Override
		public StateSystem getStateSystem() {
			return RunningSystem.getInstance();
		}
	},
	JUMPING {
		@Override
		public StateSystem getStateSystem() {
			return JumpingSystem.getInstance();
		}
	},
	FALLING {
		@Override
		public StateSystem getStateSystem() {
			return FallingSystem.getInstance();
		}
	},
	DIVING {
		@Override
		public StateSystem getStateSystem() {
			return DivingSystem.getInstance();
		}
	},
	ATTACK{
		@Override
		public StateSystem getStateSystem() {
			return AttackSystem.getInstance();
		}
	};

	public abstract StateSystem getStateSystem();
	
	@Override
	public int getIndex() {
		return ordinal();
	}

	@Override
	public int numStates() {
		return values().length;
	}
	
	@Override
	public String toString(){
		return name();
	}

	@Override
	public String getName() {
		return name();
	}
	
}
