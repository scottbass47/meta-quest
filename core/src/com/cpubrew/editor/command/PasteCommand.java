package com.cpubrew.editor.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.Interactable;

public class PasteCommand extends Command{

	private Array<Interactable<?>> selected;
	
	public PasteCommand(Array<Interactable<?>> selected, LevelEditor editor) {
		super(false);
		this.selected = new Array<Interactable<?>>();

		for(Interactable<?> select : selected) {
			this.selected.add(select.copy(editor));
		}
	}
	
	@Override
	public void execute(LevelEditor editor) {
		for(Interactable<?> select : selected) {
			select.move(select.getPosition(Vector2.X), editor);
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		for(Interactable<?> select  : selected) select.remove(editor);
	}
	
	public Array<Interactable<?>> getSelected() {
		return new Array<Interactable<?>>(selected);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pasting:");
		for(Interactable<?> select : selected) {
			builder.append("\n\tPaste " + select + " to " + select.getPosition(Vector2.Zero));
		}
		return builder.toString();
	}
	
}
