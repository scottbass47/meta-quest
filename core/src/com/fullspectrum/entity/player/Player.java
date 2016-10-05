package com.fullspectrum.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

public class Player implements Disposable {

	// Animation
	public static ArrayMap<PlayerAnim, Animation> animations;
	private final static float ANIM_SPEED = 0.1f;
	private static TextureAtlas knightAtlas;

	static {
		knightAtlas = new TextureAtlas(Gdx.files.internal("sprites/knight_anim.atlas"));
		animations = new ArrayMap<PlayerAnim, Animation>();
		animations.put(PlayerAnim.IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_idling"), PlayMode.LOOP));
		animations.put(PlayerAnim.RANDOM_IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_idling"), PlayMode.LOOP));
		animations.put(PlayerAnim.RUNNING, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_walking"), PlayMode.LOOP));
		animations.put(PlayerAnim.RISE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_rising"), PlayMode.LOOP));
		animations.put(PlayerAnim.JUMP, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_jump"), PlayMode.LOOP));
		animations.put(PlayerAnim.FALLING, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_falling"), PlayMode.LOOP));
		animations.put(PlayerAnim.JUMP_APEX, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_jump_apex"), PlayMode.LOOP));
	}
	
	@Override
	public void dispose() {
		knightAtlas.dispose();
	}

}
