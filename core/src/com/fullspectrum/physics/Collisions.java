package com.fullspectrum.physics;


import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.fullspectrum.component.CombustibleComponent;
import com.fullspectrum.component.DamageComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.entity.EntityUtils;
import com.fullspectrum.handlers.DamageHandler;

public enum Collisions {

	DROP {
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			
		}

		@Override
		public void preSolve(Fixture me, Fixture other, Contact contact) {
			Entity coin = (Entity)me.getBody().getUserData();
			Entity entity = (Entity)other.getBody().getUserData();

			if(entity == null || !EntityUtils.isValid(entity)) return;
			contact.setEnabled(false);
			
			if(Mappers.player.get(entity) == null || !Mappers.drop.get(coin).canPickUp) return;
			
			MoneyComponent moneyComp = Mappers.money.get(entity);
			MoneyComponent coinAmount = Mappers.money.get(coin);
			
			moneyComp.money += coinAmount.money;
			coinAmount.money = 0;
			
			Mappers.death.get(coin).triggerDeath();
		}

		@Override
		public void postSolve(Fixture me, Fixture other, Contact contact) {
			
		}
	},
	EXPLOSIVE{
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			
			TypeComponent myType = Mappers.type.get(entity);
			TypeComponent otherType = otherEntity != null ? Mappers.type.get(otherEntity) : null;
			
			if(otherType != null && !myType.shouldCollide(otherType)) return;

			if(!other.isSensor()){
				CombustibleComponent combustibleComp = Mappers.combustible.get(entity);
				combustibleComp.shouldExplode = true;
				return;
			}else if(other.isSensor()){
				return;
			}
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
			
		}

		@Override
		public void preSolve(Fixture me, Fixture other, Contact contact) {
			Entity entity = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			if(otherEntity == null || !EntityUtils.isValid(otherEntity)) return;
			
			if(Mappers.type.get(entity).same(Mappers.type.get(otherEntity))){
				contact.setEnabled(false);
			}
		}

		@Override
		public void postSolve(Fixture me, Fixture other, Contact contact) {
			
		}
	},
	DAMAGE_ON_CONTACT {
		@Override
		public void beginCollision(Fixture me, Fixture other) {
			Entity entity = (Entity)me.getBody().getUserData();
			Entity otherEntity = (Entity)other.getBody().getUserData();
			
			TypeComponent myType = Mappers.type.get(entity);
			TypeComponent otherType = otherEntity != null ? Mappers.type.get(otherEntity) : null;
			
			if(otherType == null || !myType.shouldCollide(otherType)) return;
			
			HealthComponent healthComp = Mappers.heatlh.get(otherEntity);
			if(healthComp == null) return;
			
			DamageComponent damageComp = Mappers.damage.get(entity);
			DamageHandler.dealDamage(otherEntity, damageComp.damage);
		}

		@Override
		public void endCollision(Fixture me, Fixture other) {
		}

		@Override
		public void preSolve(Fixture me, Fixture other, Contact contact) {
			Entity otherEntity = (Entity)other.getBody().getUserData();
			if(otherEntity == null || !EntityUtils.isValid(otherEntity)) return;
			
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
