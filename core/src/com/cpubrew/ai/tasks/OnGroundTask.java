package com.cpubrew.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.cpubrew.component.Mappers;

public class OnGroundTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		return Mappers.collision.get(getObject()).onGround() ? Status.SUCCEEDED : Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
