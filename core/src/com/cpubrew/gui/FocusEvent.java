package com.cpubrew.gui;

public class FocusEvent {

	private Component other;
	
	public FocusEvent(Component other) {
		this.other = other;
	}
	
	/**
	 * Returns the other component involved in the focus event (i.e. the one that lost / gained focus)
	 * @return
	 */
	public Component getOther() {
		return other;
	}
	
}