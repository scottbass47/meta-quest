package com.cpubrew.editor.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.Selectable;

public class MoveCommand extends Command {

	private Array<Selectable<?>> selected;
	private Vector2 offset;
	private ArrayMap<Vector2, Selectable<?>> oldState;
	
	public MoveCommand(Array<Selectable<?>> selected, Vector2 offset) {
		super(false);
		this.selected = new Array<Selectable<?>>(selected);
		this.offset = offset;
		oldState = new ArrayMap<Vector2, Selectable<?>>();
	}
	
	@Override
	public void execute(LevelEditor editor) {
		for(Selectable<?> select : selected) {
			oldState.put(select.getPosition(Vector2.Zero), select);
			select.move(select.getPosition(offset), editor);
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		for(Selectable<?> select  : selected) select.remove(editor);
		
		for(Vector2 pos : oldState.keys()) {
			Selectable<?> select = oldState.get(pos);
			select.move(pos, editor);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Moving:");
		for(Selectable<?> select : selected) {
			builder.append("\n\tMove " + select + " to " + select.getPosition(offset));
		}
		return builder.toString();
	}
	
}
