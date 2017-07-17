package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;

public class FrameMovementComponent implements Component, Poolable{

	public Array<Frame> frames;
	public float elapsed;
	public int index;
	public float frameTimer;
	public boolean useGravity = false;
	
	public FrameMovementComponent() {
		frames = new Array<Frame>();
	}
	
	public FrameMovementComponent set(String filename) {
		return set(filename, false);
	}
	
	public FrameMovementComponent set(String filename, boolean useGravity) {
		loadFrames(filename);
		this.useGravity = useGravity;
		return this;
	}
	
	private void loadFrames(String filename){
		String file = Gdx.files.internal("frames/" + filename + ".json").readString();
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(file);
		JsonValue jframes = root.get("frames");
		for(JsonValue frame : jframes.iterator()){
			frames.add(new Frame(frame.getInt("frame") - 1, frame.getInt("x"), frame.getInt("y")));
		}
	}
	
	@Override
	public void reset() {
		frames = null;
		elapsed = 0.0f;
		index = 0;
		frameTimer = 0.0f;
	}
	
	public class Frame {
		private int number;
		private int x;
		private int y;
		
		public Frame(int number, int x, int y) {
			this.number = number;
			this.x = x;
			this.y = y;
		}
		
		public int getNumber() {
			return number;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
}