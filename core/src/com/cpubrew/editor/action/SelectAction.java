package com.cpubrew.editor.action;

import java.util.Iterator;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.editor.Interactable;
import com.cpubrew.editor.SelectableSpawnpoint;
import com.cpubrew.editor.SelectableTile;
import com.cpubrew.editor.command.DeleteCommand;
import com.cpubrew.editor.command.PasteCommand;
import com.cpubrew.editor.mapobject.MapObject;
import com.cpubrew.game.GameVars;
import com.cpubrew.gui.KeyBoardManager;
import com.cpubrew.gui.KeyEvent;
import com.cpubrew.gui.MouseEvent;
import com.cpubrew.level.tiles.MapTile;
import com.cpubrew.utils.Maths;

public class SelectAction extends EditorAction {
	
	private ShapeRenderer shape;
	private Array<Interactable<?>> selected;
	private Array<Interactable<?>> clipboard;
	private Vector2 startCoords;
	private Vector2 currentCoords;
	private Rectangle selectRect;
	private boolean mouseDown = false;
	private boolean tiles = false;
	private boolean areaSelected = false;
//	private boolean copied = false;
	private boolean magicSelect = false;
	
	// Marching Ants
	private float animTime = 0.0f;
	private float frameTime = 0.15f;
	
	public SelectAction() {
		startCoords = new Vector2();
		currentCoords = new Vector2();
		shape = new ShapeRenderer();
		selected = new Array<Interactable<?>>();
		clipboard = new Array<Interactable<?>>();
		selectRect = new Rectangle();
	}
	
	@Override
	public void onEnter() {
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void update(float delta) {
		animTime += delta;
		
		// HACK ughhhh...
		// Removed bad spawns
		for(Iterator<Interactable<?>> iter = selected.iterator(); iter.hasNext(); ) {
			Interactable<?> select = iter.next();
			if(select instanceof SelectableSpawnpoint) {
				SelectableSpawnpoint spawnpoint = (SelectableSpawnpoint) select;
				if(spawnpoint.disabled()) iter.remove();
			}
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		if(mouseDown || areaSelected) {
			if(magicSelect) {
				batch.setProjectionMatrix(worldCamera.combined);
				batch.begin();
				for(Interactable<?> select : selected) {
					SelectableTile selTile = (SelectableTile) select;
					batch.draw(editor.getSelectTexture(), selTile.getTile().getCol(), selTile.getTile().getRow(), 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, 16, 16, false, false);
				}
				batch.end();
			}
			else if(tiles) {
				int startRow = Maths.toGridCoord(selectRect.y);
				int startCol = Maths.toGridCoord(selectRect.x);
				int endRow = Maths.toGridCoord(selectRect.y + selectRect.height);
				int endCol = Maths.toGridCoord(selectRect.x + selectRect.width);
				
				batch.setProjectionMatrix(worldCamera.combined);
				batch.begin();
				for(int row = startRow; row <= endRow; row++) {
					for(int col = startCol; col <= endCol; col++) {
						Texture select = editor.getSelectTexture();
						batch.draw(select, col, row, 0.0f, 0.0f, 16.0f, 16.0f, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f, 0, 0, 16, 16, false, false);
					}
				}
				batch.end();
			} else {
				if(mouseDown) {
					shape.setProjectionMatrix(worldCamera.combined);
					shape.begin(ShapeType.Line);
					shape.setColor(Color.BLACK);
					shape.rect(selectRect.x, selectRect.y, selectRect.width, selectRect.height);
					shape.end();
				} else {
					shape.setProjectionMatrix(worldCamera.combined);
					shape.begin(ShapeType.Line);
					drawMarchingAnts(shape, selectRect.x, selectRect.y, selectRect.x + selectRect.width, selectRect.y, 4);
					drawMarchingAnts(shape, selectRect.x, selectRect.y, selectRect.x, selectRect.y + selectRect.height, 4);
					drawMarchingAnts(shape, selectRect.x, selectRect.y + selectRect.height, selectRect.x + selectRect.width, selectRect.y + selectRect.height, 4);
					drawMarchingAnts(shape, selectRect.x + selectRect.width, selectRect.y, selectRect.x + selectRect.width, selectRect.y + selectRect.height, 4);
					shape.setColor(Color.BLACK);
					shape.end();
				}
			}
		} 
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
	
	public boolean isSelected(MapObject mobj) {
		for(Interactable<?> sel : selected) {
			if(sel instanceof MapObject) {
				MapObject selSpawn = (MapObject) sel;
				if(selSpawn.contentsEqual(mobj)) return true;
			}
		}
		return false;
	}
	
	@Override
	public void onKeyRelease(KeyEvent ev) {
		int keycode = ev.getKey();
		if(keycode == Keys.DEL) {
			DeleteCommand delete = new DeleteCommand(selected);
			editor.executeCommand(delete);
			selected.clear();
			areaSelected = false;
		} else if(KeyBoardManager.isControlDown() && keycode == Keys.C) {
			clipboard.clear();
			for(Interactable<?> select : selected) {
				clipboard.add(select);
			}
		} else if(KeyBoardManager.isControlDown() && keycode == Keys.V && clipboard.size > 0) {
//			copied = true;
			selected.clear();
			
			PasteCommand paste = new PasteCommand(clipboard, editor);
			editor.executeCommand(paste);
			selected.addAll(paste.getSelected());
		}
	}
	

	@Override
	public void onMouseDrag(MouseEvent ev) {
		currentCoords.set(editor.toWorldCoords(ev.getX(), ev.getY()));
		selectRect = getSelectRect(startCoords, currentCoords, tiles);
	}
	
	@Override
	public void onMouseDown(MouseEvent ev) {
		Vector2 coords = new Vector2(ev.getX(), ev.getY());
		Vector2 worldCoords = editor.toWorldCoords(coords);
		startCoords = editor.toWorldCoords(coords.x, coords.y);
		currentCoords.set(startCoords);
		
		// On the selected region
		boolean onSelection = false;
		if(magicSelect) {
			for(Interactable<?> select : selected) {
				SelectableTile selTile = (SelectableTile) select;
				MapTile tile = selTile.getTile();
				int row = Maths.toGridCoord(worldCoords.y);
				int col = Maths.toGridCoord(worldCoords.x);
				
				if(tile.getRow() == row && tile.getCol() == col) {
					onSelection = true;
					break;
				}
			}
		}
		
		if((magicSelect && onSelection) || (!magicSelect && areaSelected && selectRect.contains(worldCoords))) {
			// Move stuff
			actionManager.switchAction(EditorActions.MOVE);
			
			MoveAction moveAction = (MoveAction) actionManager.getCurrentActionInstance();
			moveAction.setStart(worldCoords);
			moveAction.setSelected(selected, editor, this, /*copied*/false);
		} else {
//			copied = false;
			selected.clear();
			mouseDown = true;
			areaSelected = false;
			tiles = KeyBoardManager.isShiftDown();
			magicSelect = false;
			selectRect.x = startCoords.x;
			selectRect.y = startCoords.y;
			selectRect.width = 0.0f;
			selectRect.height = 0.0f;
		}		
	}
		
	@Override
	public void onMouseUp(MouseEvent ev) {
		Vector2 hudCoords = new Vector2(ev.getX(), ev.getY());
		Vector2 endCoords = editor.toWorldCoords(hudCoords.x, hudCoords.y);
		
		selectRect = getSelectRect(startCoords, endCoords, tiles);
		
		// Select tiles
		if(KeyBoardManager.isControlDown()) {
			magicSelect = true;
			int row = Maths.toGridCoord(endCoords.y);
			int col = Maths.toGridCoord(endCoords.x);
			magicSelectTiles(row, col);
		} else if (tiles) {
			int startRow = Maths.toGridCoord(selectRect.y);
			int startCol = Maths.toGridCoord(selectRect.x);
			int endRow = Maths.toGridCoord(selectRect.y + selectRect.height);
			int endCol = Maths.toGridCoord(selectRect.x + selectRect.width);
			
			for(int row = startRow; row <= endRow; row++) {
				for(int col = startCol; col <= endCol; col++) {
					if(editor.getTile(row, col) != null) {
						selected.add(new SelectableTile(editor.getTile(row, col)));
					}
				}
			}
			
		} else {
			for(MapObject mobj : editor.getEnabledMapObjects()) {
				Vector2 spawnPoint = mobj.getPos();
				Rectangle hitbox = mobj.getHitbox();
				
				float lowerX = spawnPoint.x - hitbox.width * 0.5f;
				float lowerY = spawnPoint.y - hitbox.height * 0.5f;
				
				Rectangle collision = new Rectangle(lowerX, lowerY, hitbox.width, hitbox.height);
				
				if(selectRect.overlaps(collision)) {
					selected.add(mobj);
				}
			}
		}
		areaSelected = selected.size > 0;
		mouseDown = false;
	}
	
	private void magicSelectTiles(int row, int col) {
		if(editor.getTile(row, col) == null) return;
		recursiveMagicSelect(row, col, new ObjectSet<MapTile>());
	}
	
	private void recursiveMagicSelect(int row, int col, ObjectSet<MapTile> visited) {
		MapTile tile = editor.getTile(row, col);
		visited.add(tile);
		selected.add(new SelectableTile(tile));
		
		if(editor.getTile(row + 1, col) != null && !visited.contains(editor.getTile(row + 1, col))) {
			recursiveMagicSelect(row + 1, col, visited);
		}
		
		if(editor.getTile(row - 1, col) != null && !visited.contains(editor.getTile(row - 1, col))) {
			recursiveMagicSelect(row - 1, col, visited);
		}
		
		if(editor.getTile(row, col + 1) != null && !visited.contains(editor.getTile(row, col + 1))) {
			recursiveMagicSelect(row, col + 1, visited);
		}
		
		if(editor.getTile(row, col - 1) != null && !visited.contains(editor.getTile(row, col - 1))) {
			recursiveMagicSelect(row, col - 1, visited);
		}
	}

	private Rectangle getSelectRect(Vector2 start, Vector2 end, boolean tiles) {
		if(tiles) {
			int startRow = Maths.toGridCoord(start.y < end.y ? start.y : end.y);
			int startCol = Maths.toGridCoord(start.x < end.x ? start.x : end.x);
			return new Rectangle(
					startCol, 
					startRow, 
					Math.abs(Maths.toGridCoord(start.x > end.x ? start.x : end.x) - startCol) + 0.99f, 
					Math.abs(Maths.toGridCoord(start.y > end.y ? start.y : end.y) - startRow) + 0.99f
			);
		}
		
		return new Rectangle(
				Math.min(start.x, end.x), 
				Math.min(start.y, end.y), 
				Math.abs(end.x - start.x),
				Math.abs(end.y - start.y)
		);
	}
	
	public void set(Vector2 shiftAmount, SelectAction selectAction) {
		areaSelected = true;
		selected = selectAction.selected;
		clipboard = selectAction.clipboard;
		selectRect = selectAction.selectRect;
		selectRect.x += shiftAmount.x;
		selectRect.y += shiftAmount.y;
		tiles = selectAction.tiles;
		mouseDown = false;
		magicSelect = selectAction.magicSelect;
	}
}
