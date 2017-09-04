package com.cpubrew.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.cpubrew.component.Mappers;
import com.cpubrew.effects.KnockBackDef;
import com.cpubrew.handlers.DamageHandler;
import com.cpubrew.physics.collision.BodyInfo;

public class DamageOnCollideBehavior extends CollisionBehavior{

	private KnockBackDef knockBackDef;
	
	public DamageOnCollideBehavior() {
		this(null);
	}
	
	public DamageOnCollideBehavior(KnockBackDef kdef) {
		preSolveType = PreSolveType.DISABLE_CONTACT;
		knockBackDef = kdef;
	}
	
	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
		if(knockBackDef != null) {
			knockBackDef.give(other.getEntity());
		}
		DamageHandler.dealDamage(me.getEntity(), other.getEntity(), Mappers.damage.get(me.getEntity()) == null ? 0.0f : Mappers.damage.get(me.getEntity()).damage);
	}
}
