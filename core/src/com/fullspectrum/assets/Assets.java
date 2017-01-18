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
	public static final String newHud = "new_hud";
	
	// Sprites
	public static final String SPRITES = "sprites/entity_assets.atlas";
	
	// Knight
	public static final String KNIGHT_IDLE = "knight_idle";
	public static final String KNIGHT_WALK = "knight_walk";
	public static final String KNIGHT_RISE = "knight_rise";
	public static final String KNIGHT_JUMP = "knight_jump";
	public static final String KNIGHT_FALL = "knight_fall";
	public static final String KNIGHT_APEX = "knight_apex";
	public static final String KNIGHT_ATTACK_OVERHEAD = "knight_attack_overhead";
	
	// Shadow Player
	public static final String SHADOW_IDLE = "temp_player_idle";
	public static final String SHADOW_RUN = "temp_player_run";
	public static final String SHADOW_RISE = "temp_player_rise";
	public static final String SHADOW_JUMP = "temp_player_jump";
	public static final String SHADOW_FALL = "temp_player_fall";
	public static final String SHADOW_APEX = "temp_player_apex";
	public static final String SHADOW_PUNCH = "temp_player_punch";
	
	// Coins
	public static final String silverCoin = "drop_coin_silver";
	public static final String goldCoin = "drop_coin_gold";
	public static final String blueCoin = "drop_coin_blue";
	public static final String disappearCoin = "drop_coin_disappear";
	
	// Spitter
	public static final String spitterWings = "spitter_wings";
	public static final String spitterAttack = "spitter_attack";
	public static final String spitterIdle = "spitter_idle";
	public static final String spitterDeath = "spitter_death";
	public static final String spitInit = "spitter_init_projectile";
	public static final String spitFly = "spitter_flying_projectile";
	public static final String spitSplash = "spitter_splashing_projectile";
	
	// Mana Bomb
	public static final String manaBombExplosion = "mana_bomb_explosion";
	
	// Font
	public static final String font24 = "font/test24.fnt";
	public static final String font28 = "font/test28.fnt";
	
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
		manager.load(font24, BitmapFont.class);
		manager.load(font28, BitmapFont.class);
		manager.finishLoading();
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
