package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.level.Level;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class InLoSTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		Entity entity = getObject();
		TargetComponent targetComp = Mappers.target.get(entity);
		if(targetComp == null || !EntityUtils.isValid(targetComp.target)) return Status.FAILED;
		
		Vector2 myPos = PhysicsUtils.getPos(entity);
		Vector2 targetPos = PhysicsUtils.getPos(targetComp.target);
		
		Level level = Mappers.level.get(entity).level;
		boolean los = level.performRayTrace(myPos.x, myPos.y, targetPos.x, targetPos.y);
		
		if(los) return Status.SUCCEEDED;
		return Status.FAILED;
	}
	
	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
