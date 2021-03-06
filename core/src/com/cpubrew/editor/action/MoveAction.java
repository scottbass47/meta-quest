package com.cpubrew.editor.action;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.Interactable;
import com.cpubrew.editor.command.MoveCommand;
import com.cpubrew.gui.MouseEvent;

public class MoveAction extends EditorAction{

	private Array<Interactable<?>> selected;
	private Vector2 offset;
	private Vector2 start;
//	private Vector2 randStart;
	private SelectAction selectAction;
	
	@Override
	public void onEnter() {
		offset = new Vector2();
	}

	@Override
	public void onExit() {
	}
	
	@Override
	public void update(float delta) {
		for(Interactable<?> select : selected) {
			select.update(delta, editor);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(worldCamera.combined);
		batch.begin();
		for(Interactable<?> select : selected) {
			select.render(batch, select.getPositionOff(offset), editor);
		}
		batch.end();
	}

	public Array<Interactable<?>> getSelected() {
		return selected;
	}

	// Entry point for move command
	public void setSelected(Array<Interactable<?>> selected, LevelEditor editor, SelectAction selectAction, boolean copied) {
		this.selected = selected;
		this.selectAction = selectAction;
		if(!copied) {
			for(Interactable<?> select : selected) {
				select.remove(editor);
			}
		}
//		randStart = selected.first().getPosition(offset);
	}
	
	@Override
	public void onMouseDrag(MouseEvent ev) {
		Vector2 worldCoords = editor.toWorldCoords(ev.getX(), ev.getY());
		offset = worldCoords.sub(start);
	}

	@Override
	public void onMouseUp(MouseEvent ev) {
		move();
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
		
		selectAction.set(start, offset.add(start), this.selectAction);
		
	}
}
