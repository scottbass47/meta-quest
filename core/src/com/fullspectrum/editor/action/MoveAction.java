package com.fullspectrum.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.editor.LevelEditor;
import com.fullspectrum.editor.Selectable;
import com.fullspectrum.editor.command.MoveCommand;

public class MoveAction extends Action{

	private Array<Selectable<?>> selected;
	private Vector2 offset;
	private Vector2 start;
	private Vector2 randStart;
	private SelectAction selectAction;
	
	@Override
	public void init() {
		offset = new Vector2();
	}

	@Override
	public void update(float delta) {
		for(Selectable<?> select : selected) {
			select.update(delta, editor);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();
		for(Selectable<?> select : selected) {
			select.render(batch, select.getPosition(offset), editor);
		}
		batch.end();
	}

	public Array<Selectable<?>> getSelected() {
		return selected;
	}

	// Entry point for move command
	public void setSelected(Array<Selectable<?>> selected, LevelEditor editor, SelectAction selectAction, boolean copied) {
		this.selected = selected;
		this.selectAction = selectAction;
		if(!copied) {
			for(Selectable<?> select : selected) {
				select.remove(editor);
			}
		}
		randStart = selected.first().getPosition(offset);
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Vector2 worldCoords = editor.toWorldCoords(editor.toHudCoords(screenX, screenY));
		offset = worldCoords.sub(start);
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		move();
		return false;
	}
	
	public void setStart(Vector2 start) {
		this.start = start;
	}
	
	public Vector2 getStart() {
		return start;
	}
	
	public void move() {
		editor.executeCommand(new MoveCommand(selected, offset));
		
		actionManager.switchAction(EditorActions.SELECT);
		SelectAction selectAction = (SelectAction) actionManager.getCurrentActionInstance();
		
		Vector2 shiftAmount = selected.first().getPosition(Vector2.Zero).sub(randStart);
		selectAction.set(shiftAmount, this.selectAction);
		
	}
}
