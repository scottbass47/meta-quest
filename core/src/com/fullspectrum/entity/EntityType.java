package com.fullspectrum.entity;

import com.fullspectrum.utils.StringUtils;

public enum EntityType {

	// Players
	KNIGHT,
	ROGUE,
	MONK,
	
	// Enemies
	AI_PLAYER,
	SLIME,
	SPITTER,
	WINGS,
	SPAWNER,
	
	// Drops
	COIN,
	
	// Projectiles
	BULLET,
	THROWING_KNIFE,
	HOMING_KNIFE,
	BOOMERANG,
	ARROW,
	BALLOON_PELLET,
	SPIT,
	MANA_BOMB,
	DYNAMITE,
	EXPLOSIVE_PARTICLE,
	
	// Explosives
	EXPLOSION,
	SMOKE,
	
	// Particles
	PARTICLE,
	
	// Level
	CAMERA,
	BASE_TILE,
	
	// Misc
	BALLOON_TRAP,
	LEVEL_TRIGGER,
	DAMAGE_TEXT;
	
	@Override
	public String toString() {
		return StringUtils.toTitleCase(name());
	}
}
