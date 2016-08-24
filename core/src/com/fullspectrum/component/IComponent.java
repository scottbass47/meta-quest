package com.fullspectrum.component;

import com.fullspectrum.entity.Entity;

public interface IComponent {

	public void init();
	public void update(float delta, Entity e);
	
}
