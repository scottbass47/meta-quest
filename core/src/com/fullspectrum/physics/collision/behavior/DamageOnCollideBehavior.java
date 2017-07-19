package com.fullspectrum.physics.collision.behavior;

import com.badlogic.gdx.physics.box2d.Contact;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.effects.KnockBackDef;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.physics.collision.BodyInfo;

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
