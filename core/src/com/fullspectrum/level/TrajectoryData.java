package com.fullspectrum.level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class TrajectoryData extends LinkData{

	public final float time;
	public Node toNode;
	public final float speed;
	public final float jumpForce;
	
	// Serialization
	protected int toRow;
	protected int toCol;
	
	public TrajectoryData(float time, Node toNode, float speed, float jumpForce){
		this.time = time;
		this.toNode = toNode;
		this.speed = speed;
		this.jumpForce = jumpForce;
	}
	
	public static TrajectoryDataSerializer getSerializer(){
		return new TrajectoryDataSerializer();
	}
	
	public static class TrajectoryDataSerializer extends Serializer<TrajectoryData>{
		@Override
		public void write(Kryo kryo, Output output, TrajectoryData object) {
			output.writeFloat(object.time);
			output.writeFloat(object.speed);
			output.writeFloat(object.jumpForce);
			output.writeShort((short)object.toNode.row);
			output.writeShort((short)object.toNode.col);
		}

		@Override
		public TrajectoryData read(Kryo kryo, Input input, Class<TrajectoryData> type) {
			float time = input.readFloat();
			float speed = input.readFloat();
			float jumpForce = input.readFloat();
			int toRow = input.readShort();
			int toCol = input.readShort();
			TrajectoryData data = new TrajectoryData(time, null, speed, jumpForce);
			data.toRow = toRow;
			data.toCol = toCol;
			return data;
		}
	}
}
