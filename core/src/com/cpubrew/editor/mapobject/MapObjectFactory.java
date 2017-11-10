package com.cpubrew.editor.mapobject;

import com.badlogic.gdx.math.Rectangle;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.mapobject.data.SpawnpointData;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.game.GameVars;
import com.cpubrew.utils.Maths;

public class MapObjectFactory {

	// !!! IMPORTANT !!!
	// ALL map objects should be able to accept null arguments without crashing
	// when first initializing. This makes serialization/deserialization much
	// easier.
	
	public static MapObject createSpawnpoint(LevelEditor editor, EntityIndex index) {
		return new MapObjectBuilder(MapObjectType.SPAWNPOINT, editor)
				.data(new SpawnpointData(index))
				.render(new AnimationRenderer(index.getIdleAnimation()))
				.hitbox(Maths.scl(index.getHitBox(), GameVars.PPM_INV))
				.build();
	}
	
	public static class MapObjectBuilder {
		
		private MapObject mobj;
		
		public MapObjectBuilder(MapObjectType type, LevelEditor editor) {
			mobj = new MapObject(-1, type, editor);
		}

		public MapObjectBuilder render(MapObjectRenderer renderer){
			mobj.setRenderer(renderer);
			return this;
		}
		
		public MapObjectBuilder data(MapObjectData data) {
			mobj.setData(data);
			return this;
		}
		
		public MapObjectBuilder hitbox(float width, float height) {
			mobj.setHitbox(new Rectangle(0, 0, width, height));
			return this;
		}
		
		public MapObjectBuilder hitbox(Rectangle rectangle) {
			mobj.setHitbox(rectangle);
			return this;
		}
		
		public MapObject build() {
			return mobj;
		}
	}
}
