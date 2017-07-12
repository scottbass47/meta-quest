package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.level.Level;
import com.fullspectrum.utils.Maths;
import com.fullspectrum.utils.PhysicsUtils;

public class ReachedPlatformEndTask extends LeafTask<Entity>{

	@Override
	public Status execute() {
		Entity entity = getObject();
		Level level = Mappers.level.get(entity).level;
		
		Vector2 pos = PhysicsUtils.getPos(entity);
		Rectangle hitbox = Mappers.body.get(entity).getAABB();
		
		CollisionComponent collisionComp = Mappers.collision.get(entity);
		if(!collisionComp.onGround()) return Status.FAILED;
		
		float x = pos.x;
		float y = pos.y - hitbox.height * 0.5f + 0.05f;
		
		boolean facingRight = Mappers.facing.get(entity).facingRight;
		
		x += facingRight ? 1.0f : -1.0f;
		
		// Row of the tile the player is standing on
		int row = Maths.toGridCoord(y - 1.0f);
		int col = Maths.toGridCoord(x);
		
		// Check diagonals
		if(level.isSolid(row + 1, col) || !level.isSolid(row, col)) return Status.SUCCEEDED;
		
		return Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		return task;
	}


}
