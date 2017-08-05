package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.editor.command.EraseCommand;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.utils.Maths;

public class EraseAction extends Action {

	private float mouseX;
	private float mouseY;
	
	@Override
	public void init() {
	}
	
	@Override
	public void update(float delta) {
		if(editor.isMouseOnMap() && editor.isMouseDown()) {
			Vector2 worldCoords = editor.toWorldCoords(mouseX, mouseY);
			
			int row = Maths.toGridCoord(worldCoords.y);
			int col = Maths.toGridCoord(worldCoords.x);
			
			editor.executeCommand(new EraseCommand(row, col));
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		Vector2 worldCoords = editor.toWorldCoords(mouseX, mouseY);
		
		int row = Maths.toGridCoord(worldCoords.y);
		int col = Maths.toGridCoord(worldCoords.x);
		
		Texture eraseTexture = editor.getEraseTexture();
		
		batch.begin();
		batch.draw(eraseTexture, col, row, 0.0f, 0.0f, eraseTexture.getWidth(), eraseTexture.getHeight(), GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, eraseTexture.getWidth(), eraseTexture.getHeight(), false, false);
		batch.end();
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector2 mouseCoords = editor.toHudCoords(screenX, screenY);
		mouseX = mouseCoords.x;
		mouseY = mouseCoords.y;
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector2 mouseCoords = editor.toHudCoords(screenX, screenY);
		mouseX = mouseCoords.x;
		mouseY = mouseCoords.y;
		return false;
	}

}
