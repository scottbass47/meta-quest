package com.cpubrew.level;

import com.cpubrew.level.LinkData.LinkDataType;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NavLink {

	public final LinkType type;
	public Node fromNode;
	public Node toNode;
	public final float cost;
	public final LinkData data;
	
	// Serialization
	protected int toRow;
	protected int toCol;

	public NavLink(LinkType type, LinkData data, Node fromNode, Node toNode, float cost) {
		this.type = type;
		this.data = data;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.cost = cost;
	}

	public NavLink increaseCost(float amount) {
		NavLink link = new NavLink(type, data, fromNode, toNode, cost + amount);
		return link;
	}

	public enum LinkType {
		JUMP, JUMP_OVER, FALL, FALL_OVER, RUN, CLIMB
	}

	public boolean isDirRight() {
		return fromNode.col < toNode.col;
	}

	@Override
	public String toString() {
		return "Type: " + type + ", Cost: " + cost;
	}

	public static NavLinkSerializer getSerializer() {
		return new NavLinkSerializer();
	}

	public static class NavLinkSerializer extends Serializer<NavLink> {
		@Override
		public void write(Kryo kryo, Output output, NavLink object) {
			output.writeByte(object.type.ordinal());
			output.writeShort((short)object.toNode.row);
			output.writeShort((short)object.toNode.col);
			output.writeFloat(object.cost);
			if (object.data == null) {
				output.writeByte(LinkDataType.NULL.ordinal());
			}
			else if (object.data instanceof JumpOverData) {
				output.writeByte(LinkDataType.JUMP_OVER.ordinal());
				kryo.writeObject(output, object.data);
			}
			else if (object.data instanceof TrajectoryData) {
				output.writeByte(LinkDataType.TRAJECTORY.ordinal());
				kryo.writeObject(output, object.data);
			}
		}

		@Override
		public NavLink read(Kryo kryo, Input input, Class<NavLink> type) {
			LinkType linkType = LinkType.values()[input.readByte()];
			int toRow = input.readShort();
			int toCol = input.readShort();
			float cost = input.readFloat();
			LinkDataType dataType = LinkDataType.values()[input.readByte()];
			LinkData data = null;
			switch(dataType){
			case JUMP_OVER:
				data = kryo.readObject(input, JumpOverData.class);
				break;
			case NULL:
				break;
			case TRAJECTORY:
				data = kryo.readObject(input, TrajectoryData.class);
				break;
			}
			NavLink link = new NavLink(linkType, data, null, null, cost);
			link.toRow = toRow;
			link.toCol = toCol;
			return link;
		}
	}
}
