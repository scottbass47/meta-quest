package com.cpubrew.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.cpubrew.ai.AIController;

public class AIControllerComponent implements Component, Poolable {

	public AIController controller;

	@Override
	public void reset() {
		controller = null;
	}

	public AIControllerComponent set(AIController controller) {
		this.controller = controller;
		return this;
	}

}
