package com.fullspectrum.fsm;

public interface StateCreator<T extends StateObject> {

	public T getInstance();
	
}