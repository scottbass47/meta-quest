package com.cpubrew.editor.command;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.Interactable;

public class DeleteCommand extends Command {

	private Array<Interactable<?>> selected;
	
	public DeleteCommand(Array<Interactable<?>> selected) {
		super(false);
		this.selected = new Array<Interactable<?>>(selected);
	}

	@Override
	public void execute(LevelEditor editor) {
		for(Interactable<?> select : selected) {
			select.remove(editor);
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		for(Interactable<?> select : selected) {
			select.add(select.getPosition(Vector2.Zero), editor);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Deleting:");
		for(Interactable<?> select : selected) {
			builder.append("\n\tDelete " + select);
		}
		return builder.toString();
	}
	
}
