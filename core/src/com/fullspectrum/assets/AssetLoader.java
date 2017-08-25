package com.fullspectrum.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fullspectrum.audio.Sounds;
import com.fullspectrum.game.GameVars;

public class AssetLoader {

	// Asset Manager
	private AssetManager manager;
	private static AssetLoader instance;
	
	// Nearest Scaling
	private TextureParameter texParam;
	
	// Font
	public static final String font12 = "font/millenium/millenium12.fnt";
	public static final String font18 = "font/millenium/millenium18.fnt";
	public static final String font24 = "font/millenium/millenium24.fnt";
	public static final String font28 = "font/millenium/millenium28.fnt";
	public static final String font32 = "font/millenium/millenium32.fnt";
	public static final String font36 = "font/millenium/millenium36.fnt";
	public static final String font48 = "font/millenium/millenium48.fnt";
	public static final String font72 = "font/millenium/millenium72.fnt";

	public static final String consoleMain = "font/consoleMain.fnt";
	
	// Particles
	public static final String JUMP_PARTICLE = "jump_particle";
	public static final String RUN_PARTICLE = "run_particle";
	
	private AssetLoader() {
		manager = new AssetManager();
		texParam = new TextureParameter();
		texParam.minFilter = TextureFilter.Nearest;
	}
	
	public static AssetLoader getInstance(){
		if(instance == null){
			instance = new AssetLoader();
		}
		return instance;
	}
	
	public void loadHUD(){
		manager.load(Atlas.HUD.getFilepath(), TextureAtlas.class);
		manager.finishLoading();
		
		// PERFORMANCE Temporary to get immediate feedback on whether or not assets are loaded
		for(Asset asset : Asset.values()){
			if(asset.getAtlas() != Atlas.HUD) continue;
			getRegion(asset);
		}
	}
	
	public void loadSprites(){
		manager.load(Atlas.ENTITY.getFilepath(), TextureAtlas.class);
		manager.finishLoading();

		// PERFORMANCE Temporary to get immediate feedback on whether or not assets are loaded
		for(Asset asset : Asset.values()){
			if(asset.getAtlas() != Atlas.ENTITY) continue;
			getAnimation(asset);
		}
	}
	
	public void loadFont(){
		manager.load(font12, BitmapFont.class);
		manager.load(font18, BitmapFont.class);
		manager.load(font24, BitmapFont.class);
		manager.load(font28, BitmapFont.class);
		manager.load(font32, BitmapFont.class);
		manager.load(font36, BitmapFont.class);
		manager.load(font48, BitmapFont.class);
		manager.load(font72, BitmapFont.class);
		manager.load(consoleMain, BitmapFont.class);
		manager.finishLoading();
	}
	
	public void loadSounds() {
		// PERFORMANCE Inefficent loading
		for(Sounds sound : Sounds.values()) {
			manager.load("sounds/" + sound.getFilename(), Sound.class);
		}
		manager.finishLoading();
	}
	
	public Sound getSound(Sounds sound) {
		return manager.get("sounds/" + sound.getFilename(), Sound.class);
	}
	
	public TextureRegion getRegion(Asset asset){
		return getRegion(asset.getAtlas(), asset);
	}
	
	public TextureRegion getRegion(Atlas atlas, Asset asset){
		TextureRegion region = manager.get(atlas.getFilepath(), TextureAtlas.class).findRegion(asset.getFilename());
		if(region == null){
			Gdx.app.error("Assets", asset + " was not loaded properly.");
			//throw new RuntimeException(asset + " was not loaded properly.");
		}
		return region;
	}
	
	public Animation<TextureRegion> getAnimation(Asset asset){
		return getAnimation(asset.getAtlas(), asset);
	}
	
	public Animation<TextureRegion> getAnimation(Atlas atlas, Asset asset){
		Animation<TextureRegion> animation = new Animation<TextureRegion>(GameVars.ANIM_FRAME, manager.get(atlas.getFilepath(),TextureAtlas.class).findRegions(asset.getFilename()), PlayMode.LOOP);
		if(animation.getAnimationDuration() < 0.001f){
			Gdx.app.error("Assets", asset + " was not loaded properly.");
//			throw new RuntimeException(asset + " was not loaded properly.");
		}
		return animation;
	}
	
	public BitmapFont getFont(String asset){
		return manager.get(asset, BitmapFont.class);
	}
	
	public void dispose(){
		manager.dispose();
	}
	
}
