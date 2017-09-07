package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.editor.Placeable;
import com.cpubrew.editor.command.Command;
import com.cpubrew.gui.MouseEvent;

public class PlaceAction extends EditorAction {

	private Placeable activePlaceable;
	
	@Override
	public void onEnter() {
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void update(float delta) {
		activePlaceable.update(delta);
	}

	@Override
	public void render(SpriteBatch batch) {
		if (editor.isMouseOnMap()) {
			batch.setProjectionMatrix(worldCamera.combined);
			batch.begin();
			activePlaceable.render(editor.toWorldCoords(editor.getMousePos()), batch, editor);
			batch.end();
		}
	}
	
	public void setPlaceable(Placeable placeable) {
		this.activePlaceable = placeable;
	}
	
	@Override
	public void onMouseDrag(MouseEvent ev) {
		if(!editor.isMouseOnMap()) return;
		Vector2 mousePos = editor.toWorldCoords(ev.getX(), ev.getY());
		if(!activePlaceable.placeOnClick()) {
			Command command = activePlaceable.onClick(mousePos, editor);
			editor.executeCommand(command);
		}
	}
	
	@Override
	public void onMouseUp(MouseEvent ev) {
		if(!editor.isMouseOnMap()) return;
		Vector2 mousePos = editor.toWorldCoords(ev.getX(), ev.getY());
		Command command = activePlaceable.onClick(mousePos, editor);
		editor.executeCommand(command);
	}

}
