package com.fullspectrum.fsm.transition;

public class TransitionObject {

	public Transition transition;
	public Object data;
	
	public TransitionObject(Transition t, Object data){
		this.transition = t;
		this.data = data;
	}
}