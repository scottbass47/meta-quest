package com.cpubrew.editor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.editor.command.Command;

public interface Interactable<T> {

	/** Updates the object */
	public void update(float delta, LevelEditor editor);
	public Vector2 getPosition(Vector2 offset);
	public void render(SpriteBatch batch, Vector2 worldPos, LevelEditor editor);
	public Interactable<T> copy(LevelEditor editor);
	public void remove(LevelEditor editor);
	public boolean contentsEqual(T value);
	public void move(Vector2 position, LevelEditor editor);
	public void add(Vector2 position, LevelEditor editor);
	
	/** Called when the <code>Interactable</code> is being placed */
	public Command onPlace(Vector2 mousePos, LevelEditor editor);
	
	/** Return true if you want this <code>Interactable</code> to only be placed when the mouse is clicked and not just when it's held down */
	public boolean placeOnClick();
	
}
