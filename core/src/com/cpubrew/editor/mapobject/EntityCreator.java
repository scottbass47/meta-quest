package com.cpubrew.editor.mapobject;

import com.badlogic.ashley.core.Entity;

/**
 * An interface that defines how a <code>MapObject</code> is converted into an <code>Entity</code>.<br><br>
 * <b>WARNING</b>: Do not put state specific data in the <code>EntityCreator</code> because when <code>MapObject</code>s are copied, 
 * a reference to the <code>EntityCreator</code> is passed along, not a copy.
 * @author Scott
 *
 */
public interface EntityCreator {

	/**
	 * Creates an <code>Entity</code> from a <code>MapObject</code>
	 * @param me - the <code>MapObject</code> being turned into an <code>Entity</code>
	 * @return
	 */
	public Entity create(MapObject me);
	
}
