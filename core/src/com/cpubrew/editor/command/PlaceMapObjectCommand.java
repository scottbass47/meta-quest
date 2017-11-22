package com.cpubrew.editor.command;

import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.mapobject.MapObject;

public class PlaceMapObjectCommand extends Command {

	private MapObject mobj;
	private int id;
	
	public PlaceMapObjectCommand(MapObject mobj) {
		super(false);
		this.mobj = mobj;
		this.id = mobj.getId();
	}
	
	@Override
	public void execute(LevelEditor editor) {
		editor.addMapObject(mobj);
	}

	@Override
	public void undo(LevelEditor editor) {
		editor.removeMapObject(id);
	}
	
	@Override
	public String toString() {
		return "Place " + mobj;
	}

}
