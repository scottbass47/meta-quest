package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.SelectableTile;
import com.cpubrew.game.GameVars;
import com.cpubrew.gui.KeyBoardManager;
import com.cpubrew.level.GridPoint;
import com.cpubrew.level.tiles.MapTile;
import com.cpubrew.utils.Maths;

public class TileSelectManager implements SelectManager<SelectableTile>{
	
	private LevelEditor editor;
	private OrthographicCamera worldCamera;
	private Array<SelectableTile> selected;
	
	private GridPoint start;
	private GridPoint current;
	private boolean magicSelect;
	private boolean renderSelection;
	
	public TileSelectManager() {
		selected = new Array<SelectableTile>();
		start = new GridPoint();
		current = new GridPoint();
	}

	@Override
	public void update(float delta) {}
	
	@Override
	public void render(SpriteBatch batch) {
		if(!renderSelection) return;
		
		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();
		
		if(magicSelect) {
			for(SelectableTile tile : selected) {
				batch.draw(editor.getSelectTexture(), tile.getTile().getCol(), tile.getTile().getRow(), 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, 16, 16, false, false);
			}
		}
		else {
			GridPoint lowerLeft = getLowerLeft();
			GridPoint upperRight = getUpperRight();
			
			for(int row = lowerLeft.row; row <= upperRight.row; row++) {
				for(int col = lowerLeft.col; col <= upperRight.col; col++) {
					Texture select = editor.getSelectTexture();
					batch.draw(select, col, row, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, 16, 16, false, false);
				}
			}
		} 
		
		batch.end();
	}
	
	@Override
	public void mouseDown(Vector2 worldPos) {
		if(contains(worldPos)) {
			// Go to move command
		} else {
			start.set(Maths.toGridCoord(worldPos.y), Maths.toGridCoord(worldPos.x));
			current.set(start);
			magicSelect = false;
			renderSelection = true;
		}
	}
	
	@Override
	public void mouseUp(Vector2 worldPos) {
		current.set(Maths.toGridCoord(worldPos.y), Maths.toGridCoord(worldPos.x));
		selected.clear();
		magicSelect = KeyBoardManager.isControlDown();
		
		if(magicSelect) {
			magicSelectTiles(current.row, current.col);
		} else {
			GridPoint lowerLeft = getLowerLeft();
			GridPoint upperRight = getUpperRight();
			
			for(int row = lowerLeft.row; row <= upperRight.row; row++) {
				for(int col = lowerLeft.col; col <= upperRight.col; col++) {
					if(editor.getTile(row, col) != null) {
						selected.add(new SelectableTile(editor.getTile(row, col)));
					}
				}
			}	
		}
	}
	
	@Override
	public void mouseDrag(Vector2 worldPos) {
		current.set(Maths.toGridCoord(worldPos.y), Maths.toGridCoord(worldPos.x));
	}
	
	@Override
	public void onDelete() {
		selected.clear();
		renderSelection = false;
	}

	@Override
	public Array<SelectableTile> getSelected() {
		return selected;
	}

	@Override
	public void onPaste(Array<SelectableTile> clipboard) {
		
	}
	
	@Override
	public void set(Vector2 start, Vector2 end, Array<SelectableTile> selected) {
		
	}
	
	/**
	 * Returns true if the vector coords are contained within the selection
	 * @param worldPos
	 * @return
	 */
	private boolean contains(Vector2 worldPos) {
		// If the selection isn't being rendered, then there is no selection
		if(!renderSelection) return false;
		
		int row = Maths.toGridCoord(worldPos.y);
		int col = Maths.toGridCoord(worldPos.x);
		
		// Can't do a simple check with the magic select
		if(magicSelect) {
			for(SelectableTile tile : selected) {
				if(tile.getTile().getRow() == row && tile.getTile().getCol() == col) return true;
			}
		} else {
			GridPoint lowerLeft = getLowerLeft();
			GridPoint upperRight = getUpperRight();
			
			return row >= lowerLeft.row && row <= upperRight.row && col >= lowerLeft.col && col <= upperRight.col;
		}
		return false;
	}
	
	private void magicSelectTiles(int row, int col) {
		if(editor.getTile(row, col) == null) return;
		recursiveMagicSelect(row, col, new ObjectSet<MapTile>());
	}
	
	private void recursiveMagicSelect(int row, int col, ObjectSet<MapTile> visited) {
		MapTile tile = editor.getTile(row, col);
		visited.add(tile);
		selected.add(new SelectableTile(tile));
		
		if(editor.getTile(row + 1, col) != null && !visited.contains(editor.getTile(row + 1, col))) {
			recursiveMagicSelect(row + 1, col, visited);
		}
		
		if(editor.getTile(row - 1, col) != null && !visited.contains(editor.getTile(row - 1, col))) {
			recursiveMagicSelect(row - 1, col, visited);
		}
		
		if(editor.getTile(row, col + 1) != null && !visited.contains(editor.getTile(row, col + 1))) {
			recursiveMagicSelect(row, col + 1, visited);
		}
		
		if(editor.getTile(row, col - 1) != null && !visited.contains(editor.getTile(row, col - 1))) {
			recursiveMagicSelect(row, col - 1, visited);
		}
	}
	
	/** Returns the lower left corner of the select area */
	private GridPoint getLowerLeft() {
		return new GridPoint(Math.min(start.row, current.row), Math.min(start.col, current.col));
	}
	
	/** Returns the upper right corner of the select area */
	private GridPoint getUpperRight() {
		return new GridPoint(Math.max(start.row, current.row), Math.max(start.col, current.col));
	}
	
	public void setSelected(Array<SelectableTile> selected) {
		this.selected = selected;
	}
	
	public void setEditor(LevelEditor editor) {
		this.editor = editor;
	}
	
	public void setWorldCamera(OrthographicCamera worldCamera) {
		this.worldCamera = worldCamera;
	}
}
