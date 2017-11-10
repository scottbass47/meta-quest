package com.cpubrew.editor.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.Interactable;

public class MoveCommand extends Command {

	private Array<Interactable<?>> selected;
	private Vector2 offset;
	private ArrayMap<Vector2, Interactable<?>> oldState;
	
	public MoveCommand(Array<Interactable<?>> selected, Vector2 offset) {
		super(false);
		this.selected = new Array<Interactable<?>>(selected);
		this.offset = offset;
		oldState = new ArrayMap<Vector2, Interactable<?>>();
	}
	
	@Override
	public void execute(LevelEditor editor) {
		for(Interactable<?> select : selected) {
			oldState.put(select.getPosition(Vector2.Zero), select);
			select.move(select.getPosition(offset), editor);
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		for(Interactable<?> select  : selected) select.remove(editor);
		
		for(Vector2 pos : oldState.keys()) {
			Interactable<?> select = oldState.get(pos);
			select.move(pos, editor);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Moving:");
		for(Interactable<?> select : selected) {
			builder.append("\n\tMove " + select + " to " + select.getPosition(offset));
		}
		return builder.toString();
	}
	
}
