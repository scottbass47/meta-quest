package com.cpubrew.editor.mapobject;

/**
 * By default, data classes are serialized/deserialized by the default Kryo serializer. If the data
 * can't be serialized with the default serializer, one must custom implemented in the data class and registered.
 * @author Scott
 *
 */
public interface MapObjectData {

	/** Returns a deep copy of the object's data */
	public MapObjectData createCopy();
	
}
