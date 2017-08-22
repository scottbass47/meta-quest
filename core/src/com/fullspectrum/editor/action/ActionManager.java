package com.fullspectrum.editor.action;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.editor.LevelEditor;
import com.fullspectrum.editor.PlaceableTile;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.LevelUtils;
import com.fullspectrum.level.tiles.TilesetTile;

public class ActionManager implements InputProcessor {

	private EditorActions currentAction;
	private EditorActions previousAction;
	private Action currentActionInstance;
	private Action previousActionInstance;
	
	private LevelEditor editor;
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;

	public ActionManager(LevelEditor editor) {
		this.editor = editor;
		switchAction(EditorActions.SELECT);
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

	public Action getCurrentActionInstance() {
		return currentActionInstance;
	}

	public Action getPreviousActionInstance() {
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
	public boolean keyDown(int keycode) {
		currentActionInstance.keyDown(keycode);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		currentActionInstance.keyUp(keycode);

		if(keycode == Keys.ESCAPE) {
			switchAction(EditorActions.SELECT);
		}
		
		if (editor.ctrlDown() && !currentActionInstance.isBlocking()) {
			// Save
			if (keycode == Keys.X) {
				Level currentLevel = editor.getCurrentLevel();
				System.out.println("Saving level " + currentLevel.getInfo());
				LevelUtils.saveLevel(currentLevel);
				editor.saved();
			} 
			// Erase
			else if(keycode == Keys.E) {
				switchAction(EditorActions.ERASE);
			}
			// Auto Tile
			else if(keycode == Keys.A) {
				editor.setAutoTiling(!editor.isAutoTiling());
			}
			// Enemy Panel
			else if(keycode == Keys.Q) {
				switchAction(EditorActions.SELECT_ENEMY);
			}
			// Level Trigger Panel
			else if(keycode == Keys.W) {
				
			}
			// Undo
			else if(keycode == Keys.Z) {
				editor.undo();
			}
			// Fill
			else if(keycode == Keys.F && editor.getTilePanel().getActiveTile() != null) {
				switchAction(EditorActions.FILL);
			}
			// Help
			else if(keycode == Keys.H) {
				switchAction(EditorActions.HELP);
			}
		}

		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		currentActionInstance.keyTyped(character);
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		currentActionInstance.touchDown(screenX, screenY, pointer, button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		currentActionInstance.touchUp(screenX, screenY, pointer, button);
		
		Vector2 mousePos = editor.toHudCoords(screenX, screenY);
		if(editor.onTilePanel(mousePos.x, mousePos.y) && !currentActionInstance.isBlocking()) {
			TilesetTile tile = editor.getTilePanel().getTileAt(mousePos.x, mousePos.y);
			if(tile != null) {
				editor.getTilePanel().setActiveTile(tile);
				switchAction(EditorActions.PLACE);
				PlaceAction placeAction = (PlaceAction) getCurrentActionInstance();
				placeAction.setPlaceable(new PlaceableTile());
			}
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		currentActionInstance.touchDragged(screenX, screenY, pointer);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		currentActionInstance.mouseMoved(screenX, screenY);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		currentActionInstance.scrolled(amount);
		return false;
	}
}
