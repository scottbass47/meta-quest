package com.cpubrew.level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class JumpOverData extends LinkData{

	public final float jumpForce;
	
	public JumpOverData(float jumpForce){
		this.jumpForce = jumpForce;
	}
	
	public static JumpOverDataSerializer getSerializer(){
		return new JumpOverDataSerializer();
	}
	
	public static class JumpOverDataSerializer extends Serializer<JumpOverData>{
		@Override
		public void write(Kryo kryo, Output output, JumpOverData object) {
			output.writeFloat(object.jumpForce);
		}

		@Override
		public JumpOverData read(Kryo kryo, Input input, Class<JumpOverData> type) {
			return new JumpOverData(input.readFloat());
		}
	}
}
