package com.cpubrew.assets;

public enum Atlas {

	ENTITY("sprites/entity_assets.atlas"),
	HUD("hud/hud.atlas");
	
	private String filepath;
	
	private Atlas(String filepath){
		this.filepath = filepath;
	}
	
	public String getFilepath() {
		return filepath;
	}
	
}
