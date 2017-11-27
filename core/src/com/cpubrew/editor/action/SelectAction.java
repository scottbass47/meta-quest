package com.cpubrew.editor.action;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.Interactable;
import com.cpubrew.editor.command.DeleteCommand;
import com.cpubrew.editor.command.PasteCommand;
import com.cpubrew.editor.mapobject.MapObject;
import com.cpubrew.gui.KeyBoardManager;
import com.cpubrew.gui.KeyEvent;
import com.cpubrew.gui.MouseEvent;
import com.cpubrew.utils.Maths;

public class SelectAction extends EditorAction {
	
	private Array<Interactable<?>> selected;
	private Array<Interactable<?>> clipboard;
	private Rectangle selectRect;
	private boolean tiles = false;
//	private boolean copied = false;

	private TileSelectManager tileManager;
	private MapObjectSelectManager mapObjectManager;
	private SelectManager<? extends Interactable<?>> currentManager;
	
	public SelectAction() {
		selected = new Array<Interactable<?>>();
		clipboard = new Array<Interactable<?>>();
		selectRect = new Rectangle();
		
		tileManager = new TileSelectManager();
		mapObjectManager = new MapObjectSelectManager();
	}
	
	@Override
	public void onEnter() {
		tileManager.setEditor(editor);
		mapObjectManager.setEditor(editor);
		currentManager = mapObjectManager;
	}
	
	@Override
	public void setWorldCamera(OrthographicCamera worldCamera) {
		super.setWorldCamera(worldCamera);
		tileManager.setWorldCamera(worldCamera);
		mapObjectManager.setWorldCamera(worldCamera);
	}
	
	@Override
	public void onExit() {
	}
	
	@Override
	public void update(float delta) {
		currentManager.update(delta);
	}

	@Override
	public void render(SpriteBatch batch) {
		currentManager.render(batch);
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
			@SuppressWarnings("unchecked")
			DeleteCommand delete = new DeleteCommand((Array<Interactable<?>>) currentManager.getSelected()); // This is a bit precarious
			editor.executeCommand(delete);
			currentManager.onDelete();
			
			selected.clear();
		} else if(KeyBoardManager.isControlDown() && keycode == Keys.C) {
			clipboard.clear();
			clipboard.addAll(currentManager.getSelected());
		} else if(KeyBoardManager.isControlDown() && keycode == Keys.V && clipboard.size > 0 && false) {
			// NOT HANDLED FOR NOW
			selected.clear();
			
			PasteCommand paste = new PasteCommand(clipboard, editor);
			editor.executeCommand(paste);
			selected.addAll(paste.getSelected());
		}
	}
	

	@Override
	public void onMouseDrag(MouseEvent ev) {
		currentManager.mouseDrag(editor.toWorldCoords(ev.getX(), ev.getY()));
	}
	
	@Override
	public void onMouseDown(MouseEvent ev) {
		currentManager.mouseDown(editor.toWorldCoords(ev.getX(), ev.getY()));

		boolean tiles = currentManager instanceof TileSelectManager;
		
		if(KeyBoardManager.isShiftDown()) {
			currentManager = tileManager;
		} else {
			currentManager = mapObjectManager;
		}

		// True if selecting switched between tiles and map objects
		boolean switchedModes = (tiles && !KeyBoardManager.isShiftDown()) || (!tiles && KeyBoardManager.isShiftDown());
		
		// If we've switched modes, we need to do the mouseDown event with the new mode
		if(switchedModes) {
			currentManager.mouseDown(editor.toWorldCoords(ev.getX(), ev.getY()));
		}
	}
		
	@Override
	public void onMouseUp(MouseEvent ev) {
		currentManager.mouseUp(editor.toWorldCoords(ev.getX(), ev.getY()));
	}
	
	public void set(Vector2 start, Vector2 end, SelectAction selectAction) {
		tiles = selectAction.tiles;
		selected = selectAction.selected;
		clipboard = selectAction.clipboard;
		selectRect = selectAction.selectRect;
		
		// If tiles were moved, we have to do extra calculations
		if(tiles) {
			int deltaCol = Maths.toGridCoord(end.x - start.x);
			int deltaRow = Maths.toGridCoord(end.y - start.y);
			
			selectRect.x += deltaCol;
			selectRect.y += deltaRow;
		} else {
			selectRect.x += end.x - start.x;
			selectRect.y += end.y - start.y;
		}
	}
}
