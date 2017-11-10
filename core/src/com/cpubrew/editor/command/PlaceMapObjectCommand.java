package com.cpubrew.editor.command;

import com.cpubrew.editor.LevelEditor;
import com.cpubrew.editor.mapobject.MapObject;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.level.Level.EntitySpawn;

public class PlaceMapObjectCommand extends Command {

//	private int spawnID;
//	private EntitySpawn spawn; // Used for placing, DO NOT reference when undoing. Use spawnID to ensure you get a good spawn.
//	private EntitySpawn oldPlayer;
	
	private MapObject mobj;
	private int id;
	
	public PlaceMapObjectCommand(MapObject mobj) {
		super(false);
		this.mobj = mobj;
	}
	
	@Override
	public void execute(LevelEditor editor) {
//		EntityIndex index = spawn.getIndex();
//		if(index == EntityIndex.KNIGHT || index == EntityIndex.MONK || index == EntityIndex.ROGUE) {
//			oldPlayer = editor.getPlayerSpawn();
//			editor.setPlayerSpawn(spawn);
//		} else {
//			spawnID = editor.addSpawn(spawn);
//		}
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
