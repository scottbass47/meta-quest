package com.fullspectrum.entity.player;

import static com.fullspectrum.game.GameVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.GameInput;

public class Player implements Disposable{

	// Animation
	private ArrayMap<PlayerAnim, Animation> animations;
	private final static float ANIM_SPEED = 0.1f;
	private IPlayerState playerState;
	private Animation currentAnimation;
	private float frameTime = 0.0f;
	private TextureAtlas knightAtlas;
	
	// Physics
	protected World world;
	protected Body body;
	protected float width = 20.0f * 3.0f / PPM;
	protected float height = 32.0f * 3.0f / PPM;
	protected float x;
	protected float y;
	protected float dx;
	protected float dy;
	protected boolean facingRight = true;
	public final static float SPEED = 3.0f;
	public final static float ANALOG_THRESHOLD = 0.3f;
	
	// Jumping
	public final static float JUMP_GRAV = -7.0f;
	public final static float JUMP_VELOCITY = 5.0f;
	protected boolean jumping;
	private float jumpTime = 0.0f;
	
	public Player(World world){
		this.world = world;
		init();
	}
	
	private void init(){
		playerState = new IdleState();
		knightAtlas = new TextureAtlas(Gdx.files.internal("sprites/knight_anim.atlas"));
		animations = new ArrayMap<PlayerAnim, Animation>();
		animations.put(PlayerAnim.IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_idle"), PlayMode.NORMAL));
		animations.put(PlayerAnim.RANDOM_IDLE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_randomidle"), PlayMode.NORMAL));
		animations.put(PlayerAnim.RUNNING, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_runcycle"), PlayMode.NORMAL));
		animations.put(PlayerAnim.RISE, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_rise"), PlayMode.NORMAL));
		animations.put(PlayerAnim.JUMP, new Animation(ANIM_SPEED, knightAtlas.findRegions("knight_jump"), PlayMode.NORMAL));
		currentAnimation = animations.get(PlayerAnim.IDLE);
		
		// Setup Physics
		BodyDef bdef = new BodyDef();
		bdef.active = true;
		bdef.position.set(10.0f, 10.0f);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width * 0.5f, height * 0.5f);
		fdef.shape = shape;
		body.createFixture(fdef);
	}
	
	protected void setAnimation(PlayerAnim playerAnim){
		frameTime = 0;
		currentAnimation = animations.get(playerAnim);
	}
	
	protected void jump(){
		body.applyForceToCenter(new Vector2(0, 1000), true);
	}
	
	public void update(float delta){
		frameTime += delta;
		playerState.update(this);
		if(frameTime > currentAnimation.getAnimationDuration()){ 
			frameTime = 0;
			playerState.animFinished(this);
		}
		jumping = body.getLinearVelocity().y != 0;
//		if(jumping){
//			jumpTime += delta;
//			dy = JUMP_GRAV * jumpTime + JUMP_VELOCITY;
//		}
//		x += dx * delta;
//		y += dy * delta;
		
		// Fake collision detection
//		if(y < 5){
//			jumpTime = 0;
//			jumping = false;
//			dy = 0;
//			y = 5;
//		}
	}
	
	public void render(SpriteBatch batch){
		TextureRegion frame = currentAnimation.getKeyFrame(frameTime);
		frame.flip(!facingRight, false);
		batch.draw(currentAnimation.getKeyFrame(frameTime), body.getPosition().x - width * 0.5f, body.getPosition().y - height * 0.5f, 0, 0, frame.getRegionWidth(), frame.getRegionHeight(), 
					3.0f / PPM, 3.0f / PPM, 0.0f);
		frame.flip(frame.isFlipX(), false);
	}
	
	public void handleInput(GameInput input){
		IPlayerState newState = playerState.handleInput(input);
		if(newState != null){
			playerState = newState;
			playerState.init(this);
		}
		if(playerState instanceof IDirection){
			if(input.getValue(Actions.MOVE_LEFT) < ANALOG_THRESHOLD && input.getValue(Actions.MOVE_RIGHT) < ANALOG_THRESHOLD){
				dx = 0;
			}
			else if(input.getValue(Actions.MOVE_LEFT) > ANALOG_THRESHOLD){
				dx = -SPEED * input.getValue(Actions.MOVE_LEFT);
			}
			else if(input.getValue(Actions.MOVE_RIGHT) > ANALOG_THRESHOLD){
				dx = SPEED * input.getValue(Actions.MOVE_RIGHT);
			}
			body.applyForceToCenter(dx * 10, 0, true);
			facingRight = dx > 0 || dx < 0 ? dx > 0 : facingRight;
		}
	}
	
	/**
	 * Sets the current <code>IPlayerState</code> and initializes it.
	 * 
	 * @param state
	 */
	public void setPlayerState(IPlayerState state){
		this.playerState = state;
		state.init(this);
	}

	@Override
	public void dispose() {
		knightAtlas.dispose();
	}

}
