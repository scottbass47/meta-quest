package com.fullspectrum.level;

public enum Theme {

	GRASSY(WorldType.SURFACE),
	RAINY(WorldType.SURFACE),
	LIGHT_FOREST(WorldType.SURFACE),
	DARK_FOREST(WorldType.SURFACE),
	LIGHT_CAVE(WorldType.CAVE),
	MINESHAFT(WorldType.CAVE),
	SPIDER_NEST(WorldType.CAVE),
	DARK_CAVE(WorldType.CAVE);
	
	private WorldType parent;
	
	private Theme(WorldType parent){
		this.parent = parent;
	}
	
	public WorldType getHub(){
		return parent;
	}
	
	public String lowerCaseName(){
		return name().toLowerCase();
	}
	
	@Override
	public String toString() {
		return lowerCaseName();
	}
	
	public int getIndex(){
		return ordinal();
	}
	
	public Theme getNext(){
		return get(ordinal() + 1);
	}
	
	public static Theme get(int index){
		if(index < 0 || index >= values().length) return null;
		return values()[index];
	}
	
	public static Theme get(String name){
		for(Theme theme : Theme.values()){
			if(theme.name().equalsIgnoreCase(name)) return theme;
		}
		return null;
	}
	
}
