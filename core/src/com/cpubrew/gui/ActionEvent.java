package com.cpubrew.gui;

public class ActionEvent {

	private Object source;
	
	public ActionEvent() {
	}

	public ActionEvent(Object source) {
		this.source = source;
	}
	
	public Object getSource() {
		return source;
	}
	
	public void setSource(Object source) {
		this.source = source;
	}
	
}
