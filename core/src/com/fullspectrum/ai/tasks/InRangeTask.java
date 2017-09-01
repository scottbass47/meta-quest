package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.debug.DebugInput;
import com.fullspectrum.debug.DebugRender;
import com.fullspectrum.debug.DebugToggle;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class InRangeTask extends LeafTask<Entity> {

	private float range;
	private RangeTest rangeTest;
	
	public InRangeTask(float range) {
		this.range = range;
	}
	
	public InRangeTask(RangeTest rangeTest) {
		this.rangeTest = rangeTest;
	}
	
	@Override
	public Status execute() {
		Entity entity = getObject();
		TargetComponent targetComp = Mappers.target.get(entity);
		if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
		
		if(rangeTest != null) {
			return rangeTest.inRange(entity, targetComp.target) ? Status.SUCCEEDED : Status.FAILED;
		}
		
		Vector2 myPos = PhysicsUtils.getPos(entity);
		Vector2 targetPos = PhysicsUtils.getPos(targetComp.target);
		
		float dx = myPos.x - targetPos.x;
		float dy = myPos.y - targetPos.y;
		
		if(DebugInput.isToggled(DebugToggle.SHOW_RANGE)) {
			DebugRender.setColor(Color.MAGENTA);
			DebugRender.setType(ShapeType.Line);
			DebugRender.circle(myPos.x, myPos.y, range);
		}
		
		if(dx * dx + dy * dy <= range * range) return Status.SUCCEEDED;
		return Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		InRangeTask rangeTask = (InRangeTask) task;
		rangeTask.setRange(range);
		rangeTask.rangeTest = rangeTest;
		return task;
	}
	
	public void setRange(float range) {
		this.range = range;
	}
	
	public float getRange() {
		return range;
	}
	
	public static interface RangeTest {
		
		public boolean inRange(Entity me, Entity other);
		
	}
}
