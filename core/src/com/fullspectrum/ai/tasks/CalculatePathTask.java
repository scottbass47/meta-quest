package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.utils.EntityUtils;

public class CalculatePathTask extends LeafTask<Entity> {

	private boolean shadowPathing;
	
	public CalculatePathTask() {
		shadowPathing = false;
	}
	
	public CalculatePathTask(boolean shadowPathing) {
		this.shadowPathing = shadowPathing;
	}
	
	@Override
	public Status execute() {
		Entity entity = getObject();
		TargetComponent targetComp = Mappers.target.get(entity);
		if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
		
		PathComponent pathComp = Mappers.path.get(entity);
		if(pathComp == null || pathComp.pathFinder == null) return Status.FAILED;

		PathFinder pathFinder = pathComp.pathFinder;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return null;
	}
	
	

}
