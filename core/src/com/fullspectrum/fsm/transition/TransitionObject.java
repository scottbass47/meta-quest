package com.fullspectrum.fsm.transition;

public class TransitionObject {

	public Transition transition;
	public TransitionData data;
	
	public TransitionObject(Transition t, TransitionData data){
		this.transition = t;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return transition.toString() + ", " + (data == null ? "" : data.toString());
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + transition.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
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