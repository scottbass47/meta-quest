package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class TrajectoryData extends LinkData{

	public final Array<Point2f> trajectory;
	public final float time;
	public Node toNode;
	public final float speed;
	public final float jumpForce;
	
	// Serialization
	protected int toRow;
	protected int toCol;
	
	public TrajectoryData(Array<Point2f> trajectory, float time, Node toNode, float speed, float jumpForce){
		this.trajectory = trajectory;
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
			output.writeInt(object.trajectory.size);
			for(Point2f point : object.trajectory){
				output.writeFloat(point.x);
				output.writeFloat(point.y);
			}
			output.writeFloat(object.time);
			output.writeFloat(object.speed);
			output.writeFloat(object.jumpForce);
			output.writeShort((short)object.toNode.row);
			output.writeShort((short)object.toNode.col);
		}

		@Override
		public TrajectoryData read(Kryo kryo, Input input, Class<TrajectoryData> type) {
			Array<Point2f> trajectory = new Array<Point2f>();
			int size = input.readInt();
			for(int i = 0; i < size; i++){
				float x = input.readFloat();
				float y = input.readFloat();
				trajectory.add(new Point2f(x, y));
			}
			float time = input.readFloat();
			float speed = input.readFloat();
			float jumpForce = input.readFloat();
			int toRow = input.readShort();
			int toCol = input.readShort();
			TrajectoryData data = new TrajectoryData(trajectory, time, null, speed, jumpForce);
			data.toRow = toRow;
			data.toCol = toCol;
			return data;
		}
	}
}
