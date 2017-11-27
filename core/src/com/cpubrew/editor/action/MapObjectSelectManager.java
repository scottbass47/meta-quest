package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.mapobject.MapObject;
import com.cpubrew.game.GameVars;

public class MapObjectSelectManager implements SelectManager<MapObject> {

	private LevelEditor editor;
	private OrthographicCamera worldCamera;
	private Array<MapObject> selected;
	private ShapeRenderer shape;
	private Vector2 start;
	private Vector2 current;
	
	// Marching Ants
	private float animTime = 0.0f;
	private float frameTime = 0.15f;
	
	public MapObjectSelectManager() {
		shape = new ShapeRenderer();
		start = new Vector2();
		current = new Vector2();
		selected = new Array<MapObject>();
	}
	
	@Override
	public void update(float delta) {
		animTime += delta;
	}
	
	@Override
	public void render(SpriteBatch batch) {
		Rectangle selectRect = getSelectRect();
		
		shape.setProjectionMatrix(worldCamera.combined);
		shape.begin(ShapeType.Line);

		if(editor.isMouseDown()) {
			shape.setColor(Color.BLACK);
			shape.rect(selectRect.x, selectRect.y, selectRect.width, selectRect.height);
		} else {
			shape.setProjectionMatrix(worldCamera.combined);
			drawMarchingAnts(shape, selectRect.x, selectRect.y, selectRect.x + selectRect.width, selectRect.y, 4);
			drawMarchingAnts(shape, selectRect.x, selectRect.y, selectRect.x, selectRect.y + selectRect.height, 4);
			drawMarchingAnts(shape, selectRect.x, selectRect.y + selectRect.height, selectRect.x + selectRect.width, selectRect.y + selectRect.height, 4);
			drawMarchingAnts(shape, selectRect.x + selectRect.width, selectRect.y, selectRect.x + selectRect.width, selectRect.y + selectRect.height, 4);
			shape.setColor(Color.BLACK);
		}
		
		shape.end();
	}
	
	@Override
	public void mouseDrag(Vector2 worldPos) {
		current.set(worldPos);
	}

	@Override
	public void mouseUp(Vector2 worldPos) {
		
	}

	@Override
	public void mouseDown(Vector2 worldPos) {
		if(getSelectRect().contains(worldPos)) {
			// Move action
			ActionManager actionManager = editor.getActionManager();
			actionManager.switchAction(EditorActions.MOVE);
			MoveAction move = (MoveAction) actionManager.getCurrentActionInstance();
			move.setSelected(selected, editor, (SelectAction) actionManager.getPreviousActionInstance(), false);
		}

		start.set(worldPos);
		current.set(start);
	}
	
	@Override
	public void onDelete() {
		
	}

	@Override
	public Array<MapObject> getSelected() {
		return selected;
	}

	@Override
	public void onPaste(Array<MapObject> clipboard) {
		
	}
	
	@Override
	public void set(Vector2 start, Vector2 end, Array<MapObject> selected) {
		
	}
	
	private void drawMarchingAnts(ShapeRenderer shape, float x1, float y1, float x2, float y2, int pixelSize) {
		int offset = ((int)(animTime / frameTime)) % pixelSize;
		
		// Scale the offset to world coords
		float scaledPix = pixelSize * GameVars.PPM_INV;
		
		float ang = MathUtils.atan2(y2 - y1, x2 - x1);
		float cos = MathUtils.cos(ang);
		float sin = MathUtils.sin(ang);
		
		if(MathUtils.isEqual(cos, 0)) cos = 0;
		if(MathUtils.isEqual(sin, 0)) sin = 0;
		
		float x = x1 + cos * offset * GameVars.PPM_INV;
		float y = y1 + sin * offset * GameVars.PPM_INV;

		while(true) {
			float xx = x + scaledPix * cos;
			float yy = y + scaledPix * sin;
			
			// If the end point is out of bounds, then apply clipping
			if((cos < 0 && xx < x2) || (cos > 0 && xx > x2) || (sin < 0 && yy < y2) || (sin > 0 && yy > y2)) {
				
				// If the starting point is out of bounds, then stop
				if((cos < 0 && x < x2) || (cos > 0 && x > x2) || (sin < 0 && y < y2) || (sin > 0 && y > y2)) break;
				
				float lenx = Math.abs(x2 - x); // length required
				xx = x + lenx * cos;
				
				float leny = Math.abs(y2 - y);
				yy = y + leny * sin;
				
				shape.line(x, y, xx, yy);
				break;
			}
			shape.line(x, y, xx, yy);
			x = xx + scaledPix * cos;
			y = yy + scaledPix * sin;
		}
	}
	
	private Rectangle getSelectRect() {
		float x = Math.min(start.x, current.x);
		float y = Math.min(start.y, current.y);
		float width = Math.abs(start.x - current.x);
		float height = Math.abs(start.y - current.y);
		
		return new Rectangle(x, y, width, height);
	}
	
	public void setEditor(LevelEditor editor) {
		this.editor = editor;
	}
	
	public void setWorldCamera(OrthographicCamera worldCamera) {
		this.worldCamera = worldCamera;
	}
	
}
