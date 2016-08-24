package com.fullspectrum.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.component.GraphicsComponent;
import com.fullspectrum.component.PhysicsComponent;
import com.fullspectrum.entity.Entity;

public class PlayerGraphicsComponent extends GraphicsComponent {

	// Animation
	private ArrayMap<PlayerAnim, Animation> animations;
	private final static float ANIM_SPEED = 0.1f;
	private IPlayerState playerState;
	private Animation currentAnimation;
	private float frameTime = 0.0f;
	private TextureAtlas knightAtlas;
	protected boolean facingRight = true;

	public PlayerGraphicsComponent(PhysicsComponent physics, Player player) {
		super(physics);
		playerState = new IdleState();
		knightAtlas = new TextureAtlas(Gdx.files.internal("sprites/knight_anim.atlas"));
		animations = new ArrayMap<PlayerAnim, Animation>();
		animations.put(PlayerAnim.IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_idle"), PlayMode.NORMAL));
		animations.put(PlayerAnim.RANDOM_IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_randomidle"), PlayMode.NORMAL));
		animations.put(PlayerAnim.RUNNING, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_runcycle"), PlayMode.NORMAL));
		animations.put(PlayerAnim.RISE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_rise"), PlayMode.NORMAL));
		animations.put(PlayerAnim.JUMP, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_jump"), PlayMode.NORMAL));
		currentAnimation = animations.get(PlayerAnim.IDLE);
	}
	
	@Override
	public void init() {
	}
	
	protected void setAnimation(PlayerAnim playerAnim) {
		frameTime = 0;
		currentAnimation = animations.get(playerAnim);
	}

	@Override
	public void update(float delta, Entity entity) {
		frameTime += delta;
		playerState.update(this);
		if (frameTime > currentAnimation.getAnimationDuration()) {
			frameTime = 0;
			playerState.animFinished(this);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
	}


}
