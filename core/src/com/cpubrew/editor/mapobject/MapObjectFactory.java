package com.cpubrew.editor.mapobject;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.mapobject.data.SpawnpointData;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.game.GameVars;
import com.cpubrew.utils.Maths;

public class MapObjectFactory {

	// !!! IMPORTANT !!!
	// ALL map objects should be able to accept their MapObjectData as an 
	// argument (data is deserialized before the object is created, so it will
	// always be non-null).
	
	public static MapObject createSpawnpoint(LevelEditor editor, EntityIndex index) {
		return createSpawnpoint(editor, new SpawnpointData(index));
	}
	
	public static MapObject createSpawnpoint(LevelEditor editor, SpawnpointData data) {
		EntityIndex index = data.getIndex();
		return new MapObjectBuilder(MapObjectType.SPAWNPOINT, editor)
				.data(new SpawnpointData(index))
				.render(new AnimationRenderer(index.getIdleAnimation()))
				.hitbox(Maths.scl(index.getHitBox(), GameVars.PPM_INV))
				.creator(new EntityCreator() {
					@Override
					public Entity create(MapObject me) {
						SpawnpointData data = (SpawnpointData) me.getData();
						return data.getIndex().create(me.getPos().x, me.getPos().y);
					}
				})
				.build();
	}
	
	public static class MapObjectBuilder {
		
		// Mandatory Fields
		private boolean hasHitbox;
		private boolean hasCreator;
		
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
			hasHitbox = (width != 0.0f && height != 0.0f); // Hitbox can't have 0 area
			mobj.setHitbox(new Rectangle(0, 0, width, height));
			return this;
		}
		
		public MapObjectBuilder hitbox(Rectangle rectangle) {
			hasHitbox = (rectangle.getWidth() != 0.0f && rectangle.getHeight() != 0.0f); // Hitbox can't have 0 area
			mobj.setHitbox(rectangle);
			return this;
		}
		
		public MapObjectBuilder creator(EntityCreator creator) {
			hasCreator = creator != null; // EntityCreator can't be null
			mobj.setCreator(creator);
			return this;
		}
		
		public MapObject build() {
			if(!hasHitbox || !hasCreator) throw new RuntimeException("MapObject is missing mandatory fields.");
			return mobj;
		}
	}
}
