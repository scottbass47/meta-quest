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
	public static final ComponentMapper<AnimationComponent> animation =  ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<FSMComponent> fsm =  ComponentMapper.getFor(FSMComponent.class);
	public static final ComponentMapper<StateComponent> state =  ComponentMapper.getFor(StateComponent.class);
	public static final ComponentMapper<InputComponent> input =  ComponentMapper.getFor(InputComponent.class);
	
}
