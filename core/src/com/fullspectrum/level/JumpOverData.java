package com.fullspectrum.level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class JumpOverData extends LinkData{

	public final float jumpMultiplier;
	
	public JumpOverData(float jumpMultiplier){
		this.jumpMultiplier = jumpMultiplier;
	}
	
	public static JumpOverDataSerializer getSerializer(){
		return new JumpOverDataSerializer();
	}
	
	public static class JumpOverDataSerializer extends Serializer<JumpOverData>{
		@Override
		public void write(Kryo kryo, Output output, JumpOverData object) {
			output.writeFloat(object.jumpMultiplier);
		}

		@Override
		public JumpOverData read(Kryo kryo, Input input, Class<JumpOverData> type) {
			return new JumpOverData(input.readFloat());
		}
	}
}
