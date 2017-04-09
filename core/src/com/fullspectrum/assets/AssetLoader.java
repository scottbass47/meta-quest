package com.fullspectrum.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

// CLEANUP Consistent naming for static final strings
public class AssetLoader {

	// Asset Manager
	private AssetManager manager;
	private static AssetLoader instance;
	
	// Nearest Scaling
	private TextureParameter texParam;
	
	// Font
	public static final String font12 = "font/test12.fnt";
	public static final String font18 = "font/test18.fnt";
	public static final String font24 = "font/test24.fnt";
	public static final String font28 = "font/test28.fnt";
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
		
		for(Asset asset : Asset.values()){
			if(asset.getAtlas() != Atlas.HUD) continue;
			getRegion(asset);
		}
	}
	
	public void loadSprites(){
		manager.load(Atlas.ENTITY.getFilepath(), TextureAtlas.class);
		manager.finishLoading();
		
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
		manager.load(consoleMain, BitmapFont.class);
		manager.finishLoading();
	}
	
	public TextureRegion getRegion(Asset asset){
		return getRegion(asset.getAtlas(), asset);
	}
	
	public TextureRegion getRegion(Atlas atlas, Asset asset){
		TextureRegion region = manager.get(atlas.getFilepath(), TextureAtlas.class).findRegion(asset.getFilename());
		if(region == null){
			throw new RuntimeException(asset + " was not loaded properly.");
		}
		return region;
	}
	
	public Animation getAnimation(Asset asset){
		return getAnimation(asset.getAtlas(), asset);
	}
	
	public Animation getAnimation(Atlas atlas, Asset asset){
		Animation animation = new Animation(ANIM_SPEED, manager.get(atlas.getFilepath(),TextureAtlas.class).findRegions(asset.getFilename()), PlayMode.LOOP);
		if(animation.getAnimationDuration() < 0.001f){
			throw new RuntimeException(asset + " was not loaded properly.");
		}
		return animation;
	}
	
	public BitmapFont getFont(String asset){
		return manager.get(asset, BitmapFont.class);
	}
	
}
