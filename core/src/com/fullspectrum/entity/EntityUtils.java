package com.fullspectrum.entity;

import com.badlogic.ashley.core.Entity;

public class EntityUtils {
	
	public static final int VALID = 0x1;
	public static boolean engineUpdating = false;

	private EntityUtils(){}
	
	public static boolean isValid(Entity entity){
		if(entity == null) return false;
		return (entity.flags & VALID) == VALID;
	}
	
	public static void setValid(Entity entity, boolean valid){
		if(valid){
			entity.flags |= VALID;
		}
		else{
			entity.flags &= ~VALID;
		}
	}
}
