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
	
	// Abilities
	public static final String ANTI_MAGNETIC_ARMOR_ICON = "anti_magnetic_armor_icon";
	public static final String PARRY_ICON = "parry_icon";
	public static final String KICK_ICON = "kick_icon";
	public static final String OVERHEAD_SWING_ICON = "overhead_swing_icon";
	public static final String SLAM_ICON = "slam_icon";
	public static final String BLACKSMITH_ICON = "blacksmith_icon";
	public static final String SPIN_SLICE_ICON = "spin_slice_icon";
	public static final String DASH_SLASH_ICON = "dash_slash_icon";
	public static final String TORNADO_ICON = "tornado_icon";

	// Sprites
	public static final String SPRITES = "sprites/entity_assets.atlas";
	
	// AI Player
	public static final String AI_PLAYER_IDLE = "ai_player_idle";
	public static final String AI_PLAYER_WALK = "ai_player_walk";
	public static final String AI_PLAYER_RISE = "ai_player_rise";
	public static final String AI_PLAYER_JUMP = "ai_player_jump";
	public static final String AI_PLAYER_FALL = "ai_player_fall";
	public static final String AI_PLAYER_APEX = "ai_player_apex";
	public static final String AI_PLAYER_ATTACK_OVERHEAD = "ai_player_attack_overhead";
	
	// Knight Chain
	public static final String KNIGHT_CHAIN1_IDLE_ANTICIPATION = "knight_chain1_idle_anticipation";
	public static final String KNIGHT_CHAIN1_SWING = "knight_chain1_swing";
	public static final String KNIGHT_CHAIN1_ANTICIPATION = "knight_chain1_anticipation";
	public static final String KNIGHT_CHAIN2_IDLE_ANTICIPATION = "knight_chain2_idle_anticipation";
	public static final String KNIGHT_CHAIN2_SWING = "knight_chain2_swing";
	public static final String KNIGHT_CHAIN2_ANTICIPATION = "knight_chain2_anticipation";
	public static final String KNIGHT_CHAIN3_IDLE_ANTICIPATION = "knight_chain3_idle_anticipation";
	public static final String KNIGHT_CHAIN3_SWING = "knight_chain3_swing";
	public static final String KNIGHT_CHAIN3_ANTICIPATION = "knight_chain3_anticipation";
	public static final String KNIGHT_CHAIN4_IDLE_ANTICIPATION = "knight_chain4_idle_anticipation";
	public static final String KNIGHT_CHAIN4_SWING = "knight_chain4_swing";
	public static final String KNIGHT_CHAIN4_ANTICIPATION = "knight_chain4_anticipation";
	
	// Knight Player
	public static final String KNIGHT_RUN = "knight_run";
	public static final String KNIGHT_IDLE = "knight_idle";
	public static final String KNIGHT_JUMP = "knight_jump";
	public static final String KNIGHT_RISE = "knight_rise";
	public static final String KNIGHT_APEX = "knight_apex";
	public static final String KNIGHT_FALL = "knight_fall";
	public static final String KNIGHT_LAND = "knight_land";
	public static final String KNIGHT_PARRY_BLOCK = "knight_parry_block";
	public static final String KNIGHT_PARRY_SWING = "knight_parry_swing";
	public static final String KNIGHT_KICK = "knight_kick";
	public static final String KNIGHT_OVERHEAD_SWING = "knight_overhead_swing";
	public static final String KNIGHT_SLAM = "knight_slam";
	public static final String KNIGHT_SPIN_SLICE = "knight_slam";
	public static final String KNIGHT_TORNADO_INIT = "knight_tornado_init";
	public static final String KNIGHT_TORNADO_SWING = "knight_tornado_swing";

	
	// Rogue
	public static final String ROGUE_IDLE = "rogue_idle";
	public static final String ROGUE_PROJECTILE = "rogue_projectile";
	public static final String ROGUE_RUN_LEGS = "rogue_run";
	public static final String ROGUE_RUN_ARMS = "rogue_run_arms";
	public static final String ROGUE_RUN_THROW = "rogue_run_throw";
	public static final String ROGUE_BACK_PEDAL_LEGS = "rogue_back_pedal";
	public static final String ROGUE_BACK_PEDAL_ARMS = "rogue_back_pedal_arms";
	public static final String ROGUE_BACK_PEDAL_THROW = "rogue_back_pedal_throw";
	public static final String ROGUE_APEX_LEGS = "rogue_apex";
	public static final String ROGUE_APEX_ARMS = "rogue_apex_arms";
	public static final String ROGUE_FALL_LEGS = "rogue_fall";
	public static final String ROGUE_FALL_ARMS = "rogue_fall_arms";
	public static final String ROGUE_RISE_LEGS = "rogue_rise";
	public static final String ROGUE_RISE_ARMS = "rogue_rise_arms";
	public static final String ROGUE_SLINGSHOT_ARMS = "sling_shot_arms";
	
	// Mage
	public static final String MAGE_IDLE = "mage_idle";
	public static final String MAGE_RUN = "mage_run";
	
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
	
	// Slime
	public static final String slimeApex = "slime_apex";
	public static final String slimeFall = "slime_fall";
	public static final String slimeJump = "slime_jump";
	public static final String slimeLand = "slime_land";
	public static final String slimeRise = "slime_rise";
	public static final String slimeIdle = "slime_idle";
	
	// Mana Bomb
	public static final String manaBombExplosion = "mana_bomb_explosion";
	
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
		manager.load(font12, BitmapFont.class);
		manager.load(font18, BitmapFont.class);
		manager.load(font24, BitmapFont.class);
		manager.load(font28, BitmapFont.class);
		manager.load(consoleMain, BitmapFont.class);
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
