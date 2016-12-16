package com.fullspectrum.level;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fullspectrum.level.NavLink.LinkType;

public class Node{
	
	protected int row;
	protected int col;
	protected Tile tile;
	
	public NodeType type = NodeType.MIDDLE;
	private Array<NavLink> links;
	
	public Node(){
		links = new Array<NavLink>();
	}
	
	public enum NodeType{
		LEFT_EDGE,
		RIGHT_EDGE,
		SOLO,
		MIDDLE,
		LADDER
	}
	
	public void addLink(NavLink link){
		links.add(link);
	}
	
	public Array<NavLink> getLinks(){
		return links;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getCol(){
		return col;
	}
	
	public Tile getTile(){
		return tile;
	}
	
	public boolean hasLinkType(LinkType type){
		for(NavLink link : links){
			if(link.type == type) return true;
		}
		return false;
	}
	
	public Array<NavLink> getLinks(LinkType type){
		Array<NavLink> ret = new Array<NavLink>();
		for(NavLink link : links){
			if(link.type == type) ret.add(link);
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return "Row: " + row + ", Col: " + col + ", Type: " + type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Node other = (Node) obj;
		if (col != other.col) return false;
		if (row != other.row) return false;
		if (type != other.type) return false;
		return true;
	}
	
	public static NodeSerializer getSerializer(){
		return new NodeSerializer();
	}
	
	public static class NodeSerializer extends Serializer<Node>{
		@Override
		public void write(Kryo kryo, Output output, Node object) {
			output.writeShort((short)object.row);
			output.writeShort((short)object.col);
			output.writeByte(object.type.ordinal());
		}

		@Override
		public Node read(Kryo kryo, Input input, Class<Node> type) {
			Node node = new Node();
			node.row = input.readShort();
			node.col = input.readShort();
			node.type = NodeType.values()[input.readByte()];
			return node;
		}
	}

}
