package com.cpubrew.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.editor.command.Command;

/**
 * All position arguments are passed through using the <code>getPosition</code> function
 * @author Scott
 *
 * @param <T>
 */
public interface Interactable<T> {

	/** Updates the object */
	public void update(float delta, LevelEditor editor);
	
	/** Converts a raw world position to the actual position based on snapping policies */
	public Vector2 getPosition(Vector2 position);
	
	/** Returns the position of the Interactable based on its current position and the specified offset */
	public Vector2 getPositionOff(Vector2 offset);
	
	public void render(SpriteBatch batch, Vector2 worldPos, LevelEditor editor);
	public Interactable<T> copy(LevelEditor editor);
	public void remove(LevelEditor editor);
	public boolean contentsEqual(T value);
	
//	/** 
//	 * Moves the Interactable to a specified position.
//	 * @param position - The actual position calculated with <code>getPosition</code>
//	 * @param editor
//	 */
//	public void move(Vector2 position, LevelEditor editor);
	
	/** Adds the Interactable to the map at the specified position. 
	 * <br><br>
	 * Note: This position should be saved in the underlying object for future moving/selecting.
	 * 
	 * @param position
	 * @param editor
	 */
	public void add(Vector2 position, LevelEditor editor);
	
	/** Called when the <code>Interactable</code> is being placed */
	public Command onPlace(Vector2 mousePos, LevelEditor editor);
	
	/** Return true if you want this <code>Interactable</code> to only be placed when the mouse is clicked and not just when it's held down */
	public boolean placeOnClick();
	
}
