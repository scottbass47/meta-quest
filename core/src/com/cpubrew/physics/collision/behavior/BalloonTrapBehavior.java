package com.cpubrew.physics.collision.behavior;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.rogue.BalloonTrapAbility;
import com.cpubrew.component.Mappers;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.entity.EntityStatus;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.physics.collision.BodyInfo;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.PhysicsUtils;

public class BalloonTrapBehavior extends CollisionBehavior{

	private boolean spawnedBullets = false;
	private EntityStatus type;
	private int numPellets;
	private float damagePerPellet;
	private float speed;
	
	public BalloonTrapBehavior(EntityStatus type, int numPellets, float damagePerPellet, float speed) {
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
	public void endCollision(BodyInfo me, BodyInfo other, Contact contact) {
		spawnPellets(me.getEntity());
	}

}
