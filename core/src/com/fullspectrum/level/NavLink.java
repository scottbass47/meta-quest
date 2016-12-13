package com.fullspectrum.level;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class NavLink {

	public final LinkType type;
	public final Node fromNode;
	public final Node toNode;
	public final float cost;
	public final LinkData data;
	
	public NavLink(LinkType type, LinkData data, Node fromNode, Node toNode, float cost){
		this.type = type;
		this.data = data;
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.cost = cost;
	}
	
	public NavLink increaseCost(float amount){
		NavLink link = new NavLink(type, data, fromNode, toNode, cost + amount);
		return link;
	}
	
	public enum LinkType {
		JUMP,
		JUMP_OVER,
		FALL,
		FALL_OVER,
		RUN,
		CLIMB
	}
	
	public boolean isDirRight(){
		return fromNode.col < toNode.col;
	}
	
	@Override
	public String toString() {
		return "Type: " + type + ", Cost: " + cost;
	}
	
	public static NavLinkSerializer getSerializer(){
		return new NavLinkSerializer();
	}
	
	public static class NavLinkSerializer extends Serializer<NavLink>{
		@Override
		public void write(Kryo kryo, Output output, NavLink object) {
			output.writeString(object.type.name());
			kryo.writeClassAndObject(output, object.fromNode);
			kryo.writeClassAndObject(output, object.toNode);
			output.writeFloat(object.cost);
			kryo.writeObjectOrNull(output, object.data, LinkData.class);
		}

		@Override
		public NavLink read(Kryo kryo, Input input, Class<NavLink> type) {
			LinkType linkType = LinkType.valueOf(input.readString());
			Node fromNode = kryo.readObject(input, Node.class);
			Node toNode = kryo.readObject(input, Node.class);
			float cost = input.readFloat();
			LinkData data = kryo.readObjectOrNull(input, LinkData.class);
			return new NavLink(linkType, data, fromNode, toNode, cost);
		}
	}
}
