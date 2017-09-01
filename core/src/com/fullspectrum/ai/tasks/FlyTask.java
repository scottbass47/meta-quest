package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.MathUtils;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;
import com.fullspectrum.utils.Maths;

public class FlyTask extends LeafTask<Entity>{

	public float angle;
	public boolean useProperty = false;
	
	public FlyTask(float angle) {
		this.angle = angle;
	}
	
	/**
	 * If <code>useProperty</code> is set to true, this task will use the value named <code>fly_angle</code> in the
	 * entity's <code>PropertyComponent</code>
	 * @param useProperty
	 */
	public FlyTask(boolean useProperty) {
		this.useProperty = useProperty;
	}

	@Override
	public Status execute() {
		Entity entity = getObject();
		if(!Mappers.input.get(entity).enabled) return Status.FAILED;

		if(useProperty) {
			angle = Mappers.property.get(entity).getFloat("fly_angle");
		}
		
		float cos = MathUtils.cosDeg(angle);
		float sin = MathUtils.sinDeg(angle);
		
		AIController controller = Mappers.aiController.get(entity).controller;
		
		switch(Maths.getQuad(angle)) {
		case 1:
			controller.press(Actions.MOVE_RIGHT, cos);
			controller.press(Actions.MOVE_UP, sin);
			break;
		case 2:
			controller.press(Actions.MOVE_LEFT, Math.abs(cos));
			controller.press(Actions.MOVE_UP, sin);
			break;
		case 3:
			controller.press(Actions.MOVE_LEFT, Math.abs(cos));
			controller.press(Actions.MOVE_DOWN, Math.abs(sin));
			break;
		case 4:
			controller.press(Actions.MOVE_RIGHT, cos);
			controller.press(Actions.MOVE_DOWN, Math.abs(sin));
			break;
		}
		
		return Status.SUCCEEDED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		FlyTask ftask = (FlyTask) task;
		ftask.angle = angle;
		return ftask;
	}
	
}
