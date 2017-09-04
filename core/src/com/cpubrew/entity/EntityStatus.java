package com.cpubrew.entity;

public enum EntityStatus{
	FRIENDLY {
		@Override
		public EntityStatus getOpposite() {
			return ENEMY;
		}
	},
	ENEMY {
		@Override
		public EntityStatus getOpposite() {
			return FRIENDLY;
		}
	},
	NEUTRAL {
		@Override
		public EntityStatus getOpposite() {
			return NEUTRAL;
		}
	};

	public abstract EntityStatus getOpposite();
}