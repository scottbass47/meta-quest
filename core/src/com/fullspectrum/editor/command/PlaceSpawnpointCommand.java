package com.fullspectrum.editor.command;

import com.fullspectrum.editor.LevelEditor;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.Level.EntitySpawn;

public class PlaceSpawnpointCommand extends Command {

	private EntitySpawn spawn;
	private EntitySpawn oldPlayer;
	
	public PlaceSpawnpointCommand(EntitySpawn spawn) {
		super(false);
		this.spawn = spawn;
	}
	
	@Override
	public void execute(LevelEditor editor) {
		Level level = editor.getCurrentLevel();
		EntityIndex index = spawn.getIndex();
		if(index == EntityIndex.KNIGHT || index == EntityIndex.MONK || index == EntityIndex.ROGUE) {
			oldPlayer = level.getPlayerSpawn();
			level.setPlayerSpawn(spawn);
		} else {
			editor.getCurrentLevel().addEntitySpawn(spawn);
		}
	}

	@Override
	public void undo(LevelEditor editor) {
		Level level = editor.getCurrentLevel();
		EntityIndex index = spawn.getIndex();
		if(index == EntityIndex.KNIGHT || index == EntityIndex.MONK || index == EntityIndex.ROGUE) {
			level.setPlayerSpawn(oldPlayer);
		} else {
			editor.getCurrentLevel().removeSpawn(spawn);
		}
	}
	
	@Override
	public String toString() {
		return "Place " + spawn;
	}

}
