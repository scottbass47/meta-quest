package com.fullspectrum.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	
	// Sprites
	public static final String SPRITES = "sprites/entity_assets.atlas";
	public static final String KNIGHT_IDLE = "knight_idle";
	public static final String KNIGHT_WALK = "knight_walk";
	public static final String KNIGHT_RISE = "knight_rise";
	public static final String KNIGHT_JUMP = "knight_jump";
	public static final String KNIGHT_FALL = "knight_fall";
	public static final String KNIGHT_APEX = "knight_apex";
	public static final String KNIGHT_ATTACK_OVERHEAD = "knight_attack_overhead";
	
	// Coins
	public static final String silverCoin = "drop_coin_silver";
	public static final String goldCoin = "drop_coin_gold";
	public static final String blueCoin = "drop_coin_blue";
	
	// Font
	public static final String FONT = "font/";
	
	public static final float ANIM_SPEED = 0.1f;
	
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
	
	public void loadSprites(){
		manager.load(SPRITES, TextureAtlas.class);
		manager.finishLoading();
	}
	
	public void loadFont(){
		
	}
	
	public TextureRegion getHUDElement(String asset){
		return getRegion(HUD, asset);
	}
	
	public TextureRegion getRegion(String atlas, String asset){
		return manager.get(atlas,TextureAtlas.class).findRegion(asset);
	}
	
	public Animation getSpriteAnimation(String asset){
		return getAnimation(SPRITES, asset);
	}
	
	public Animation getAnimation(String atlas, String asset){
		return new Animation(ANIM_SPEED, manager.get(atlas,TextureAtlas.class).findRegions(asset), PlayMode.LOOP);
	}
	
	public BitmapFont getFont(String asset){
		return manager.get(asset, BitmapFont.class);
	}
	
}
