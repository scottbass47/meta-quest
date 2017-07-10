package com.fullspectrum.ability.knight;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.ability.AnimationAbility;
import com.fullspectrum.ability.OnGroundConstraint;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.StatusComponent;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.effects.Effects;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.handlers.DamageHandler;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.level.LevelHelper;

public class SlamAbility extends AnimationAbility{

	private int frameNum;
	private boolean hasSlammed = false;
	private float range;
	private float damage;
	private float knockback;
	private float stunDuration;	
	
	public SlamAbility(float cooldown, Actions input, Animation<TextureRegion> slamAnimation, int frameNum, float range, float damage, float knockback, float stunDuration) {
		super(AbilityType.SLAM, AssetLoader.getInstance().getRegion(Asset.SLAM_ICON), cooldown, input, slamAnimation);
		this.frameNum = frameNum;
		this.range = range;
		this.damage = damage;
		this.knockback = knockback;
		this.stunDuration = stunDuration;
		setAbilityConstraints(new OnGroundConstraint());
		addTemporaryImmunties(EffectType.KNOCKBACK, EffectType.STUN);
		addTemporaryInvincibilities(InvincibilityType.ALL);
	}

	@Override
	public void init(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.SLAM).changeState(EntityStates.SLAM);
		Mappers.facing.get(entity).locked = true;
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		int currFrame = (int)(elapsed / GameVars.ANIM_FRAME);
		if(!hasSlammed && currFrame >= frameNum - 1){
			// Do Slam
			LevelComponent levelComp = Mappers.level.get(entity);
			LevelHelper helper = levelComp.levelHelper;
			
			Array<Entity> entities = helper.getEntities(new EntityGrabber() {
				@Override
				public boolean validEntity(Entity me, Entity other) {
					// Same type enemies aren't affected
					if(Mappers.status.get(me).same(Mappers.status.get(other))) return false;
					
					Body myBody = Mappers.body.get(me).body;
					Body otherBody = Mappers.body.get(other).body;
					
					float myX = myBody.getPosition().x;
					float myY = myBody.getPosition().y;
					float otherX = otherBody.getPosition().x;
					float otherY = otherBody.getPosition().y;
					
					float yRange = 0.75f;
					
					float x = myX - range;
					float y = myY - yRange;
					float width = 2 * range;
					float height = 2 * yRange;
					
					DebugRender.setType(ShapeType.Line);
					DebugRender.setColor(Color.CYAN);
					DebugRender.rect(x, y, width, height, 1.0f);
					
					return otherX >= x && otherX <= x + width && otherY >= y && otherY <= y + height;
				}
				
				@SuppressWarnings("unchecked")
				@Override
				public Family componentsNeeded() {
					return Family.all(StatusComponent.class, BodyComponent.class, HealthComponent.class).get();
				}
			});
			
			for(Entity hitEntity : entities){
				Body myBody = Mappers.body.get(entity).body;
				Body otherBody = Mappers.body.get(hitEntity).body;
				DamageHandler.dealDamage(entity, hitEntity, damage);
				Effects.giveKnockBack(hitEntity, knockback, myBody.getPosition().x < otherBody.getPosition().x ? 0.0f : 180.0f);
				Effects.giveStun(hitEntity, stunDuration);
			}
			hasSlammed = true;
		}
	}

	@Override
	public void destroy(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.SLAM).changeState(EntityStates.IDLING);
		Mappers.facing.get(entity).locked = false;
		hasSlammed = false;
	}

	

}
