package com.cpubrew.fsm;

public class StateChangeDef {

	private StateMachine<? extends State, ? extends StateObject> machine;
	private State state;
	
	public StateChangeDef(StateMachine<? extends State, ? extends StateObject> machine, State state) {
		this.machine = machine;
		this.state = state;
	}
	
	public StateMachine<? extends State, ? extends StateObject> getMachine() {
		return machine;
	}
	
	public State getState() {
		return state;
	}
	
}
