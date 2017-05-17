package com.fullspectrum.physics.collision.behavior;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.physics.collision.BodyInfo;

public class SpawnExplosionBehavior extends CollisionBehavior{

	private float radius;
	private float damage;
	private float knockback;
	
	public SpawnExplosionBehavior(float radius, float damage, float knockback) {
		this.radius = radius;
		this.damage = damage;
		this.knockback = knockback;
	}

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		BodyComponent bodyComp = Mappers.body.get(me.getEntity());
		Body body = bodyComp.body;
		Vector2 pos = body.getPosition();
		
		Entity explosion = EntityFactory.createExplosion(
				pos.x, pos.y, 
				radius, damage, knockback, 
				me.getEntityStatus());
		EntityManager.addEntity(explosion);
	}

}
