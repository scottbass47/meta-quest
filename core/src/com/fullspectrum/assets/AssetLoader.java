package com.fullspectrum.assets;

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

public class AssetLoader {

	// Asset Manager
	private AssetManager manager;
	private static AssetLoader instance;
	
	// Nearest Scaling
	private TextureParameter texParam;
	
	// Font
	public static final String font12 = "font/calibriLight12.fnt";
	public static final String font18 = "font/calibriLight18.fnt";
	public static final String font24 = "font/calibriLight24.fnt";
	public static final String font28 = "font/calibriLight28.fnt";
	public static final String font32 = "font/calibriLight32.fnt";
	public static final String font36 = "font/calibriLight36.fnt";
	public static final String font48 = "font/calibriLight48.fnt";
	public static final String font72 = "font/calibriLight72.fnt";

	public static final String consoleMain = "font/consoleMain.fnt";
	
	// Particles
	public static final String JUMP_PARTICLE = "jump_particle";
	public static final String RUN_PARTICLE = "run_particle";
	
	public static final float ANIM_SPEED = 0.1f;
	
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
		manager.load(Sounds.COIN_PICKUP.getFilename(), Sound.class);
		manager.finishLoading();
	}
	
	public Sound getSound(Sounds sound) {
		return manager.get(sound.getFilename(), Sound.class);
	}
	
	public TextureRegion getRegion(Asset asset){
		return getRegion(asset.getAtlas(), asset);
	}
	
	public TextureRegion getRegion(Atlas atlas, Asset asset){
		TextureRegion region = manager.get(atlas.getFilepath(), TextureAtlas.class).findRegion(asset.getFilename());
		if(region == null){
			//throw new RuntimeException(asset + " was not loaded properly.");
		}
		return region;
	}
	
	public Animation getAnimation(Asset asset){
		return getAnimation(asset.getAtlas(), asset);
	}
	
	public Animation getAnimation(Atlas atlas, Asset asset){
		Animation animation = new Animation(ANIM_SPEED, manager.get(atlas.getFilepath(),TextureAtlas.class).findRegions(asset.getFilename()), PlayMode.LOOP);
		if(animation.getAnimationDuration() < 0.001f){
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
