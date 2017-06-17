package com.fullspectrum.level.tiles;

public class MapTile {

	private int id;
	private int row;
	private int col;
	private TileType type;
	
	public MapTile() {
		id = -1;
		row = 0;
		col = 0;
	}
	
	public MapTile(int row, int col, TileType type){
		this.row = row;
		this.col = col;
		this.type = type;
	}
	
	public int getRow() {
		return row;
	}

	public int getCol(){
		return col;
	}
	
	public int getIndex(int width){
		return row * width + col;
	}
	
	public TileType getType(){
		return type;
	}
	
	public int getID() {
		return id;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setType(TileType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		result = prime * result + ((type == null) ? 0 : type.name().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MapTile other = (MapTile) obj;
		if (col != other.col) return false;
		if (row != other.row) return false;
		if (type != other.type) return false;
		return true;
	}

	public boolean isSolid(){
		return type.isSolid;
	}
	
	public enum Side{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	public enum TileType{
		GROUND(true),
		LADDER(false),
		DECOR(false);
		
		private final boolean isSolid;

		private TileType(boolean isSolid){
			this.isSolid = isSolid;
		}
		
		public boolean isSolid(){
			return isSolid;
		}
		
		public static TileType getType(String name){
			for(TileType type : values()){
				if(type.name().equalsIgnoreCase(name)) return type;
			}
			return null;
		}
	}
}
