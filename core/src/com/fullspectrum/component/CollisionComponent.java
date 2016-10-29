package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;

public class CollisionComponent implements Component{

	public int bottomContacts = 0;
	public int topContacts = 0;
	public int rightContacts = 0;
	public int leftContacts = 0;
	
	public boolean onGround(){
		return bottomContacts > 0;
	}
	
	public boolean hittingCeiling(){
		return topContacts > 0;
	}
	
	public boolean onRightWall(){
		return rightContacts > 0;
	}
	
	public boolean onLeftWall(){
		return leftContacts > 0;
	}
}
