package com.fullspectrum.ability;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;
import com.fullspectrum.utils.PhysicsUtils;

public class HomingKnivesAbility extends AnimationAbility{

	private int frameRightThrow = 4;
	private int frameLeftThrow = 7;
	private int frameCenterThrow = 11;
	private boolean thrownRight = false;
	private boolean thrownLeft = false;
	private boolean thrownUp = false;
	
	private float clusterDistance = 2.0f;
	private float clusterRadius = 1.0f;
	private int knivesPerCluster;
	
	public HomingKnivesAbility(float cooldown, Actions input, Animation animation, int knivesPerCluster){
		super(AbilityType.HOMING_KNIVES, AssetLoader.getInstance().getRegion(Asset.HOMING_KNIVES_ICON), cooldown, input, animation);
		this.knivesPerCluster = knivesPerCluster;
		setAbilityConstraints(new AbilityConstraints() {
			@Override
			public boolean canUse(Ability ability, Entity entity) {
				return Mappers.collision.get(entity).onGround();
			}
		});
	}

	@Override
	protected void init(Entity entity) {
		Mappers.esm.get(entity).get(EntityStates.HOMING_KNIVES).changeState(EntityStates.HOMING_KNIVES);
	}

	@Override
	public void onUpdate(Entity entity, float delta) {
		int frame = (int)(elapsed / GameVars.ANIM_FRAME);
		
		// Right Throw
		if(frame == frameRightThrow && !thrownRight){
			thrownRight = true;
			throwKnives(entity, 30.0f);
		}
		
		if(frame == frameLeftThrow && !thrownLeft){
			thrownLeft = true;
			throwKnives(entity, 150.0f);
		}
		
		if(frame == frameCenterThrow && !thrownUp){
			thrownUp = true;
			throwKnives(entity, 90.0f);
		}
		
	}
	
	private void throwKnives(Entity entity, float angle){
		Vector2 playerPos = PhysicsUtils.getPos(entity);
		Vector2 clusterCenter = new Vector2();
		
		clusterCenter.add(playerPos);
		clusterCenter.add(clusterDistance * MathUtils.cosDeg(angle), clusterDistance * MathUtils.sinDeg(angle));
	
		float ang1 = 0;
		float ang2 = 0;
		float angInc = 360.0f / (float)knivesPerCluster;
		for(int i = 0; i < knivesPerCluster; i++){
			ang1 = i * angInc;
			ang2 = (i + 1) * angInc;
			
			DebugRender.setType(ShapeType.Line);
			DebugRender.setColor(Color.RED);
			DebugRender.arc(clusterCenter.x, clusterCenter.y, clusterRadius, ang1, ang2 - ang1, 1.0f);
		}
	}
	
	@Override
	protected void destroy(Entity entity) {
		thrownRight = false;
		thrownLeft = false;
		thrownUp = false;
		
		Mappers.esm.get(entity).get(EntityStates.HOMING_KNIVES).changeState(EntityStates.IDLING);
	}
}
