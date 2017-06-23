package com.fullspectrum.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Placeable {
	
	/** Called when the placeable is being placed */
	public void onClick(Vector2 mousePos, LevelEditor editor);
	
	public void update(float delta);
	
	/** 
	 * Batch will be loaded up with the world camera's projection matrix. Don't need to begin/end batch. Mouse pos
	 * is already in world coords.
	 */
	public void render(Vector2 mousePos, SpriteBatch batch, LevelEditor editor);
	
	/** Return true if you want this placeable to only be placed when the mouse is clicked and not just when it's held down */
	public boolean placeOnClick();
	
}