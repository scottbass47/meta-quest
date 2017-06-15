package com.fullspectrum.editor;

import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.level.Level;

public class LevelEditor {

	private Level currentLevel;
	private Vector2 position;
	
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	public void setPosition(Vector2 position) {
		this.position = position;
	}

}
