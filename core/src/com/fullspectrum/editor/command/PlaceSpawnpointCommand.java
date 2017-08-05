package com.fullspectrum.editor.command;

import com.fullspectrum.editor.LevelEditor;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.level.Level.EntitySpawn;

public class PlaceSpawnpointCommand extends Command {

	private int spawnID;
	private EntitySpawn spawn; // Used for placing, DO NOT reference when undoing. Use spawnID to ensure you get a good spawn.
	private EntitySpawn oldPlayer;
	
	public PlaceSpawnpointCommand(EntitySpawn spawn) {
		super(false);
		this.spawn = spawn;
	}
	
	@Override
	public void execute(LevelEditor editor) {
		EntityIndex index = spawn.getIndex();
		if(index == EntityIndex.KNIGHT || index == EntityIndex.MONK || index == EntityIndex.ROGUE) {
			oldPlayer = editor.getPlayerSpawn();
			editor.setPlayerSpawn(spawn);
		} else {
			spawnID = editor.addSpawn(spawn);
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		EntitySpawn spawn = editor.getSpawn(spawnID);
		EntityIndex index = spawn.getIndex();
		if(index == EntityIndex.KNIGHT || index == EntityIndex.MONK || index == EntityIndex.ROGUE) {
			editor.setPlayerSpawn(oldPlayer);
		} else {
			editor.removeSpawn(spawnID);
		}
	}
	
	@Override
	public String toString() {
		return "Place " + spawn + " [" + spawnID + "]";
	}

}
