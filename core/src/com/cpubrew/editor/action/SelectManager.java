package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.Interactable;

public interface SelectManager<T extends Interactable<?>> {

	public void render(SpriteBatch batch);
	public void update(float delta);
	public void mouseDrag(Vector2 worldPos);
	public void mouseUp(Vector2 worldPos);
	public void mouseDown(Vector2 worldPos);
	
	/** Called upon deletion of the current selection of Interactables. Deletion shouldn't be handled by the manager (this is simply an event). */
	public void onDelete();
	public Array<T> getSelected();
	public void onPaste(Array<T> clipboard);
	
	/** Called when returning to selecting from moving */
	public void set(Vector2 start, Vector2 end, Array<T> selected);
	
}
