package com.fullspectrum.level.tiles;

public class TilesetTile {

	private int tileID;
	private int clusterID;
	private TileSlot slot;
	private Tileset tileset;
	private int sheetX;
	private int sheetY;

	public TilesetTile() {
		this(-1, TileSlot.SOLO);
	}

	public TilesetTile(int id) {
		this(id, TileSlot.SOLO);
	}

	public TilesetTile(int id, TileSlot slot) {
		this.tileID = id;
		this.slot = slot;
	}

	public int getID() {
		return tileID;
	}

	public TileSlot getSlot() {
		return slot;
	}

	public void setSlot(TileSlot slot) {
		this.slot = slot;
	}

	public Tileset getTileset() {
		return tileset;
	}

	public void setTileset(Tileset tileset) {
		this.tileset = tileset;
	}

	public int getSheetX() {
		return sheetX;
	}

	public void setSheetX(int sheetX) {
		this.sheetX = sheetX;
	}

	public int getSheetY() {
		return sheetY;
	}

	public void setSheetY(int sheetY) {
		this.sheetY = sheetY;
	}
	
	public void setTileID(int id) {
		this.tileID = id;
	}
	
	public int getTileID() {
		return tileID;
	}
	
	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}
	
	public int getClusterID() {
		return clusterID;
	}

	@Override
	public String toString() {
		return tileID + ", " + slot + ", ["
				+ sheetX + ", " + sheetY + "]";
	}

}
