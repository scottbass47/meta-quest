package com.cpubrew.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.TargetComponent;
import com.cpubrew.level.Level;
import com.cpubrew.level.Platform;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.PhysicsUtils;

public class TargetOnPlatformTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		Entity entity = getObject();
		
		TargetComponent targetComp = Mappers.target.get(entity);
		if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
		
		Level level = Mappers.level.get(entity).level;
		
		Vector2 myPos = PhysicsUtils.getPos(entity);
		Rectangle myHitbox = Mappers.body.get(entity).getAABB();
		
		Vector2 targetPos = PhysicsUtils.getPos(targetComp.target);
		Rectangle targetHitbox = Mappers.body.get(targetComp.target).getAABB();
		
		Platform platform = level.getPlatform(myPos.x, myPos.y - myHitbox.height * 0.5f + 0.05f);
		if(platform == null) return Status.FAILED;
		
		if(platform.contains(targetPos.x, targetPos.y - targetHitbox.height * 0.5f + 0.05f)) return Status.SUCCEEDED;
		return Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}

}
