package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.editor.command.EraseCommand;
import com.cpubrew.game.GameVars;
import com.cpubrew.utils.Maths;

public class EraseAction extends EditorAction {

	private float mouseX;
	private float mouseY;
	
	@Override
	public void onEnter() {
	}
	
	@Override
	public void onExit() {
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
	public void onMouseDrag(int x, int y) {
		mouseX = x;
		mouseY = y;
	}
	
	@Override
	public void onMouseMove(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

}
