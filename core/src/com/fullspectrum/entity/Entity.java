package com.fullspectrum.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.fullspectrum.component.GraphicsComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.PhysicsComponent;

public abstract class Entity implements Disposable {

	// Components
	private InputComponent input;
	private PhysicsComponent physics;
	private GraphicsComponent graphics;
	
	// Entity State Manager
	private EntityStateManager manager;
	
	public Entity(InputComponent input, PhysicsComponent physics, GraphicsComponent graphics, EntityStateManager manager) {
		this.input = input;
		this.physics = physics;
		this.graphics = graphics;
		this.manager = manager;
		manager.setEntity(this);
	}

	public void update(float delta) {
		if (input != null) {
			manager.handleInput(input.getInput());
			input.update(delta, this);
		}
		if (physics != null){ 
			physics.update(delta, this);
			manager.handlePhysics(physics);
		}
		if (graphics != null) graphics.update(delta, this);
	}

	public void render(SpriteBatch batch) {
		if(graphics != null) graphics.render(batch);
	}
	
	public EntityStateManager getEntityStateManager(){
		return manager;
	}

	@Override
	public void dispose() {
	}

}
