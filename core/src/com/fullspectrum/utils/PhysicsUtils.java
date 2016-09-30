package com.fullspectrum.utils;

import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody;
import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.fullspectrum.fsm.State;
import com.fullspectrum.physics.EntityFixtures;

public class PhysicsUtils {
	
	public static EntityFixtures getEntityFixtures(FileHandle file, State state){
		EntityFixtures eFixtures = new EntityFixtures();
		String jsonString = file.readString();
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(jsonString);
		
		JsonValue value = root.get(StringUtils.toTitleCase(state.getName()));
		eFixtures.setName(state.getName());
		if(value == null){
			value = root.get("Default");
			eFixtures.setName("Default");
			if(value == null){
				Gdx.app.log("ERROR", "no default physics body for this entity.");
				return null;
			}
		}
		
		for(JsonValue json : value.get("Fixtures")){
			eFixtures.add(loadFixture(json));
		}
		return eFixtures;
	}
	
	public static Body createPhysicsBody(FileHandle file, World world, Vector2 position, boolean withFixtures){
		String jsonString = file.readString();
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(jsonString);
		
		Body body = loadBodyDef(root.get("BodyDef"), world, position);
		if(withFixtures) loadFixtures(root.get("Fixtures"), body);
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
		return world.createBody(bdef);
	}
	
	private static void loadFixtures(JsonValue root, Body body){
		for(JsonValue fixture : root){
			loadFixture(fixture, body);
		}
	}
	
	private static FixtureDef loadFixture(JsonValue root){
		FixtureDef fdef = new FixtureDef();
		fdef.density = root.getFloat("density", 0.0f);
		fdef.restitution = root.getFloat("restitution", 0.0f);
		fdef.friction = root.getFloat("friction", 0.2f);
		fdef.isSensor = root.getBoolean("isSensor", false);
		
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
	
	private static void loadFixture(JsonValue root, Body body){
		FixtureDef fdef = loadFixture(root);
		body.createFixture(fdef);
	}
}
