package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.editor.TilePanel;
import com.fullspectrum.editor.command.FillCommand;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.level.GridPoint;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.utils.Maths;

public class FillAction extends Action {

	private GridPoint currTile;
	private Array<GridPoint> fillTiles;
	
	@Override
	public void init() {
		fillTiles = new Array<GridPoint>();
	}

	@Override
	public void update(float delta) {
		if(editor.isMouseOnMap()) {
			if(currTile == null) currTile = new GridPoint();
			
			Vector2 mouseCoords = editor.toWorldCoords(editor.getMousePos());
			currTile.row = Maths.toGridCoord(mouseCoords.y);
			currTile.col = Maths.toGridCoord(mouseCoords.x);
			
			brushFire(currTile.row, currTile.col);
		} else {
			currTile = null;
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if(editor.isMouseOnMap() && currTile != null) {
			batch.begin();
			for(GridPoint p : fillTiles) {
				batch.draw(editor.getSelectTexture(), p.col, p.row, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, 16, 16, false, false);
			}
			batch.end();
		}
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		TilePanel tilePanel = editor.getTilePanel();
		editor.executeCommand(new FillCommand(fillTiles, tilePanel.getActiveTile().getID(), TileType.GROUND));
		return false;
	}
	
	private void brushFire(int row, int col) {
		MapTile tile = editor.getTile(row, col);
		fillTiles.clear();
		recursiveBrushFire(row, col, getCluster(tile), new ObjectSet<GridPoint>());
	}
	
	private void recursiveBrushFire(int row, int col, int clusterID, ObjectSet<GridPoint> visited) {
		GridPoint point = new GridPoint(row, col);
		visited.add(point);
		fillTiles.add(point);
		
		if(editor.contains(row + 1, col) && getCluster(editor.getTile(row + 1, col)) == clusterID && !visited.contains(new GridPoint(row + 1, col))) {
			recursiveBrushFire(row + 1, col, clusterID, visited);
		}
		
		if(editor.contains(row - 1, col) && getCluster(editor.getTile(row - 1, col)) == clusterID && !visited.contains(new GridPoint(row - 1, col))) {
			recursiveBrushFire(row - 1, col, clusterID, visited);
		}
		
		if(editor.contains(row, col + 1) && getCluster(editor.getTile(row, col + 1)) == clusterID && !visited.contains(new GridPoint(row, col + 1))) {
			recursiveBrushFire(row, col + 1, clusterID, visited);
		}
		
		if(editor.contains(row, col - 1) && getCluster(editor.getTile(row, col - 1)) == clusterID && !visited.contains(new GridPoint(row, col - 1))) {
			recursiveBrushFire(row, col - 1, clusterID, visited);
		}
	}
	
	private int getCluster(MapTile tile) {
		return tile == null ? -1 : editor.getTilePanel().getTileset().getClusterID(tile.getID());
	}

}
