package com.fullspectrum.physics.collision.behavior;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.rogue.BalloonTrapAbility;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.physics.collision.BodyInfo;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class BalloonTrapBehavior implements CollisionBehavior{

	private boolean spawnedBullets = false;
	private EntityType type;
	private int numPellets;
	private float damagePerPellet;
	private float speed;
	
	public BalloonTrapBehavior(EntityType type, int numPellets, float damagePerPellet, float speed) {
		this.type = type;
		this.numPellets = numPellets;
		this.damagePerPellet = damagePerPellet;
		this.speed = speed;
	}
	
	private void spawnPellets(Entity entity){
		if(spawnedBullets) return;
		float angInc = 360.0f / numPellets;
		float ang1;
		float ang2;
		for(int i = 0; i < numPellets; i++){
			ang1 = i * angInc;
			ang2 = (i + 1) * angInc;
			Vector2 pos = PhysicsUtils.getPos(entity);
			Entity pellet = EntityFactory.createBalloonPellet(pos.x, pos.y, speed, MathUtils.random(ang1, ang2), damagePerPellet, type);
			EntityManager.addEntity(pellet);
		}
		Entity player = EntityUtils.getPlayer();
		BalloonTrapAbility balloonAbility = (BalloonTrapAbility) Mappers.ability.get(player).getAbility(AbilityType.BALLOON_TRAP);
		if(balloonAbility != null) balloonAbility.removeBalloon(entity);
		spawnedBullets = true;
	}
	
	@Override
	public void beginCollision(BodyInfo me, BodyInfo other, Contact contact) {
	}

	@Override
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		spawnPellets(me.getEntity());
	}

	@Override
	public void preSolveCollision(BodyInfo me, BodyInfo other, Contact contact, Manifold manifold) {
	}

	@Override
	public void postSolveCollision(BodyInfo me, BodyInfo other, Contact contact, ContactImpulse impulse) {
	}

}
