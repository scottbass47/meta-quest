package com.fullspectrum.assets;

public enum Asset {

	// HUD
	HEALTH_BAR_EMPTY (Atlas.HUD, "healthbar_empty"),
	HEALTH_BAR_FULL  (Atlas.HUD, "healthbar_full"),
	STAMINA_BAR_EMPTY(Atlas.HUD, "staminabar_empty"),
	STAMINA_BAR_FULL (Atlas.HUD, "staminabar_full"),

	// UI
	VERTICAL_ARROW(Atlas.HUD, "vertical_arrow"),
	
	// Ability Icons
	ANTI_MAGNETIC_ARMOR_ICON (Atlas.HUD, "anti_magnetic_armor_icon"),
	PARRY_ICON				 (Atlas.HUD, "parry_icon"),
	KICK_ICON                (Atlas.HUD, "kick_icon"),
	OVERHEAD_SWING_ICON      (Atlas.HUD, "overhead_swing_icon"),
	SLAM_ICON                (Atlas.HUD, "slam_icon"),
	BLACKSMITH_ICON          (Atlas.HUD, "blacksmith_icon"),
	SPIN_SLICE_ICON          (Atlas.HUD, "spin_slice_icon"),
	DASH_SLASH_ICON          (Atlas.HUD, "dash_slash_icon"),
	TORNADO_ICON             (Atlas.HUD, "tornado_icon"),
	DYNAMITE                 (Atlas.HUD, "slingshot_icon"),
	BALLOON_ICON             (Atlas.HUD, "balloon_icon"),
	BOOMERANG_ICON           (Atlas.HUD, "boomerang_icon"),
	BOW_ICON                 (Atlas.HUD, "bow_icon"),
	EXECUTE_ICON             (Atlas.HUD, "execute_icon"),
	DASH_ICON                (Atlas.HUD, "dash_icon"),
	FLASH_POWDER_ICON        (Atlas.HUD, "flash_powder_icon"),
	VANISH_ICON              (Atlas.HUD, "smoke_bomb_icon"),
	HOMING_KNIVES_ICON       (Atlas.HUD, "homing_knives_icon"),
	POISON_DEBUFF_ICON       (Atlas.HUD, "poison_debuff_icon"),
	INSTA_WALL_ICON          (Atlas.HUD, "insta_wall_icon"),
	WIND_BURST_ICON          (Atlas.HUD, "wind_burst_icon"),

	//Knight
//	KNIGHT_CHAIN1_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_1/knight_chain1_idle_anticipation"),
	KNIGHT_CHAIN1_SWING(Atlas.ENTITY, "chain_1/knight_chain1_swing"),
	KNIGHT_CHAIN1_ANTICIPATION(Atlas.ENTITY, "chain_1/knight_chain1_anticipation"),
//	KNIGHT_CHAIN2_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_2/knight_chain2_idle_anticipation"),
//	KNIGHT_CHAIN2_SWING(Atlas.ENTITY, "chain_2/knight_chain2_swing"),
//	KNIGHT_CHAIN2_ANTICIPATION(Atlas.ENTITY, "chain_2/knight_chain2_anticipation"),
//	KNIGHT_CHAIN3_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_3/knight_chain3_idle_anticipation"),
//	KNIGHT_CHAIN3_SWING(Atlas.ENTITY, "chain_3/knight_chain3_swing"),
//	KNIGHT_CHAIN3_ANTICIPATION(Atlas.ENTITY, "chain_3/knight_chain3_anticipation"),
//	KNIGHT_CHAIN4_IDLE_ANTICIPATION(Atlas.ENTITY, "chain_4/knight_chain4_idle_anticipation"),
//	KNIGHT_CHAIN4_SWING(Atlas.ENTITY, "chain_4/knight_chain4_swing"),
//	KNIGHT_CHAIN4_ANTICIPATION(Atlas.ENTITY, "chain_4/knight_chain4_anticipation"),
		
	//Knight Player
	KNIGHT_RUN(Atlas.ENTITY, "run/knight_run"),
	KNIGHT_IDLE(Atlas.ENTITY, "idle/knight_idle"),
	KNIGHT_JUMP(Atlas.ENTITY, "jump/knight_jump"),
	KNIGHT_RISE(Atlas.ENTITY, "rise/knight_rise"),
	KNIGHT_APEX(Atlas.ENTITY, "apex/knight_apex"),
	KNIGHT_FALL(Atlas.ENTITY, "fall/knight_fall"),
	KNIGHT_ROLL(Atlas.ENTITY, "roll/knight_roll"),
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
	ROGUE_JUMP_LEGS(Atlas.ENTITY, "jump_legs/rogue_jump_legs"),
	ROGUE_JUMP_ARMS(Atlas.ENTITY, "jump_arms/rogue_jump_arms"),
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
		
	// Monk
	MONK_IDLE(Atlas.ENTITY, "idle/monk_idle"),
	MONK_RUN(Atlas.ENTITY, "run/monk_run"),
	MONK_JUMP(Atlas.ENTITY, "jump/monk_jump"),
	MONK_RISE(Atlas.ENTITY, "rise/monk_rise"),
	MONK_APEX(Atlas.ENTITY, "apex/monk_apex"),
	MONK_FALL(Atlas.ENTITY, "fall/monk_fall"),
	MONK_DASH(Atlas.ENTITY, "dash/monk_dash"),
	MONK_ATTACK_FRONT(Atlas.ENTITY, "swing_attack_front/monk_attack_front"),
	MONK_ATTACK_UP(Atlas.ENTITY, "swing_attack_up/monk_swing_attack_up"),
	MONK_ATTACK_FRONT_ANTICIPATION(Atlas.ENTITY, "swing_attack_front/monk_attack_front_anticipation"),
	MONK_ATTACK_UP_ANTICIPATION(Atlas.ENTITY, "swing_attack_up/monk_swing_attack_up_anticipation"),
	MONK_INSTA_WALL_TILE(Atlas.ENTITY, "insta_wall/monk_wall_tile"),
	MONK_STAFF_SLAM(Atlas.ENTITY, "insta_wall/monk_staff_slam"),
	MONK_WIND(Atlas.ENTITY, "wind/monk_wind"),
	MONK_WIND_BURST(Atlas.ENTITY, "wind_burst/monk_wind_burst"),
	
	// Coins
	COIN_SILVER(Atlas.ENTITY, "coin_silver"),
	COIN_GOLD(Atlas.ENTITY, "coin_gold"),
	COIN_BLUE(Atlas.ENTITY, "coin_blue"),
		
	// Boar
	BOAR_IDLE(Atlas.ENTITY, "idle/boar_idle"),
	BOAR_WALK(Atlas.ENTITY, "walk/boar_walk"),
	BOAR_CHARGE(Atlas.ENTITY, "charge/boar_charge"),
	BOAR_CHARGE_ANTICIPATION(Atlas.ENTITY, "charge_anticipation/boar_charge_anticipation"),
	BOAR_CHARGE_COOLDOWN(Atlas.ENTITY, "charge_cooldown/boar_charge_cooldown"),
	
	// Goat
	GOAT_IDLE(Atlas.ENTITY, "idle/goat_idle"),
	GOAT_WALK(Atlas.ENTITY, "walk/goat_walk"),
	GOAT_SWING_ATTACK(Atlas.ENTITY, "swing/goat_swing"),
	
	// Gun Gremlin
	GUN_GREMLIN_IDLE         (Atlas.ENTITY, "idle/gun_gremlin_idle"),
	GUN_GREMLIN_WALK         (Atlas.ENTITY, "walk/gun_gremlin_walk"),
	GUN_GREMLIN_RUN          (Atlas.ENTITY, "run/gun_gremlin_run"),
	GUN_GREMLIN_JUMP         (Atlas.ENTITY, "jump/gun_gremlin_jump"),
	GUN_GREMLIN_RISE         (Atlas.ENTITY, "rise/gun_gremlin_rise"),
	GUN_GREMLIN_APEX         (Atlas.ENTITY, "apex/gun_gremlin_apex"),
	GUN_GREMLIN_FALL         (Atlas.ENTITY, "fall/gun_gremlin_fall"),
	GUN_GREMLIN_SHOOT        (Atlas.ENTITY, "shoot/gun_gremlin_shoot"),
	GUN_GREMLIN_MUZZLE_FLASH (Atlas.ENTITY, "muzzle_flash/gun_gremlin_muzzle_flash"),
	GUN_GREMLIN_IDLE_RANDOM  (Atlas.ENTITY, "idle_random/gun_gremlin_idle_random"),
		
	// Rocky
	ROCKY_IDLE		 (Atlas.ENTITY, "idle/rocky_idle"),
	ROCKY_WALK		 (Atlas.ENTITY, "walk/rocky_walk"),
	ROCKY_SWING		 (Atlas.ENTITY, "swing/rocky_swing"),
	ROCKY_THROW 	 (Atlas.ENTITY, "throw/rocky_throw"),
	ROCKY_PROJECTILE (Atlas.ENTITY, "projectile/rocky_projectile"),
	
	// Grunt Gremlin
	GRUNT_GREMLIN_IDLE  (Atlas.ENTITY, "idle/grunt_gremlin_idle"),
	GRUNT_GREMLIN_WALK  (Atlas.ENTITY, "walk/grunt_gremlin_walk"),
	GRUNT_GREMLIN_RUN   (Atlas.ENTITY, "run/grunt_gremlin_run"),
	GRUNT_GREMLIN_TRIP  (Atlas.ENTITY, "trip/grunt_gremlin_trip"),
	GRUNT_GREMLIN_JUMP  (Atlas.ENTITY, "jump/grunt_gremlin_jump"),
	GRUNT_GREMLIN_RISE  (Atlas.ENTITY, "rise/grunt_gremlin_rise"),
	GRUNT_GREMLIN_APEX  (Atlas.ENTITY, "apex/grunt_gremlin_apex"),
	GRUNT_GREMLIN_FALL  (Atlas.ENTITY, "fall/grunt_gremlin_fall"),
	GRUNT_GREMLIN_SWING (Atlas.ENTITY, "swing/grunt_gremlin_swing"),
	
	// Projectile Gremlin
	PROJECTILE_GREMLIN_IDLE       (Atlas.ENTITY, "idle/projectile_gremlin_idle"),
	PROJECTILE_GREMLIN_WALK       (Atlas.ENTITY, "walk/projectile_gremlin_walk"),
	PROJECTILE_GREMLIN_SHOOT      (Atlas.ENTITY, "shoot/projectile_gremlin_shoot"),
	PROJECTILE_GREMLIN_PROJECTILE (Atlas.ENTITY, "projectile/projectile_gremlin_projectile"),
	
	// Particles
	COIN_EXPLOSION(Atlas.ENTITY, "coin_explosion"),
	MANA_BOMB_EXPLOSION(Atlas.ENTITY, "mana_bomb_explosion"),
	FLASH_POWDER_EXPLOSION(Atlas.ENTITY, "flash_powder_explosion"),
	SMOKE_BOMB(Atlas.ENTITY, "smoke_bomb"),
	JUMP_PARTICLE(Atlas.ENTITY, "jump_particle"),
	POISON_PARTICLES(Atlas.ENTITY, "poison_particles");
	
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
