package com.fullspectrum.level.tiles;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class Tileset {

	private TextureRegion tilesheet;
	private int width;
	private int height;
	private Array<TilesetTile> tiles;
	private IntMap<TilesetTile> tileIDMap;
	private IntMap<Array<TilesetTile>> clusterIDMap;
	
	public Tileset() {
		tiles = new Array<TilesetTile>();
		tileIDMap = new IntMap<TilesetTile>();
		clusterIDMap = new IntMap<Array<TilesetTile>>();
	}
	
	public void addTiles(Array<TilesetTile> tiles) {
		this.tiles.addAll(tiles);
		for(TilesetTile tile : tiles) {
			int tileID = tile.getTileID();
			int clusterID = tile.getClusterID();
			
			tileIDMap.put(tileID, tile);
			addToClusterMap(clusterID, tile);
		}
	}
	
	private void addToClusterMap(int id, TilesetTile tile) {
		if(!clusterIDMap.containsKey(id)) {
			clusterIDMap.put(id, new Array<TilesetTile>());
		}
		clusterIDMap.get(id).add(tile);
	}
	
	public TilesetTile getTilesetTile(int tileID) {
		return tileIDMap.get(tileID);
	}
	
	public IntMap<Array<TilesetTile>> getClusterIDMap() {
		return clusterIDMap;
	}
	
	public void setTilesheet(TextureRegion tilesheet) {
		this.tilesheet = tilesheet;
	}
	
	public TextureRegion getTilesheet() {
		return tilesheet;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getWidth() {
		return width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Array<TilesetTile> getTiles() {
		return tiles;
	}
}
