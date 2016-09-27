package com.fullspectrum.utils;

import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class PhysicsUtils {
	
	public static Body createPhysicsBody(FileHandle file, World world, Vector2 position){
		String jsonString = file.readString();
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(jsonString);
		
		Body body = loadBodyDef(root.get("BodyDef"), world, position);
		loadFixtures(root.get("Fixtures"), world);
	}
	
	private static Body loadBodyDef(JsonValue root, World world, Vector2 position){
		BodyDef bdef = new BodyDef();
		String type = root.getString("type").toLowerCase();
		
		if(type.equals("dynamicbody")) bdef.type = DynamicBody;
		if(type.equals("staticbody")) bdef.type = StaticBody;
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
	
	private static void loadFixtures(JsonValue root, World world){
		for(JsonValue fixture : root.get("Fixtures")){
			loadFixture(fixture, world);
		}
	}
	
	private static void loadFixture(JsonValue root, World world){
		
	}
	
}
