package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.editor.Placeable;
import com.fullspectrum.editor.command.Command;

public class PlaceAction extends Action {

	private Placeable activePlaceable;
	
	@Override
	public void init() {
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
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(!editor.isMouseOnMap()) return false;
		Vector2 mousePos = editor.toWorldCoords(editor.toHudCoords(screenX, screenY));
		if(!activePlaceable.placeOnClick()) {
			Command command = activePlaceable.onClick(mousePos, editor);
			if(command.editsTiles()) {
				editor.beginTile();
				editor.executeCommand(command);
				editor.endTile();
			} else {
				editor.executeCommand(command);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(!editor.isMouseOnMap()) return false;
		Vector2 mousePos = editor.toWorldCoords(editor.toHudCoords(screenX, screenY));
		Command command = activePlaceable.onClick(mousePos, editor);
		if(command.editsTiles()) {
			editor.beginTile();
			editor.executeCommand(command);
			editor.endTile();
		} else {
			editor.executeCommand(command);
		}
		return false;
	}

}
