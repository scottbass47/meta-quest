package com.cpubrew.editor.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.Selectable;

public class PasteCommand extends Command{

	private Array<Selectable<?>> selected;
	
	public PasteCommand(Array<Selectable<?>> selected, LevelEditor editor) {
		super(false);
		this.selected = new Array<Selectable<?>>();

		for(Selectable<?> select : selected) {
			this.selected.add(select.copy(editor));
		}
	}
	
	@Override
	public void execute(LevelEditor editor) {
		for(Selectable<?> select : selected) {
			select.move(select.getPosition(Vector2.X), editor);
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		for(Selectable<?> select  : selected) select.remove(editor);
	}
	
	public Array<Selectable<?>> getSelected() {
		return new Array<Selectable<?>>(selected);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pasting:");
		for(Selectable<?> select : selected) {
			builder.append("\n\tPaste " + select + " to " + select.getPosition(Vector2.Zero));
		}
		return builder.toString();
	}
	
}
