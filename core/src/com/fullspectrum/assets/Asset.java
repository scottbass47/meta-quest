package com.fullspectrum.assets;

public enum Asset {

	// HUD
	HEALTH_BAR_EMPTY(Atlas.HUD, "healthbar_empty"),
	HEALTH_BAR_FULL(Atlas.HUD, "healthbar_full"),
	STAMINA_BAR_EMPTY(Atlas.HUD, "staminabar_empty"),
	STAMINA_BAR_FULL(Atlas.HUD, "staminabar_full"),

	// Ability Icons
	ANTI_MAGNETIC_ARMOR_ICON(Atlas.HUD, "anti_magnetic_armor_icon"),
	PARRY_ICON(Atlas.HUD, "parry_icon"),
	KICK_ICON(Atlas.HUD, "kick_icon"),
	OVERHEAD_SWING_ICON(Atlas.HUD, "overhead_swing_icon"),
	SLAM_ICON(Atlas.HUD, "slam_icon"),
	BLACKSMITH_ICON(Atlas.HUD, "blacksmith_icon"),
	SPIN_SLICE_ICON(Atlas.HUD, "spin_slice_icon"),
	DASH_SLASH_ICON(Atlas.HUD, "dash_slash_icon"),
	TORNADO_ICON(Atlas.HUD, "tornado_icon"),
	DYNAMITE(Atlas.HUD, "slingshot_icon"),
	BALLOON_ICON(Atlas.HUD, "balloon_icon"),
	BOOMERANG_ICON(Atlas.HUD, "boomerang_icon"),
	BOW_ICON(Atlas.HUD, "bow_icon"),
	EXECUTE_ICON(Atlas.HUD, "execute_icon"),
	DASH_ICON(Atlas.HUD, "dash_icon"),
	FLASH_POWDER_ICON(Atlas.HUD, "flash_powder_icon"),
	VANISH_ICON(Atlas.HUD, "smoke_bomb_icon"),
	HOMING_KNIVES_ICON(Atlas.HUD, "homing_knives_icon"),

	//Knight
	KNIGHT_CHAIN1_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_1/knight_chain1_idle_anticipation"),
	KNIGHT_CHAIN1_SWING(Atlas.ENTITY, "chain_1/knight_chain1_swing"),
	KNIGHT_CHAIN1_ANTICIPATION(Atlas.ENTITY, "chain_1/knight_chain1_anticipation"),
	KNIGHT_CHAIN2_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_2/knight_chain2_idle_anticipation"),
	KNIGHT_CHAIN2_SWING(Atlas.ENTITY, "chain_2/knight_chain2_swing"),
	KNIGHT_CHAIN2_ANTICIPATION(Atlas.ENTITY, "chain_2/knight_chain2_anticipation"),
	KNIGHT_CHAIN3_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_3/knight_chain3_idle_anticipation"),
	KNIGHT_CHAIN3_SWING(Atlas.ENTITY, "chain_3/knight_chain3_swing"),
	KNIGHT_CHAIN3_ANTICIPATION(Atlas.ENTITY, "chain_3/knight_chain3_anticipation"),
	KNIGHT_CHAIN4_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_4/knight_chain4_idle_anticipation"),
	KNIGHT_CHAIN4_SWING(Atlas.ENTITY, "chain_4/knight_chain4_swing"),
	KNIGHT_CHAIN4_ANTICIPATION(Atlas.ENTITY, "chain_4/knight_chain4_anticipation"),
		
	//Knight Player
	KNIGHT_RUN(Atlas.ENTITY, "run/knight_run"),
	KNIGHT_IDLE(Atlas.ENTITY, "idle/knight_idle"),
	KNIGHT_JUMP(Atlas.ENTITY, "jump/knight_jump"),
	KNIGHT_RISE(Atlas.ENTITY, "rise/knight_rise"),
	KNIGHT_APEX(Atlas.ENTITY, "apex/knight_apex"),
	KNIGHT_FALL(Atlas.ENTITY, "fall/knight_fall"),
	KNIGHT_PARRY_BLOCK(Atlas.ENTITY, "parry_block/knight_parry_block"),
	KNIGHT_PARRY_SWING(Atlas.ENTITY, "parry_swing/knight_parry_swing"),
	KNIGHT_KICK(Atlas.ENTITY, "kick/knight_kick"),
	KNIGHT_OVERHEAD_SWING(Atlas.ENTITY, "overhead_swing/knight_overhead_swing"),
	KNIGHT_SLAM(Atlas.ENTITY, "slam/knight_slam"),
	KNIGHT_SPIN_SLICE(Atlas.ENTITY, "slam/knight_slam"),
	KNIGHT_TORNADO_INIT(Atlas.ENTITY, "tornado_init/knight_tornado_init"),
	KNIGHT_TORNADO_SWING(Atlas.ENTITY, "tornado_swing/knight_tornado_swing"),
		
	// Rogue
	ROGUE_IDLE_LEGS(Atlas.ENTITY, "idle_legs/rogue_idle_legs"),
	ROGUE_IDLE_ARMS(Atlas.ENTITY, "idle_arms/rogue_idle_arms"),
	ROGUE_THROWING_KNIFE(Atlas.ENTITY, "throwing_knife/rogue_throwing_knife"),
	ROGUE_RUN_LEGS(Atlas.ENTITY, "run_legs/rogue_run_legs"),
	ROGUE_RUN_ARMS(Atlas.ENTITY, "run_arms/rogue_run_arms"),
	ROGUE_THROW_ARMS(Atlas.ENTITY, "throw_arms/rogue_throw_arms"),
	ROGUE_BACK_PEDAL_LEGS(Atlas.ENTITY, "back_pedal_legs/rogue_back_pedal_legs"),
	ROGUE_BACK_PEDAL_ARMS(Atlas.ENTITY, "back_pedal_arms/rogue_back_pedal_arms"),
	ROGUE_APEX_LEGS(Atlas.ENTITY, "apex_legs/rogue_apex_legs"),
	ROGUE_APEX_ARMS(Atlas.ENTITY, "apex_arms/rogue_apex_arms"),
	ROGUE_FALL_LEGS(Atlas.ENTITY, "fall_legs/rogue_fall_legs"),
	ROGUE_FALL_ARMS(Atlas.ENTITY, "fall_arms/rogue_fall_arms"),
	ROGUE_RISE_LEGS(Atlas.ENTITY, "rise_legs/rogue_rise_legs"),
	ROGUE_RISE_ARMS(Atlas.ENTITY, "rise_arms/rogue_rise_arms"),
	ROGUE_DYNAMITE_ARMS(Atlas.ENTITY, "dynamite_arms/rogue_dynamite_arms"),
	ROGUE_DYNAMITE_PROJECTILE(Atlas.ENTITY, "dynamite_projectile/rogue_dynamite_projectile"),
	ROGUE_BOOMERANG_PROJECTILE(Atlas.ENTITY, "boomerang/rogue_boomerang"),
	ROGUE_FLASH_POWDER_ARMS(Atlas.ENTITY, "flash_powder_arms/rogue_flash_powder_arms"),
	ROGUE_HOMING_KNIVES_THROW(Atlas.ENTITY, "homing_knives_throw/rogue_homing_knives_throw"),
	ROGUE_SMOKE_BOMB_ARMS(Atlas.ENTITY, "smoke_bomb_arms/rogue_smoke_bomb_arms"),
	ROGUE_BOOMERANG_ARMS(Atlas.ENTITY, "boomerang_arms/rogue_boomerang_arms"),
	ROGUE_EXECUTE(Atlas.ENTITY, "execute/rogue_execute"),
	ROGUE_BOW(Atlas.ENTITY, "bow/rogue_bow"),
	ROGUEG_ARROW_PROJECTILE(Atlas.ENTITY, "arrow_projectile/rogue_arrow_projectile"),
	ROGUE_BALLOON_INFLATE(Atlas.ENTITY, "balloon_inflate/rogue_balloon_inflate"),
	ROGUE_BALLOON_POP(Atlas.ENTITY, "balloon_pop/rogue_balloon_pop"),
	ROGUE_BALLOON_IDLE(Atlas.ENTITY, "balloon_idle/rogue_balloon_idle"),
	ROGUE_BALLOON_PROJECTILE(Atlas.ENTITY, "balloon_projectile/rogue_balloon_projectile"),
	
		
	// Alchemist
	MONK_IDLE(Atlas.ENTITY, "idle/monk_idle"),
	MONK_RUN(Atlas.ENTITY, "run/monk_run"),
	MONK_JUMP(Atlas.ENTITY, "jump/monk_jump"),
	MONK_RISE(Atlas.ENTITY, "rise/monk_rise"),
	MONK_APEX(Atlas.ENTITY, "apex/monk_apex"),
	MONK_FALL(Atlas.ENTITY, "fall/monk_fall"),
	MONK_SWING_ATTACK_FRONT(Atlas.ENTITY, "swing_attack_front/monk_swing_attack_front"),
	MONK_SWING_ATTACK_UP(Atlas.ENTITY, "swing_attack_up/monk_swing_attack_up"),
		
	// Coins
	COIN_SILVER(Atlas.ENTITY, "coin_silver"),
	COIN_GOLD(Atlas.ENTITY, "coin_gold"),
	COIN_BLUE(Atlas.ENTITY, "coin_blue"),
		
	// Spitter
	SPITTER_WINGS(Atlas.ENTITY, "wings/spitter_wings"),
	SPITTER_ATTACK(Atlas.ENTITY, "attack/spitter_attack"),
	SPITTER_IDLE(Atlas.ENTITY, "idle/spitter_idle"),
	SPITTER_DEATH(Atlas.ENTITY, "death/spitter_death"),
	SPIT_INIT(Atlas.ENTITY, "init_projectile/spitter_init_projectile"),
	SPIT_FLY(Atlas.ENTITY, "flying_projectile/spitter_flying_projectile"),
	SPIT_DEATH(Atlas.ENTITY, "splashing_projectile/spitter_splashing_projectile"),
		
	// Slime
	SLIME_APEX(Atlas.ENTITY, "apex/slime_apex"),
	SLIME_FALL(Atlas.ENTITY, "fall/slime_fall"),
	SLIME_JUMP(Atlas.ENTITY, "jump/slime_jump"),
	SLIME_LAND(Atlas.ENTITY, "land/slime_land"),
	SLIME_RISE(Atlas.ENTITY, "rise/slime_rise"),
	SLIME_IDLE(Atlas.ENTITY, "idle/slime_idle"),
		
	// Particles
	COIN_EXPLOSION(Atlas.ENTITY, "coin_explosion"),
	MANA_BOMB_EXPLOSION(Atlas.ENTITY, "mana_bomb_explosion"),
	FLASH_POWDER_EXPLOSION(Atlas.ENTITY, "flash_powder_explosion"),
	SMOKE_BOMB(Atlas.ENTITY, "smoke_bomb"),
	JUMP_PARTICLE(Atlas.ENTITY, "jump_particle");
	
	private Atlas atlas;
	private String filename;
	
	private Asset(Atlas atlas, String filename){
		this.atlas = atlas;
		this.filename = filename;
	}
	
	public Atlas getAtlas() {
		return atlas;
	}
	
	public String getFilename() {
		return filename;
	}
}
