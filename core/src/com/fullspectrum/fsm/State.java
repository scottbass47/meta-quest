package com.fullspectrum.fsm;

import com.fullspectrum.fsm.transition.Tag;

public interface State extends Tag{

	public int numStates();
	public String getName();
	
}
