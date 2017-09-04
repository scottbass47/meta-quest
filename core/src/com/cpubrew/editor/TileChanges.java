package com.cpubrew.editor;

import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.level.GridPoint;
import com.cpubrew.level.tiles.MapTile;

public class TileChanges {

	private ArrayMap<GridPoint, MapTile> tileMap;
	
	public TileChanges() {
		tileMap = new ArrayMap<GridPoint, MapTile>();
	}
	
	public void addTile(int row, int col, MapTile value) {
		if(tileMap.containsKey(new GridPoint(row, col))) return;
		tileMap.put(new GridPoint(row, col), value == null ? null : new MapTile(value));
	}
	
	public void removeTile(int row, int col) {
		tileMap.removeKey(new GridPoint(row, col));
	}
	
	public ArrayMap<GridPoint, MapTile> getChanges(){
		return tileMap;
	}
	
	@Override
	public String toString() {
		return tileMap.toString();
	}
}
