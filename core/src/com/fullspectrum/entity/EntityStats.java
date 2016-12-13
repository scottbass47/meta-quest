package com.fullspectrum.entity;

import com.badlogic.gdx.math.Rectangle;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class EntityStats {

	private final float runSpeed;
	private final float jumpForce;
	private final float climbSpeed;
	private final float airSpeed;
	private final Rectangle hitBox;
	private final EntityType type;

	public EntityStats(float runSpeed, float jumpForce, float climbSpeed, float airSpeed, Rectangle hitBox, EntityType type) {
		this.runSpeed = runSpeed;
		this.jumpForce = jumpForce;
		this.climbSpeed = climbSpeed;
		this.airSpeed = airSpeed;
		this.hitBox = hitBox;
		this.type = type;
	}
	
	public float getRunSpeed() {
		return runSpeed;
	}

	public float getJumpForce() {
		return jumpForce;
	}

	public float getClimbSpeed() {
		return climbSpeed;
	}
	
	public float getAirSpeed(){
		return airSpeed;
	}
	
	public Rectangle getHitBox() {
		return hitBox;
	}

	public EntityType getType() {
		return type;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(airSpeed);
		result = prime * result + Float.floatToIntBits(climbSpeed);
		result = prime * result + ((hitBox == null) ? 0 : hitBox.hashCode());
		result = prime * result + Float.floatToIntBits(jumpForce);
		result = prime * result + Float.floatToIntBits(runSpeed);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EntityStats other = (EntityStats) obj;
		if (Float.floatToIntBits(airSpeed) != Float.floatToIntBits(other.airSpeed)) return false;
		if (Float.floatToIntBits(climbSpeed) != Float.floatToIntBits(other.climbSpeed)) return false;
		if (hitBox == null) {
			if (other.hitBox != null) return false;
		} else if (!hitBox.equals(other.hitBox)) return false;
		if (Float.floatToIntBits(jumpForce) != Float.floatToIntBits(other.jumpForce)) return false;
		if (Float.floatToIntBits(runSpeed) != Float.floatToIntBits(other.runSpeed)) return false;
		if (type != other.type) return false;
		return true;
	}

	public static EntityStatsSerializer getSerializer(){
		return new EntityStatsSerializer();
	}
	
	public static class EntityStatsSerializer extends Serializer<EntityStats>{
		@Override
		public void write(Kryo kryo, Output output, EntityStats object) {
			output.writeFloat(object.runSpeed);
			output.writeFloat(object.jumpForce);
			output.writeFloat(object.climbSpeed);
			output.writeFloat(object.airSpeed);
			output.writeFloat(object.hitBox.width);
			output.writeFloat(object.hitBox.height);
			output.writeString(object.type.name());
		}

		@Override
		public EntityStats read(Kryo kryo, Input input, Class<EntityStats> type) {
			float runSpeed = input.readFloat();
			float jumpForce = input.readFloat();
			float climbSpeed = input.readFloat();
			float airSpeed = input.readFloat();
			float width = input.readFloat();
			float height = input.readFloat();
			Rectangle hitBox = new Rectangle(0, 0, width, height);
			EntityType eType = EntityType.valueOf(input.readString());
			return new EntityStats(runSpeed, jumpForce, climbSpeed, airSpeed, hitBox, eType);
		}
	}

	public static class Builder {
		private float runSpeed;
		private float jumpForce;
		private float climbSpeed;
		private float airSpeed;
		private Rectangle hitBox;
		private EntityType type;

		public Builder(EntityType type) {
			this.type = type;
		}

		public Builder setRunSpeed(float runSpeed) {
			this.runSpeed = runSpeed;
			return this;
		}
		
		public Builder setJumpForce(float jumpForce) {
			this.jumpForce = jumpForce;
			return this;
		}
		
		public Builder setClimbSpeed(float climbSpeed) {
			this.climbSpeed = climbSpeed;
			return this;
		}
		
		public Builder setAirSpeed(float airSpeed){
			this.airSpeed = airSpeed;
			return this;
		}
		
		public Builder setHitBox(Rectangle hitBox){
			this.hitBox = hitBox;
			return this;
		}
		
		public Builder setType(EntityType type) {
			this.type = type;
			return this;
		}
		
		public EntityStats build(){
			return new EntityStats(runSpeed, jumpForce, climbSpeed, airSpeed, hitBox, type);
		}
	}

}
