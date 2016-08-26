package com.fullspectrum.component;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * Convenience class for accessing components
 * 
 * @author Scott
 *
 */
public class Mappers {

	public static final ComponentMapper<PositionComponent> position =  ComponentMapper.getFor(PositionComponent.class);
	public static final ComponentMapper<RenderComponent> render =  ComponentMapper.getFor(RenderComponent.class);
	public static final ComponentMapper<TextureComponent> texture =  ComponentMapper.getFor(TextureComponent.class);
	public static final ComponentMapper<BodyComponent> body =  ComponentMapper.getFor(BodyComponent.class);
	
}
