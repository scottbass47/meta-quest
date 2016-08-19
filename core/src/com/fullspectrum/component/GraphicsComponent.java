package com.fullspectrum.component;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.fullspectrum.entity.Entity;

public abstract class GraphicsComponent implements IComponent {

	protected PhysicsComponent physics;
	
	public GraphicsComponent(PhysicsComponent physics){
		this.physics = physics;
	}
	
	public abstract void update(float delta, Entity entity);
	public abstract void render(SpriteBatch batch);
	
}
