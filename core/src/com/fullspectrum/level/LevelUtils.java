package com.fullspectrum.level;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fullspectrum.level.tiles.MapTile;

public class LevelUtils {

	private LevelUtils() {}
	
	private static Kryo setupKryoInstance() {
		Kryo kryo = new Kryo();
		kryo.setReferences(false);
		kryo.register(Level.class, Level.getSerializer(), 10);
		kryo.register(MapTile.class, MapTile.getSerializer(), 11);
		return kryo;
	}
	
	public static Level loadLevel(LevelManager manager, LevelInfo info) {
		FileHandle handle = Gdx.files.local("map/" + info.toFileFormatWithExtension());
		
		if(!handle.exists()) return null;
		
		Kryo kryo = setupKryoInstance();
		Input input = new Input(new BufferedInputStream(handle.read()));
		
		Level level = kryo.readObject(input, Level.class);
		level.setManager(manager);
		level.setInfo(info);
		
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
		FileHandle handle = Gdx.files.local("map/" + level.getInfo().toFileFormatWithExtension());
		
		Kryo kryo = setupKryoInstance();
		Output output = new Output(new BufferedOutputStream(handle.write(false)));
		
		kryo.writeObject(output, level);
		output.close();
	}
}
