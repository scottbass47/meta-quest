package com.cpubrew.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TargetComponent;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.PhysicsUtils;

public class TargetBehindMeTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		Entity entity = getObject();
		Vector2 myPos = PhysicsUtils.getPos(entity);
		boolean facingRight = Mappers.facing.get(entity).facingRight;
		
		TargetComponent targetComp = Mappers.target.get(entity);
		if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
		
		Vector2 targetPos = PhysicsUtils.getPos(targetComp.target);
		
		if(myPos.x >= targetPos.x == facingRight) return Status.SUCCEEDED;
		return Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
