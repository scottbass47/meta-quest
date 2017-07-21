package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class InRangeTask extends LeafTask<Entity> {

	private float range;
	
	public InRangeTask(float range) {
		this.range = range;
	}
	
	@Override
	public Status execute() {
		Entity entity = getObject();
		TargetComponent targetComp = Mappers.target.get(entity);
		if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
		
		Vector2 myPos = PhysicsUtils.getPos(entity);
		Vector2 targetPos = PhysicsUtils.getPos(targetComp.target);
		
		float dx = myPos.x - targetPos.x;
		float dy = myPos.y - targetPos.y;
		
		if(dx * dx + dy * dy <= range * range) return Status.SUCCEEDED;
		return Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		InRangeTask rangeTask = (InRangeTask) task;
		rangeTask.setRange(range);
		return task;
	}
	
	public void setRange(float range) {
		this.range = range;
	}
	
	public float getRange() {
		return range;
	}
}
