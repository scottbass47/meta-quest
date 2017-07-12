package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.component.Mappers;

public class ReleaseControlsTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		Entity entity = getObject();
		if(!Mappers.input.get(entity).enabled) return Status.FAILED;
		((AIController)Mappers.input.get(entity).input).releaseAll();
		return Status.SUCCEEDED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
