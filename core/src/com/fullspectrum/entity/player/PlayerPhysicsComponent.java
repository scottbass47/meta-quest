package com.fullspectrum.entity.player;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.fullspectrum.component.PhysicsComponent;
import com.fullspectrum.entity.Entity;

public class PlayerPhysicsComponent extends PhysicsComponent {

	public final static float SPEED = 5.0f;
	
	public PlayerPhysicsComponent(World world, Player player) {
		super(world);
	}

	@Override
	public void init() {
		// Setup Physics
		BodyDef bdef = new BodyDef();
		bdef.active = true;
		bdef.position.set(10.0f, 10.0f);
		bdef.type = BodyType.DynamicBody;
		body = world.createBody(bdef);

		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		// TODO -> Make physics less strict
		float width = 15.0f / 8.0f;
		float height = 3.0f;
		shape.setAsBox(width * 0.3f, height * 0.4f, new Vector2(0.1f, -0.1f), 0);
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

	@Override
	public void update(float delta, Entity entity) {
		// Movement
		float velChange = entity.dx - body.getLinearVelocity().x;
		float impulse = body.getMass() * velChange;
		body.applyLinearImpulse(impulse, 0, body.getWorldCenter().x, body.getWorldCenter().y, true);
	}
	
	public void jump(){
		body.applyForceToCenter(new Vector2(0, 500), true);
	}

}
