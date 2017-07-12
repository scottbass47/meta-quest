package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fullspectrum.component.Mappers;

/**
 * Flips whether or not the AI is facing right. Use this with <code>ReleaseControlsTask</code>.
 * @author Scott
 */
public class TurnAroundTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		Entity entity = getObject();
		if(Mappers.facing.get(entity).locked) return Status.FAILED;
		Mappers.facing.get(entity).facingRight = !Mappers.facing.get(entity).facingRight;
		return Status.SUCCEEDED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
