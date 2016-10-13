package com.fullspectrum.entity.enemy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.entity.EntityAnim;

public class GoblinAssets {

	// Animation
	public static ArrayMap<EntityAnim, Animation> animations;
	private final static float ANIM_SPEED = 0.1f;
	private static TextureAtlas goblinAtlas;

	static {
		goblinAtlas = new TextureAtlas(Gdx.files.internal("sprites/knight_anim.atlas"));
		animations = new ArrayMap<EntityAnim, Animation>();
		animations.put(EntityAnim.IDLE, new Animation(ANIM_SPEED, goblinAtlas.findRegions("goblin_idling"), PlayMode.LOOP));
		animations.put(EntityAnim.RUNNING, new Animation(ANIM_SPEED, goblinAtlas.findRegions("goblin_walking"), PlayMode.LOOP));
//		animations.put(EntityAnim.RISE, new Animation(ANIM_SPEED, goblinAtlas.findRegions("knight_rising"), PlayMode.LOOP));
//		animations.put(EntityAnim.JUMP, new Animation(ANIM_SPEED, goblinAtlas.findRegions("knight_jump"), PlayMode.LOOP));
//		animations.put(EntityAnim.FALLING, new Animation(ANIM_SPEED, goblinAtlas.findRegions("knight_falling"), PlayMode.LOOP));
//		animations.put(EntityAnim.JUMP_APEX, new Animation(ANIM_SPEED, goblinAtlas.findRegions("knight_jump_apex"), PlayMode.LOOP));
	}
	
	public static void dispose() {
		goblinAtlas.dispose();
	}

}
