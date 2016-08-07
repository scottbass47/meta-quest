package com.fullspectrum.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ArrayMap;

public class Player {

	// Animation
	private ArrayMap<PlayerAnim, Animation> animations;
	private final static float ANIM_SPEED = 0.1f;
	private IPlayerState playerState;
	private Animation currentAnimation;
	private float frameTime = 0.0f;
	
	// Position and Velocity
	protected float x;
	protected float y;
	protected float dx;
	protected float dy;
	protected boolean facingRight = true;
	public final static float SPEED = 90.0f;
	
	public Player(){
		init();
	}
	
	private void init(){
		playerState = new IdleState();
		TextureAtlas knightAtlas = new TextureAtlas(Gdx.files.internal("sprites/knight_anim.atlas"));
		animations = new ArrayMap<PlayerAnim, Animation>();
		for(TextureRegion tr : knightAtlas.getRegions()){
			tr.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		}
		animations.put(PlayerAnim.IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knightrandomidle"), PlayMode.LOOP));
		animations.put(PlayerAnim.RUNNING, new Animation(ANIM_SPEED, knightAtlas.findRegions("knightruncycle"), PlayMode.LOOP));
		currentAnimation = animations.get(PlayerAnim.IDLE);
		
		x = 500;
		y = 300;
	}
	
	protected void setAnimation(PlayerAnim playerAnim){
		frameTime = 0;
		currentAnimation = animations.get(playerAnim);
	}
	
	public void update(float delta){
		frameTime += delta;
		x += dx * delta;
		y += dy * delta;
	}
	
	public void render(SpriteBatch batch){
		TextureRegion frame = currentAnimation.getKeyFrame(frameTime);
		frame.flip(!facingRight, false);
		batch.draw(currentAnimation.getKeyFrame(frameTime), x, y, frame.getRegionWidth() * 0.5f, frame.getRegionHeight() * 0.5f, frame.getRegionWidth(), frame.getRegionHeight(), 6.0f, 6.0f, 0.0f);
		frame.flip(frame.isFlipX(), false);
	}
	
	public void handleInput(){
		IPlayerState newState = playerState.handleInput();
		if(newState != null){
			playerState = newState;
			playerState.init(this);
		}
		playerState.update(this);
	}
	
}
