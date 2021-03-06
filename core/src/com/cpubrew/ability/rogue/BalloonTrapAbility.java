package com.cpubrew.ability.rogue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.ability.AbilityType;
import com.cpubrew.ability.InstantAbility;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.Mappers;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.input.Actions;
import com.cpubrew.utils.PhysicsUtils;

public class BalloonTrapAbility extends InstantAbility {

	private float damagePerPellet;
	private int numPellets;
	private float speed;
	private int maxBalloons;
	private Array<Entity> balloons;
	private int numBalloons = 0;
	
	public BalloonTrapAbility(float cooldown, Actions input, float damagePerPellet, int numPellets, float speed, int maxBalloons){
		super(AbilityType.BALLOON_TRAP, AssetLoader.getInstance().getRegion(Asset.BALLOON_ICON), cooldown, input);
		this.damagePerPellet = damagePerPellet;
		this.numPellets = numPellets;
		this.speed = speed;
		this.maxBalloons = maxBalloons;
		balloons = new Array<Entity>(maxBalloons);
	}

	@Override
	public void onUse(Entity entity) {
		Vector2 pos = PhysicsUtils.getPos(entity);
		if(numBalloons == maxBalloons) {
			Entity balloon = balloons.removeIndex(0);
			Mappers.body.get(balloon).body.setTransform(pos.x, pos.y + 0.5f, 0.0f);
			Mappers.esm.get(balloon).first().changeState(EntityStates.INIT);
			balloons.add(balloon);
		} else {
			Entity balloon = EntityFactory.createBalloonTrap(pos.x, pos.y + 0.5f, numPellets, damagePerPellet, speed, Mappers.status.get(entity).status);
			EntityManager.addEntity(balloon);
			balloons.add(balloon);
			numBalloons++;
		}
	}
	
	public void removeBalloon(Entity balloon) {
		if(balloons.size < 1) return;
		balloons.removeIndex(balloons.indexOf(balloon, false));
		numBalloons--;
	}
	
}
