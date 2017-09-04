package com.cpubrew.physics.collision.behavior;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.PropertyComponent;
import com.cpubrew.effects.EffectDef;
import com.cpubrew.handlers.DamageHandler;
import com.cpubrew.physics.collision.BodyInfo;

public class WindParticleBehavior extends CollisionBehavior{

	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		Entity particle = me.getEntity();
		
		PropertyComponent propertyComp = Mappers.property.get(particle);
		boolean facingRight = MathUtils.isEqual(Mappers.projectile.get(particle).angle, 0.0f, 1.0f); 
		
		Entity group = Mappers.parent.get(particle).parent;
		PropertyComponent globalProperties = Mappers.property.get(group);
		
		@SuppressWarnings("unchecked")
		ObjectSet<Entity> hitEntities = (ObjectSet<Entity>) globalProperties.getObject("hit_entities");
		
		if(hitEntities.contains(other.getEntity())) return;
		hitEntities.add(other.getEntity());
		
		if(globalProperties.getObject("effect") != null) {
			EffectDef def = (EffectDef) globalProperties.getObject("effect");
			def.give(other.getEntity());
		}
		
		DamageHandler.dealDamage(particle, other.getEntity(), propertyComp.getFloat("damage"), propertyComp.getFloat("knockback"), facingRight ? 0.0f : 180.0f);
	}
	
}
