package com.cpubrew.editor.mapobject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.editor.Interactable;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.command.Command;
import com.cpubrew.editor.command.PlaceMapObjectCommand;
import com.cpubrew.utils.Maths;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class MapObject implements Interactable<MapObject> {

	// Basic Info
	private int id; // Every MapObject needs a unique ID per map
	private MapObjectType type;
	private Vector2 pos;
	private Rectangle hitbox;
	private LevelEditor editor;
	protected float animTime; // Convenient timer for animations
	
	// Rendering
	private MapObjectRenderer renderer;
	
	// Data
	private MapObjectData data;
	
	public MapObject() {
		this(-1, null, null);
	}
	
	public MapObject(int id, MapObjectType type, LevelEditor editor) {
		this.id = id;
		this.type = type;
		this.editor = editor;
		pos = Vector2.Zero;
	}
	
	@Override
	public void update(float delta, LevelEditor editor) {
		animTime += delta;
	}

	@Override
	public Vector2 getPosition(Vector2 offset) {
		return offset.add(pos);
	}

	@Override
	public void render(SpriteBatch batch, Vector2 worldPos, LevelEditor editor) {
		// We handle input in the render method because update doesn't get called frequently enough to
		// catch just pressed keyboard events
//		if(Gdx.input.isKeyJustPressed(Keys.R)) {
//			facingRight = !facingRight;
//		}

		renderer.setAnimTime(animTime);
		renderer.render(batch, worldPos);
		
//		float x = worldPos.x;
//		float y = worldPos.y;
//		
//		int row = Maths.toGridCoord(y);
//		
//		// TEMPORARY: Using drill gremlin for testing purposes
//		Animation<TextureRegion> idle = EntityIndex.DRILL_GREMLIN.getIdleAnimation();
//		Rectangle rect = EntityIndex.DRILL_GREMLIN.getHitBox();
//		TextureRegion region = idle.getKeyFrame(animTime);
//		float w = region.getRegionWidth();
//		float h = region.getRegionHeight();
//		
//		float adjustedY = row + GameVars.PPM_INV * (rect.height * 0.5f);
//		float yy =  adjustedY - h * 0.5f;
//		
//		float hitX = x - GameVars.PPM_INV * (rect.width * 0.5f);
//		float hitY = yy + h * 0.5f - GameVars.PPM_INV * (rect.height * 0.5f);
//		
//		if(collidingWithMap(hitX, hitY, GameVars.PPM_INV * rect.width, GameVars.PPM_INV * rect.height, editor)){
//			batch.setColor(Color.RED);
//		} 
//		
////		if(!facingRight) {
////			region.flip(true, false);
////		}
//		
//		batch.draw(region, x - w * 0.5f, yy, w * 0.5f, h * 0.5f, w, h, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f);
//
//		region.flip(region.isFlipX(), false);
//		batch.setColor(Color.WHITE);
	}

	private boolean collidingWithMap(float x, float y, float width, float height, LevelEditor editor) {
		int minRow = Math.abs(y - (int) y) < 0.0005f ? (int)y : Maths.toGridCoord(y);
		int minCol = Maths.toGridCoord(x);
		int maxRow = Maths.toGridCoord(y + height);
		int maxCol = Maths.toGridCoord(x + width);
		
		for(int row = minRow; row <= maxRow; row++) {
			for(int col = minCol; col <= maxCol; col++) {
				if(editor.contains(row, col) && editor.getTile(row, col) != null && editor.getTile(row, col).isSolid()) return true;
			}
		}
		return false;
	}

	/**
	 * Creates a copy of this <code>MapObject</code> performing a deep copy on all state values
	 * @return
	 */
	private MapObject deepCopy() {
		MapObject copy = new MapObject();
		copy.id = id;
		copy.type = type;
		copy.animTime = animTime;
		copy.pos = new Vector2(pos);
		copy.hitbox = new Rectangle(hitbox);
		copy.editor = editor;
		copy.renderer = renderer.createCopy();
		copy.data = data.createCopy();
		
		return copy;
	}
	
	@Override
	public Interactable<MapObject> copy(LevelEditor editor) {
		MapObject copy = deepCopy();
		copy.id = editor.nextID();
		return copy;
	}

	@Override
	public void remove(LevelEditor editor) {
		editor.removeMapObject(id);
	}

	@Override
	public boolean contentsEqual(MapObject value) {
		return equals(value);
	}

	@Override
	public void move(Vector2 position, LevelEditor editor) {
		
	}

	@Override
	public void add(Vector2 position, LevelEditor editor) {
		
	}

	@Override
	public Command onPlace(Vector2 mousePos, LevelEditor editor) {
		MapObject copy = (MapObject) copy(editor);
		copy.setPos(mousePos);
		
		return new PlaceMapObjectCommand(copy);
	}

	@Override
	public boolean placeOnClick() {
		return false;
	}
	
	/**
	 * Ignores the snapping policy. Avoid directly setting position, use <code>move</code> instead.
	 * @param pos
	 */
	public void setPos(Vector2 pos) {
		this.pos = pos;
	}
	
	public Vector2 getPos() {
		return pos;
	}
	
	public int getId() {
		return id;
	}
	
	public void setType(MapObjectType type) {
		this.type = type;
	}
	
	public MapObjectType getType() {
		return type;
	}
	
	public void setEditor(LevelEditor editor) {
		this.editor = editor;
	}
	
	public LevelEditor getEditor() {
		return editor;
	}
	
	public void setRenderer(MapObjectRenderer renderer) {
		this.renderer = renderer;
	}
	
	public MapObjectRenderer getRenderer() {
		return renderer;
	}
	
	public void setData(MapObjectData data) {
		this.data = data;
	}
	
	public MapObjectData getData() {
		return data;
	}
	
	public Rectangle getHitbox() {
		return hitbox;
	}

	public void setHitbox(Rectangle hitbox) {
		this.hitbox = hitbox;
	}

	@Override
	public String toString() {
		return type + " [ " + id + "] @ " + pos;
	}
	
	// SERIALIZATION
	// ---------------------------------------------
	
	public static MapObjectSerializer getSerializer() {
		return new MapObjectSerializer();
	}
	
	public static class MapObjectSerializer extends Serializer<MapObject> {

		@Override
		public void write(Kryo kryo, Output output, MapObject object) {
			kryo.writeClassAndObject(output, object.type); 
			
			output.writeInt(object.id);
			output.writeFloat(object.pos.x);
			output.writeFloat(object.pos.y);
			kryo.writeClassAndObject(output, object.data);
		}

		@Override
		public MapObject read(Kryo kryo, Input input, Class<MapObject> type) {
			MapObjectType mtype = (MapObjectType) kryo.readClassAndObject(input);
			
			MapObject mobj = mtype.createDefault();
			mobj.id = input.readInt();
			mobj.type = mtype;
			mobj.pos = new Vector2(input.readFloat(), input.readFloat());
			mobj.data = (MapObjectData) kryo.readClassAndObject(input);
			
			return mobj;
		}
	}
	
}
