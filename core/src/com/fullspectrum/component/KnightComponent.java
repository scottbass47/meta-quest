package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fullspectrum.entity.EntityAnim;

public class KnightComponent implements Component, Poolable{

	// ORDER MATTERS!!!
	private Array<EntityAnim> attacks;
	public boolean first = true;
	public int index = 0;
	
	// Chaining
	public float lungeX;
	public float lungeY;
	
	
	public KnightComponent(){
		attacks = new Array<EntityAnim>();
	}
	
	/**
	 * ORDER MATTERS!!!
	 * @param attack
	 * @return
	 */
	public KnightComponent addAttack(EntityAnim attack){
		if(attacks.contains(attack, false)) throw new IllegalArgumentException("Duplicate attacks.");
		attacks.add(attack);
		return this;
	}
	
	public int numAttacks(){
		return attacks.size;
	}
	
	public EntityAnim getAnim(int index){
		return attacks.get(index);
	}
	
	public EntityAnim getCurrentAnim(){
		return attacks.get(index);
	}
	
	public Array<EntityAnim> getAttacks(){
		return attacks;
	}
	
	@Override
	public void reset() {
		attacks = null;
		first = true;
		index = 0;
	}

}
