package com.fullspectrum.fsm;

import com.badlogic.gdx.utils.Bits;

public abstract class AbstractDef {

	protected Bits all;
	protected Bits one;
	protected Bits exclude;
	protected State toState;

	public AbstractDef(Bits all, Bits one, Bits exclude, State toState) {
		// Copy over
		this.all = new Bits();
		this.all.or(all);
		this.one = new Bits();
		this.one.or(one);
		this.exclude = new Bits();
		this.exclude.or(exclude);
		this.toState = toState;
	}

	public boolean matches(StateObject stateObj) {
		if (!stateObj.bits.containsAll(all)) {
			return false;
		}
		if (!one.isEmpty() && !one.intersects(stateObj.bits)) {
			return false;
		}
		if (!exclude.isEmpty() && exclude.intersects(stateObj.bits)) {
			return false;
		}
		return true;
	}
	
}
