package com.fullspectrum.ability.monk;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.factory.EntityFactory;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;
import com.fullspectrum.utils.PhysicsUtils;

public class WindBurstAbility extends AnimationAbility{

	private float damage;
	private float knockback;
	private Entity windBurst;
	private Animation windAnim;
	private boolean createdWind = false;
	
	public WindBurstAbility(float cooldown, Actions input, Animation animation, float damage, float knockback) {
		super(AbilityType.WIND_BURST, AssetLoader.getInstance().getRegion(Asset.WIND_BURST_ICON), cooldown, input, animation);
		this.damage = damage;
		this.knockback = knockback;
		windAnim = AssetLoader.getInstance().getAnimation(Asset.MONK_WIND);
	}

	@Override
	protected void init(Entity entity) {
		lockFacing();
		Mappers.esm.get(entity).get(EntityStates.WIND_BURST).changeState(EntityStates.WIND_BURST);
		
		Body body = Mappers.body.get(entity).body;
		
		windBurst = EntityFactory.createWind(body.getPosition().x, body.getPosition().y, windAnim, true, "monk/frames_monk_wind");
	}
	

	@Override
	public void onUpdate(Entity entity, float delta) {
		int frame = (int)(elapsed / GameVars.ANIM_FRAME);
		
		if(frame >= 1 && !createdWind) {
			// put in wind burst
			createdWind = true;
			
			boolean facingRight = Mappers.facing.get(entity).facingRight;
			Mappers.facing.get(windBurst).facingRight = facingRight;

			float width = 3.0f;
			
			Vector2 pos = PhysicsUtils.getPos(entity);
			float x = pos.x;
			float y = pos.y;
			
			x += facingRight ? width : -width;
			y -= GameVars.PPM_INV * 2; // two pixel shift down
			
			Mappers.position.get(windBurst).set(x, y);
			
			EntityManager.addEntity(windBurst);
			
			// Setup a group
			Entity group = EntityFactory.createGroup();
			Mappers.property.get(group).setProperty("hit_entities", new ObjectSet<Entity>());
			Mappers.property.get(group).setProperty("effect", Mappers.monk.get(entity).activeEffect);
			
			
			int numParticles = 5;
			float height = 2.0f;
			float distance = 8.0f;
			float speed = distance / windAnim.getAnimationDuration();
			float angle = facingRight ? 0.0f : 180.0f;
			float xOff = 0.5f;
			
			for(int i = 0; i < numParticles; i++) {
				float xx = pos.x + (facingRight ? xOff : -xOff);
				float yy = pos.y - 0.5f + ((float)i / (float)numParticles) * height;
				
				Entity windParticle = EntityFactory.createWindParticle(group, Mappers.status.get(entity).status, xx, yy, distance, speed, angle, damage, knockback);
				EntityManager.addEntity(windParticle);
			}
			EntityManager.addEntity(group);
		}
	}

	@Override
	protected void destroy(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.WIND_BURST).changeState(EntityStates.IDLING);
		createdWind = false;
	}

}
