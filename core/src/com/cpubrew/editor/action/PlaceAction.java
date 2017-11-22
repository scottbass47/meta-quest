package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.editor.Interactable;
import com.cpubrew.editor.command.Command;
import com.cpubrew.gui.MouseEvent;

public class PlaceAction extends EditorAction {

	private Interactable<?> activePlaceable;
	
	@Override
	public void onEnter() {
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void update(float delta) {
		activePlaceable.update(delta, editor);
	}

	@Override
	public void render(SpriteBatch batch) {
		if (editor.isMouseOnMap()) {
			batch.setProjectionMatrix(worldCamera.combined);
			batch.begin();
			activePlaceable.render(batch, activePlaceable.getPosition(editor.toWorldCoords(editor.getMousePos())), editor);
			batch.end();
		}
	}
	
	public void setPlaceable(Interactable<?> placeable) {
		this.activePlaceable = placeable;
	}
	
	@Override
	public void onMouseDrag(MouseEvent ev) {
		if(!editor.isMouseOnMap()) return;
		Vector2 mousePos = editor.toWorldCoords(ev.getX(), ev.getY());
		if(!activePlaceable.placeOnClick()) {
			Command command = activePlaceable.onPlace(activePlaceable.getPosition(mousePos), editor);
			editor.executeCommand(command);
		}
	}
	
	@Override
	public void onMouseUp(MouseEvent ev) {
		if(!editor.isMouseOnMap()) return;
		Vector2 mousePos = editor.toWorldCoords(ev.getX(), ev.getY());
		Command command = activePlaceable.onPlace(activePlaceable.getPosition(mousePos), editor);
		editor.executeCommand(command);
	}

}
