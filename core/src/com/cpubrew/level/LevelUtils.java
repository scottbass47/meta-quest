package com.cpubrew.level;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cpubrew.editor.mapobject.MapObject;
import com.cpubrew.level.tiles.MapTile;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class LevelUtils {

	private LevelUtils() {}
	
	private static Kryo setupKryoInstance() {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		kryo.register(Level.class, Level.getSerializer(), 10);
		kryo.register(MapTile.class, MapTile.getSerializer(), 11);
		kryo.register(MapObject.class, MapObject.getSerializer(), 12);
		return kryo;
	}
	
	public static Level loadLevel(LevelManager manager, String levelName) {
		FileHandle handle = Gdx.files.local("map/" + levelName + ".map");
		
		if(!handle.exists()) return null;
		
		Kryo kryo = setupKryoInstance();
		Input input = new Input(new BufferedInputStream(handle.read()));
		
		Level level = kryo.readObject(input, Level.class);
		level.setManager(manager);
		level.setName(levelName);
		
		input.close();
		return level;
	}
	
//	public static void saveTmpLevel(Level level) {
//		FileHandle handle = Gdx.files.local("map/" + "~" + level.getInfo().toFileFormat() + "~.map");
//		
//		Kryo kryo = setupKryoInstance();
//		Output output = new Output(new BufferedOutputStream(handle.write(false)));
//		
//		kryo.writeObject(output, level);
//		output.close();
//	}
	
	public static void saveLevel(Level level) {
		FileHandle handle = Gdx.files.local("map/" + level.getName() + ".map");
		
		Kryo kryo = setupKryoInstance();
		Output output = new Output(new BufferedOutputStream(handle.write(false)));
		
		kryo.writeObject(output, level);
		output.close();
	}
	
	public static boolean levelExists(String levelName) {
		return Gdx.files.local("map/" + levelName + ".map").exists();
	}
}
