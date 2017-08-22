package com.fullspectrum.ai.tasks;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.level.Level;

public class OnTileTask extends LeafTask<Entity>{

	private float percentOn;

	public OnTileTask(float percentOn) {
		this.percentOn = percentOn;
	}
	
	/** Defaults <cod>percentOn</code> to be 50% (0.5) */
	public OnTileTask() {
		this(0.5f);
	}
	
	@Override
	public Status execute() {
		Entity entity = getObject();
		boolean facingRight = Mappers.facing.get(entity).facingRight;
		
		
		BodyComponent bodyComp = Mappers.body.get(entity);
		Rectangle hitbox = bodyComp.getAABB();
		Body body = bodyComp.body;
		
		// Front corner of hitbox
		float x = body.getPosition().x + (facingRight ? hitbox.width * 0.5f : -hitbox.width * 0.5f);
		float xOff = percentOn * hitbox.width;
		x += (facingRight ? -xOff : xOff);
		
		float y = body.getPosition().y - hitbox.height * 0.5f - 0.4f; // one tile below where you're standing
		
		Level level = Mappers.level.get(entity).level;
		return level.isSolid(x, y) ? Status.SUCCEEDED : Status.FAILED;
	}

	@Override
	protected Task<Entity> copyTo(Task<Entity> task) {
		OnTileTask otask = (OnTileTask) task;
		otask.percentOn = percentOn;
		return otask;
	}
	
}
