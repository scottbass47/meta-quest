package com.cpubrew.physics;

import static com.cpubrew.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.component.CollisionListenerComponent;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.physics.collision.CollisionBodyType;
import com.cpubrew.physics.collision.CollisionData;

public class BodyBuilder {

	private Entity entity;
	private BodyDef bdef;
	private CollisionBodyType collisionType;
	private CollisionListenerComponent listenerComp;
	private Array<FixtureBuilder> fixtures;
	
	public BodyBuilder() {
		bdef = new BodyDef();
		bdef.fixedRotation = true;
		bdef.bullet = false;
		bdef.gravityScale = 1.0f;
		
		listenerComp = EntityFactory.engine.createComponent(CollisionListenerComponent.class);
		listenerComp.collisionData = new CollisionData();
		
		fixtures = new Array<FixtureBuilder>();
	}
	
	public BodyBuilder type(BodyType bodyType, CollisionBodyType collisionType) {
		bdef.type = bodyType;
		this.collisionType = collisionType;
		return this;
	}
	
	public BodyBuilder pos(float x, float y) {
		bdef.position.set(x, y);
		return this;
	}
	
	public BodyBuilder fixedRotation(boolean fixed) {
		bdef.fixedRotation = fixed;
		return this;
	}
	
	public BodyBuilder gravScale(float gravScale) {
		bdef.gravityScale = gravScale;
		return this;
	}
	
	public BodyBuilder bullet(boolean bullet) {
		bdef.bullet = bullet;
		return this;
	}

	public BodyBuilder allowSleep(boolean sleep) {
		bdef.allowSleep = sleep;
		return this;
	}
	
	public FixtureBuilder addFixture() {
		return new FixtureBuilder(this);
	}
	
	/**
	 * Avoid calling this in the <code>EntityFactory</code>. If you do, you risk getting a <code>world.isLocked() == True</code> exception. 
	 * @return
	 */
	public Body build() {
		listenerComp.type = collisionType;
		entity.add(listenerComp);
		
		Body body = EntityFactory.world.createBody(bdef);
		body.setUserData(entity);

		for(FixtureBuilder fb : fixtures) {
			Shape shape = fb.fdef.shape;
			Fixture fixture = body.createFixture(fb.fdef);
			fixture.setUserData(fb.type);
			listenerComp.collisionData.registerDefault(fb.type, entity);
			shape.dispose();
		}
		
		return body;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public static class FixtureBuilder {
		
		private BodyBuilder bodyBuilder;
		private FixtureDef fdef;
		private FixtureType type;
		
		public FixtureBuilder(BodyBuilder bodyBuilder) {
			this.bodyBuilder = bodyBuilder;
			
			fdef = new FixtureDef();
			fdef.friction = 0.0f;
		}
		
		public FixtureBuilder makeSensor() {
			fdef.isSensor = true;
			return this;
		}
		
		public FixtureBuilder fixtureType(FixtureType type) {
			this.type = type;
			return this;
		}
		
		public FixtureBuilder loop(Vector2[] vertices) {
			ChainShape chain = new ChainShape();
			chain.createLoop(vertices);
			fdef.shape = chain;
			return this;
		}
		
		public FixtureBuilder chain(Vector2[] vertices) {
			ChainShape chain = new ChainShape();
			chain.createChain(vertices);
			fdef.shape = chain;
			return this;
		}
		
		/**
		 * x, y  represent center coords and width, height represent full width and height.
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 * @return
		 */
		public FixtureBuilder box(float x, float y, float width, float height) {
			PolygonShape poly = new PolygonShape();
			poly.setAsBox(width * 0.5f, height * 0.5f, new Vector2(x, y), 0.0f);
			fdef.shape = poly;
			return this;
		}
		
		/**
		 * Creates a box scaling down all dimensions by PPM_INV centered at x, y
		 * @param x
		 * @param y
		 * @param width
		 * @param height
		 * @return
		 */
		public FixtureBuilder boxPixels(int x, int y, int width, int height) {
			return box(x * PPM_INV, y * PPM_INV, width * PPM_INV, height * PPM_INV);
		}
		
		public FixtureBuilder circle(float x, float y, float radius) {
			CircleShape circle = new CircleShape();
			circle.setPosition(new Vector2(x, y));
			circle.setRadius(radius);
			fdef.shape = circle;
			return this;
		}
		
		public FixtureBuilder shape(Shape shape) {
			fdef.shape = shape;
			return this;
		}
		
		public BodyBuilder build() {
			if(type == null) throw new NullPointerException("FixtureType cannot be null.");
			bodyBuilder.fixtures.add(this);
			return bodyBuilder;
		}
		
	}
}
