package com.fullspectrum.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.entity.EntityAnim;

public class PlayerAssets {

	// Animation
	public static ArrayMap<EntityAnim, Animation> animations;
	private final static float ANIM_SPEED = 0.1f;
	private static TextureAtlas knightAtlas;

	static {
		knightAtlas = new TextureAtlas(Gdx.files.internal("sprites/knight_anim.atlas"));
		animations = new ArrayMap<EntityAnim, Animation>();
		animations.put(EntityAnim.IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_idling"), PlayMode.LOOP));
		animations.put(EntityAnim.RANDOM_IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_idling"), PlayMode.LOOP));
		animations.put(EntityAnim.RUNNING, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_walking"), PlayMode.LOOP));
		animations.put(EntityAnim.RISE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_rising"), PlayMode.LOOP));
		animations.put(EntityAnim.JUMP, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_jump"), PlayMode.LOOP));
		animations.put(EntityAnim.FALLING, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_falling"), PlayMode.LOOP));
		animations.put(EntityAnim.JUMP_APEX, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_jump_apex"), PlayMode.LOOP));
	}
	
	public static void dispose() {
		knightAtlas.dispose();
	}

}
