package com.cpubrew.fsm;

import com.badlogic.gdx.utils.Bits;

public class MultiTransitionDef extends AbstractDef{

	private MultiTransition multi;

	public MultiTransitionDef(Bits all, Bits one, Bits exclude, State toState, MultiTransition multi) {
		super(all, one, exclude, toState);
		this.multi = multi;
	}
	
	public MultiTransition getMultiTransition(){
		return multi;
	}
}