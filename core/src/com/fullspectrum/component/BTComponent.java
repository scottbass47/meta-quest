package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.utils.Pool.Poolable;

public class BTComponent implements Component, Poolable{

	public BehaviorTree<Entity> tree;

	public BTComponent set(BehaviorTree<Entity> tree) {
		this.tree = tree;
		return this;
	}

	@Override
	public void reset() {
		tree = null;
	}
	
}
