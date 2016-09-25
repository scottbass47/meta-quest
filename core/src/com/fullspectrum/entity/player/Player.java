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
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

public class Player implements Disposable {

	// Animation
	public static ArrayMap<PlayerAnim, Animation> animations;
	private final static float ANIM_SPEED = 0.1f;
	private Animation currentAnimation;
	private float frameTime = 0.0f;
	private static TextureAtlas knightAtlas;
	protected boolean facingRight = true;

	// Physics
	protected World world;
	protected Body body;
//	protected float width = 20.0f * PLAYER_SCALE / PPM;
//	protected float height = 32.0f * PLAYER_SCALE / PPM;
	protected float x;
	protected float y;
	protected float dx;
	protected float dy;
	public final static float SPEED = 4.5f;

	// Jumping
	public final static float JUMP_GRAV = -7.0f;
	public final static float JUMP_VELOCITY = 5.0f;
	protected boolean jumping;
	private float jumpTime = 0.0f;

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
	
	public Player() {
		init();
	}

	private void init() {
		
		currentAnimation = animations.get(PlayerAnim.IDLE);

		// Setup Physics
		BodyDef bdef = new BodyDef();
		bdef.active = true;
		bdef.position.set(10.0f, 10.0f);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
//		shape.setAsBox(width * 0.3f, height * 0.4f, new Vector2(0.1f, -0.1f), 0);
		fdef.shape = shape;

		body.createFixture(fdef);

		// Feet
		CircleShape cshape = new CircleShape();
		cshape.setRadius(0.1f);
		cshape.setPosition(new Vector2(0.59f, -1.35f));
		fdef.shape = cshape;
		fdef.friction = 1.0f;
		body.createFixture(fdef);

		cshape.setPosition(new Vector2(-0.4f, -1.35f));
		body.createFixture(fdef);
	}

	protected void setAnimation(PlayerAnim playerAnim) {
		frameTime = 0;
		currentAnimation = animations.get(playerAnim);
	}

	protected void jump() {
		body.applyForceToCenter(new Vector2(0, 500), true);
	}

	public void update(float delta) {
		frameTime += delta;
		if (frameTime > currentAnimation.getAnimationDuration()) {
			frameTime = 0;
		}
		jumping = body.getLinearVelocity().y != 0;
	}

	public void render(SpriteBatch batch) {
		TextureRegion frame = currentAnimation.getKeyFrame(frameTime);
		frame.flip(!facingRight, false);
//		batch.draw(currentAnimation.getKeyFrame(frameTime), body.getPosition().x - width * 0.5f, body.getPosition().y - height * 0.5f, 0, 0, frame.getRegionWidth(), frame.getRegionHeight(), PLAYER_SCALE / PPM, PLAYER_SCALE / PPM, 0.0f);
		frame.flip(frame.isFlipX(), false);
	}

//	public void handleInput(GameInput input) {
//		IPlayerState newState = playerState.handleInput(input);
//		if (newState != null) {
//			playerState = newState;
//			playerState.init(this);
//		}
//		if (playerState instanceof IDirection) {
//			if (input.getValue(Actions.MOVE_LEFT) < ANALOG_THRESHOLD && input.getValue(Actions.MOVE_RIGHT) < ANALOG_THRESHOLD) {
//				dx = 0;
//			} else if (input.getValue(Actions.MOVE_LEFT) > ANALOG_THRESHOLD) {
//				dx = -SPEED * input.getValue(Actions.MOVE_LEFT);
//			} else if (input.getValue(Actions.MOVE_RIGHT) > ANALOG_THRESHOLD) {
//				dx = SPEED * input.getValue(Actions.MOVE_RIGHT);
//			}
//			float velChange = dx - body.getLinearVelocity().x;
//			float impulse = body.getMass() * velChange;
//			body.applyLinearImpulse(impulse, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
//			// System.out.println(body.getLinearVelocity().x);
//			facingRight = dx > 0 || dx < 0 ? dx > 0 : facingRight;
//		}
//	}
//
//	/**
//	 * Sets the current <code>IPlayerState</code> and initializes it.
//	 * 
//	 * @param state
//	 */
//	public void setPlayerState(IPlayerState state) {
//		this.playerState = state;
//		state.init(this);
//	}

	@Override
	public void dispose() {
		knightAtlas.dispose();
	}

}
