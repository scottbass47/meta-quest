package com.fullspectrum.editor.command;

import com.fullspectrum.editor.LevelEditor;

public class ResizeMapCommand extends Command {

	private Direction dir;
	private boolean expand;
	
	public ResizeMapCommand(Direction dir, boolean expand) {
		super(false);
		this.dir = dir;
		this.expand = expand;
	}
	
	@Override
	public void execute(LevelEditor editor) {
		if(expand){
			switch (dir) {
			case DOWN:
				editor.addRow(false);
				break;
			case LEFT:
				editor.addCol(false);
				break;
			case RIGHT:
				editor.addCol(true);
				break;
			case UP:
				editor.addRow(true);
				break;
			default:
				break;
			}
		} else {
			switch (dir) {
			case DOWN:
				editor.removeRow(true, true);
				break;
			case LEFT:
				editor.removeCol(true, true);
				break;
			case RIGHT:
				editor.removeCol(false, true);
				break;
			case UP:
				editor.removeRow(false, true);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		if(expand){
			switch (dir) {
			case DOWN:
				editor.removeRow(false, true);
				break;
			case LEFT:
				editor.removeCol(false, true);
				break;
			case RIGHT:
				editor.removeCol(true, true);
				break;
			case UP:
				editor.removeRow(true, true);
				break;
			default:
				break;
			}
		} else {
			switch (dir) {
			case DOWN:
				editor.addRow(true);
				break;
			case LEFT:
				editor.addCol(true);
				break;
			case RIGHT:
				editor.addCol(false);
				break;
			case UP:
				editor.addRow(false);
				break;
			default:
				break;
			}
		}
	}
	
	@Override
	public String toString() {
		return (expand ? "Expand map " : "Shrink map ") + dir.name();
	}

	public enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT
	}
	
}
