package com.fullspectrum.utils;

import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody;
import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Shape.Type;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CollisionListenerComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.physics.BodyProperties;
import com.fullspectrum.physics.CollisionBits;
import com.fullspectrum.physics.FixtureType;
import com.fullspectrum.physics.PhysicsDef;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionData;

public class PhysicsUtils {
	
	public static Body createPhysicsBody(PhysicsDef def){
		return createPhysicsBody(def.getFile(), def.getWorld(), def.getPosition(), def.getEntity(), def.getProperties());
	}
	
	public static Body createPhysicsBody(FileHandle file, World world, Vector2 position, Entity entity){
		return createPhysicsBody(file, world, position, entity, null);
	}
	
	public static Body createPhysicsBody(FileHandle file, World world, Vector2 position, Entity entity, BodyProperties properties){
		if(world.isLocked()){
			EntityManager.addPhysicsLoad(new PhysicsDef(file, world, position, entity, properties));
			return null;
		}
		String jsonString = file.readString();
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(jsonString);
		
		Body body = loadBodyDef(root.get("BodyDef"), world, position);
		CollisionBodyType type = (CollisionBodyType)body.getUserData();
		body.setUserData(entity);
		
		CollisionListenerComponent listenerComp = EntityUtils.add(entity, CollisionListenerComponent.class);
		CollisionData data = new CollisionData();
		
		// Load Properties
		if(properties != null){
			body.setGravityScale(properties.getGravityScale());
			body.setSleepingAllowed(properties.isSleepingAllowed());
			body.setActive(properties.isActive());
		}
		
		loadFixtures(root.get("Fixtures"), body, data);
		listenerComp.collisionData = data;
		listenerComp.type = type;
		return body;
	}
	
	private static Body loadBodyDef(JsonValue root, World world, Vector2 position){
		BodyDef bdef = new BodyDef();
		String type = root.getString("type").toLowerCase();
		
		if(type.equals("dynamicbody")) bdef.type = DynamicBody;
		else if(type.equals("staticbody")) bdef.type = StaticBody;
		else if(type.equals("kinematicbody")) bdef.type = KinematicBody;
		else{
			bdef.type = DynamicBody;
			Gdx.app.log("WARNING", "body not given a type. Setting default type to DynamicBody.");
		}
		bdef.bullet = root.getBoolean("bullet", false);
		bdef.fixedRotation = root.getBoolean("fixedRotation", true);
		bdef.gravityScale = root.getFloat("gravityScale", 1.0f);
		bdef.position.set(position);

		String collisionType = root.getString("collisionType", "empty").toLowerCase();
		CollisionBodyType bodyType = CollisionBodyType.get(collisionType);
		if(bodyType == null){
			throw new RuntimeException("No collision body type for '" + collisionType + "'.");
		}
		Body body = world.createBody(bdef);
		body.setUserData(bodyType);
		return body;
	}
	
	private static void loadFixtures(JsonValue root, Body body, CollisionData data){
		for(JsonValue fixture : root){
			loadFixture(fixture, body, data);
		}
	}
	
	// INCOMPLETE Shapes need to be disposed after being created
	private static FixtureDef loadFixture(JsonValue root, CollisionData data){
		FixtureDef fdef = new FixtureDef();
		fdef.density = root.getFloat("density", 0.0f);
		fdef.restitution = root.getFloat("restitution", 0.0f);
		fdef.friction = root.getFloat("friction", 0.2f);
		CollisionBits bit = CollisionBits.getValue(root.getString("Category", "null"));
//		fdef.filter.categoryBits = bit == null ? -1 : bit.getBit();
//		fdef.filter.maskBits = bit == null ? -1 : CollisionBits.getOtherBits(bit);
		fdef.isSensor = bit == CollisionBits.SENSOR;
		
		float x = root.getFloat("xOff", 0.0f) * PPM_INV;
		float y = root.getFloat("yOff", 0.0f) * PPM_INV;
		
		Shape shape = null;
		String shapeType = root.getString("shape").toLowerCase();
		if(shapeType.equals("box")){
			shape = new PolygonShape();
			PolygonShape poly = (PolygonShape)shape;
			float width = root.getFloat("width") * PPM_INV;
			float height = root.getFloat("height") * PPM_INV;
			poly.setAsBox(width * 0.5f, height * 0.5f, new Vector2(x, y), 0.0f);
		}else if(shapeType.equals("circle")){
			shape = new CircleShape();
			CircleShape circle = (CircleShape)shape;
			float radius = root.getFloat("radius") * PPM_INV;
			circle.setPosition(new Vector2(x, y));
			circle.setRadius(radius);
		}else{
			Gdx.app.log("ERROR", "no shape type defined.");
			return null;
		}
		fdef.shape = shape;
		return fdef;
	}
	
	private static void loadFixture(JsonValue root, Body body, CollisionData data){
		FixtureDef fdef = loadFixture(root, data);
		String type = root.getString("FixtureType", "none");
		FixtureType fixtureType = FixtureType.get(type);
		if(fixtureType == null){
			throw new RuntimeException("Fixture type '" + type + "' does not exist.");
		}
		boolean isSensor = root.getBoolean("isSensor", false);
		if(isSensor) fdef.isSensor = true;
		Fixture fixture = body.createFixture(fdef);
		fixture.setUserData(fixtureType);
		data.registerDefault(fixtureType, (Entity)body.getUserData());
	}
	
	public static Rectangle getAABB(Body body){
		return getAABB(body, false);
	}
	
	public static Rectangle getAABB(Body body, boolean includeSensors){
		float maxX = 0, maxY = 0, minX = 0, minY = 0;
		for(Fixture fixture : body.getFixtureList()){
			if(fixture.isSensor() && !includeSensors) continue;
			Type type = fixture.getShape().getType();
			switch(type){
			case Circle:
				CircleShape circleShape = (CircleShape)fixture.getShape();
				Vector2 position = circleShape.getPosition();
				float radius = circleShape.getRadius();
				if(position.x - radius < minX) minX = position.x - radius;
				if(position.y - radius < minY) minY = position.y - radius;
				if(position.x + radius > maxX) maxX = position.x + radius;
				if(position.y + radius > maxY) maxY = position.y + radius;
				break;
			case Polygon:
				PolygonShape boxShape = (PolygonShape)fixture.getShape();
				for(int i = 0; i < boxShape.getVertexCount(); i++){
					Vector2 vertex = new Vector2();
					boxShape.getVertex(i, vertex);
					if(vertex.x < minX) minX = vertex.x;
					if(vertex.y < minY) minY = vertex.y;
					if(vertex.x > maxX) maxX = vertex.x;
					if(vertex.y > maxY) maxY = vertex.y;
				}
				break;
			default:
//				Gdx.app.error("ERROR", "Invalid shape type for calculating AABB.");
				break;
			}
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	public static Body createTilePhysics(World world, Entity tile, Vector2[] vertices){
		BodyDef bdef = new BodyDef();
		bdef.fixedRotation = true;
		bdef.type = BodyType.StaticBody;
		Body body = world.createBody(bdef);
		body.setUserData(tile);
		
		FixtureDef fdef = new FixtureDef();
		ChainShape shape = new ChainShape();
		shape.createLoop(vertices);
		fdef.shape = shape;
		fdef.friction = 0.0f;
//		fdef.filter.categoryBits = CollisionBits.TILE.getBit();
//		fdef.filter.maskBits = CollisionBits.getOtherBits(CollisionBits.TILE);
		body.createFixture(fdef).setUserData(FixtureType.GROUND);
		
		shape.dispose();
		
		CollisionListenerComponent listenerComp = EntityUtils.add(tile, CollisionListenerComponent.class);
		CollisionData data = new CollisionData();
		
		listenerComp.collisionData = data;
		listenerComp.type = CollisionBodyType.TILE;
		data.registerDefault(FixtureType.GROUND, tile);
		return body;
	}
	
	public static Vector2 getPos(Entity e1){
		BodyComponent bodyComp = Mappers.body.get(e1);
		if(bodyComp == null || bodyComp.body == null){
			PositionComponent posComp = Mappers.position.get(e1);
			return new Vector2(posComp.x, posComp.y);
		}
		return bodyComp.body.getPosition();
	}
	
	public static float getDistanceSqr(Entity e1, Entity e2){
		return getDistanceSqr(getPos(e1), getPos(e2));
	}
	
	public static float getDistanceSqr(Body b1, Body b2){
		return getDistanceSqr(b1.getPosition(), b2.getPosition());
	}
	
	public static float getDistanceSqr(Vector2 vec1, Vector2 vec2){
		return getDistanceSqr(vec1.x, vec1.y, vec2.x, vec2.y);
	}
	
	public static float getDistanceSqr(float x1, float y1, float x2, float y2){
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}
	
	public static float getDistance(Entity e1, Entity e2){
		return (float)Math.sqrt(getDistanceSqr(e1, e2));
	}
	
	
}
