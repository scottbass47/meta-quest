package com.cpubrew.fsm;

import com.cpubrew.fsm.transition.Tag;

public interface State extends Tag{

	public int numStates();
	public String getName();
	
}
