package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class MoneyComponent implements Component, Poolable{

	public int money = 0;
	
	public MoneyComponent set(int money){
		this.money = money;
		return this;
	}
	
	@Override
	public void reset() {
		money = 0;
	}

}
