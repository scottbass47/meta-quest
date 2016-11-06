package com.fullspectrum.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

	// Asset Manager
	private AssetManager manager;
	private static Assets instance;
	
	// Nearest Scaling
	private TextureParameter texParam;
	
	// HUD
	public static final String HUD = "hud/hud.atlas";
	public static final String healthBarEmpty = "healthbar_empty";
	public static final String healthBarFull = "healthbar_full";
	public static final String staminaBarEmpty = "staminabar_empty";
	public static final String staminaBarFull = "staminabar_full";
	
	private Assets() {
		manager = new AssetManager();
		texParam = new TextureParameter();
		texParam.minFilter = TextureFilter.Nearest;
	}
	
	public static Assets getInstance(){
		if(instance == null){
			instance = new Assets();
		}
		return instance;
	}
	
	public void loadHUD(){
		manager.load(HUD, TextureAtlas.class);
		manager.finishLoading();
	}
	
	public TextureRegion getHUDElement(String asset){
		return getRegion(HUD, asset);
	}
	
	public TextureRegion getRegion(String atlas, String asset){
		return manager.get(atlas,TextureAtlas.class).findRegion(asset);
	}
	
}
