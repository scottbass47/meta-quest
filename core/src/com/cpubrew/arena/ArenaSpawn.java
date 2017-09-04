package com.cpubrew.arena;

import com.badlogic.gdx.math.Vector2;

public class ArenaSpawn {
	
	private int id;
	private Vector2 pos;

	public ArenaSpawn() {
		id = -1;
		pos = new Vector2();
	}
	
	public ArenaSpawn(int id, Vector2 pos) {
		this.id = id;
		this.pos = pos;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Vector2 getPos() {
		return pos;
	}
	
	public void setPos(Vector2 pos) {
		this.pos = pos;
	}
	
	@Override
	public String toString() {
		return id + ": " + pos;
	}
}
