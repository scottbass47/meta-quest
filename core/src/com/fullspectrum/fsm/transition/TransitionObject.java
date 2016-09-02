package com.fullspectrum.fsm.transition;

public class TransitionObject {

	public Transition transition;
	public Object data;
	
	public TransitionObject(Transition t, Object data){
		this.transition = t;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return transition.toString() + ", " + (data == null ? "null" : data.toString());
	}
	
	@Override
	public int hashCode() {
		return transition.ordinal() * 26 + data.hashCode() * 45;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TransitionObject)) return false;
		TransitionObject other = (TransitionObject)obj;
		if(!transition.equals(other.transition)) return false;
		if(data == null || other.data == null) return data == null && other.data == null;
		return data.equals(other.data);
	}
}