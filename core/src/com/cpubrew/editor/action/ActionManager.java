package com.cpubrew.editor.action;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.SelectableTile;
import com.cpubrew.gui.Action;
import com.cpubrew.gui.Component;
import com.cpubrew.gui.KeyBind;
import com.cpubrew.gui.KeyBind.FocusType;
import com.cpubrew.gui.KeyBind.Modifiers;
import com.cpubrew.gui.KeyEvent;
import com.cpubrew.gui.KeyListener;
import com.cpubrew.gui.MouseEvent;
import com.cpubrew.gui.MouseListener;
import com.cpubrew.gui.Window;
import com.cpubrew.level.Level;
import com.cpubrew.level.LevelUtils;
import com.cpubrew.level.tiles.MapTile;
import com.cpubrew.level.tiles.TilesetTile;
import com.cpubrew.level.tiles.MapTile.TileType;

public class ActionManager implements KeyListener, MouseListener {

	private EditorActions currentAction;
	private EditorActions previousAction;
	private EditorAction currentActionInstance;
	private EditorAction previousActionInstance;
	
	private LevelEditor editor;
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;

	public ActionManager(final LevelEditor editor) {
		this.editor = editor;
		switchAction(EditorActions.SELECT);
		
		Window window = editor.getEditorWindow();
		
		// Escape
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.ESCAPE), new SwitchBind(this, EditorActions.SELECT, true));

		// Erase
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.E, Modifiers.CTRL), new SwitchBind(this, EditorActions.ERASE));
		
		// Select Enemy
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.Q, Modifiers.CTRL), new SwitchBind(this, EditorActions.SELECT_ENEMY));
		
		// Fill
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.F, Modifiers.CTRL), new Action() {
			@Override
			public void onAction(Component source) {
				if(isBlocking() || editor.getTilePanel().getActiveTile() == null) return;
				switchAction(EditorActions.ERASE);
			}
		});
		
		// Help
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.H, Modifiers.CTRL), new SwitchBind(this, EditorActions.HELP));
		
		// Open level
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.O, Modifiers.CTRL), new SwitchBind(this, EditorActions.OPEN_LEVEL));
		
		// New Level
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.N, Modifiers.CTRL), new SwitchBind(this, EditorActions.NEW_LEVEL));
		
		// Undo
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.Z, Modifiers.CTRL), new Action() {
			@Override
			public void onAction(Component source) {
				if(isBlocking()) return;
				editor.undo();
			}
		});
		
		// Auto tile
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.A, Modifiers.CTRL), new Action() {
			@Override
			public void onAction(Component source) {
				if(isBlocking()) return;
				editor.setAutoTiling(!editor.isAutoTiling());
			}
		});
		
		// Save
		window.addKeyBind(new KeyBind(FocusType.WINDOW_FOCUS, Keys.X, Modifiers.CTRL), new Action() {
			@Override
			public void onAction(Component source) {
				if(isBlocking()) return;
				Level currentLevel = editor.getCurrentLevel();
				System.out.println("Saving level " + currentLevel.getName());
				LevelUtils.saveLevel(currentLevel);
				editor.saved();
			}
		});
		
		window.addKeyListener(this);
		window.addMouseListener(this);
	}

	public void update(float delta) {
		currentActionInstance.update(delta);
	}

	public void render(SpriteBatch batch) {
		currentActionInstance.render(batch);
	}

	public void switchAction(EditorActions newAction) {
		if(currentActionInstance != null) currentActionInstance.onExit();
		
		previousAction = currentAction;
		previousActionInstance = currentActionInstance;
		
		currentAction = newAction;
		currentActionInstance = newAction.getActionInstance();
		currentActionInstance.setEditor(editor);
		currentActionInstance.setWorldCamera(worldCamera);
		currentActionInstance.setHudCamera(hudCamera);
		currentActionInstance.setActionManager(this);
		
		currentActionInstance.onEnter();
	}
	
	public boolean isBlocking() {
		return currentActionInstance == null ? false : currentActionInstance.isBlocking();
	}
	
	public boolean renderInFront() {
		return currentActionInstance == null ? false : currentActionInstance.renderInFront();
	}

	public EditorActions getCurrentAction() {
		return currentAction;
	}
	
	public EditorActions getPreviousAction() {
		return previousAction;
	}

	public EditorAction getCurrentActionInstance() {
		return currentActionInstance;
	}

	public EditorAction getPreviousActionInstance() {
		return previousActionInstance;
	}
	
	public void setEditor(LevelEditor editor) {
		this.editor = editor;
	}

	public LevelEditor getEditor() {
		return editor;
	}

	public void setWorldCamera(OrthographicCamera worldCamera) {
		this.worldCamera = worldCamera;
		currentActionInstance.setWorldCamera(worldCamera);
	}

	public OrthographicCamera getWorldCamera() {
		return worldCamera;
	}

	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
		currentActionInstance.setHudCamera(hudCamera);
	}

	public OrthographicCamera getHudCamera() {
		return hudCamera;
	}

	@Override
	public void onMouseMove(MouseEvent ev) {
		currentActionInstance.onMouseMove(ev);
	}

	@Override
	public void onMouseDrag(MouseEvent ev) {
		currentActionInstance.onMouseDrag(ev);
	}

	@Override
	public void onMouseUp(MouseEvent ev) {
		currentActionInstance.onMouseUp(ev);
		
		int x = ev.getX();
		int y = ev.getY();
		
		if(editor.onTilePanel(x, y) && !currentActionInstance.isBlocking()) {
			TilesetTile tile = editor.getTilePanel().getTileAt(x, y);
			if(tile != null) {
				editor.getTilePanel().setActiveTile(tile);
				switchAction(EditorActions.PLACE);
				
				// Create a template tile with the right id
				MapTile mapTile = new MapTile(0, 0, TileType.GROUND);
				mapTile.setId(tile.getTileID());

				PlaceAction placeAction = (PlaceAction) getCurrentActionInstance();
				placeAction.setPlaceable(new SelectableTile(mapTile));
			}
		}
	}

	@Override
	public void onMouseDown(MouseEvent ev) {
		currentActionInstance.onMouseDown(ev);
	}

	@Override
	public void onMouseEnter(MouseEvent ev) {
		currentActionInstance.onMouseEnter(ev);
	}

	@Override
	public void onMouseExit(MouseEvent ev) {
		currentActionInstance.onMouseExit(ev);
	}

	@Override
	public void onKeyPress(KeyEvent ev) {
		currentActionInstance.onKeyPress(ev);
	}

	@Override
	public void onKeyRelease(KeyEvent ev) {
		currentActionInstance.onKeyRelease(ev);
	}

	@Override
	public void onKeyType(KeyEvent ev) {
		currentActionInstance.onKeyType(ev);
	}
}
