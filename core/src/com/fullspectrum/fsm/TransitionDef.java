package com.fullspectrum.fsm;

import com.badlogic.gdx.utils.Bits;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionData;

public class TransitionDef extends AbstractDef{
	private Transition transition;
	private TransitionData data;

	public TransitionDef(Bits all, Bits one, Bits exclude, State toState, Transition transition, TransitionData data) {
		super(all, one, exclude, toState);
		this.transition = transition;
		this.data = data;
	}
	
	public Transition getTransition(){
		return transition;
	}
	
	public TransitionData getData(){
		return data;
	}
}