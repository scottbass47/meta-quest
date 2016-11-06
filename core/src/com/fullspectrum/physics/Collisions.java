package com.fullspectrum.physics;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.entity.EntityUtils;

public enum Collisions {

	COIN {
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)other.getBody().getUserData();
			if(entity == null || !EntityUtils.isValid(entity)) return;
			
			MoneyComponent moneyComp = Mappers.money.get(entity);
			TypeComponent typeComp = Mappers.type.get(entity);
			
			// Only entities that are type friendly can pick up currency
			if(moneyComp == null || typeComp == null || typeComp.type != EntityType.FRIENDLY)return;
			
			Entity coin = (Entity)me.getBody().getUserData();
			MoneyComponent coinAmount = Mappers.money.get(coin);
			
			moneyComp.money += coinAmount.money;
			coin.add(new RemoveComponent());
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			
		}

		@Override
		public void preSolve(Fixture me, Fixture other, Contact contact) {
			Entity entity = (Entity)other.getBody().getUserData();
			if(entity == null || !EntityUtils.isValid(entity)) return;
			
			TypeComponent typeComp = Mappers.type.get(entity);
			
			// Only entities that are type friendly can pick up currency
			if(typeComp == null || typeComp.type == EntityType.FRIENDLY)return;
			contact.setEnabled(false);
		}

		@Override
		public void postSolve(Fixture me, Fixture other, Contact contact) {
			
		}
	};
	
	
	public abstract void beginCollision(Fixture me, Fixture other);
	public abstract void endCollision(Fixture me, Fixture other);
	public abstract void preSolve(Fixture me, Fixture other, Contact contact);
	public abstract void postSolve(Fixture me, Fixture other, Contact contact);
	
	public static Collisions get(String name){
		for(Collisions collision : Collisions.values()){
			if(collision.name().equalsIgnoreCase(name)) return collision;
		}
		return null;
	}
	
}
