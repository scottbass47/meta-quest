package com.cpubrew.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.cpubrew.ai.AIController;
import com.cpubrew.component.Mappers;
import com.cpubrew.input.Actions;

public class WalkForwardTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		Entity entity = getObject();
		
		// If the input isn't enabled, the task failed
		if(!Mappers.input.get(entity).enabled) return Status.FAILED;
		
		AIController controller = (AIController)Mappers.input.get(entity).input;
		boolean facingRight = Mappers.facing.get(entity).facingRight;
		
		if(facingRight) {
			controller.press(Actions.MOVE_RIGHT);
		} else {
			controller.press(Actions.MOVE_LEFT);
		}
		
		return Status.SUCCEEDED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
