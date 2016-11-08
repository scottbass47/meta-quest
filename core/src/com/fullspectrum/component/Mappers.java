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
	public static final ComponentMapper<DirectionComponent> direction =  ComponentMapper.getFor(DirectionComponent.class);
	public static final ComponentMapper<FacingComponent> facing =  ComponentMapper.getFor(FacingComponent.class);
	public static final ComponentMapper<SpeedComponent> speed =  ComponentMapper.getFor(SpeedComponent.class);
	public static final ComponentMapper<VelocityComponent> velocity =  ComponentMapper.getFor(VelocityComponent.class);
	public static final ComponentMapper<GroundMovementComponent> groundMovement = ComponentMapper.getFor(GroundMovementComponent.class);
	public static final ComponentMapper<DropMovementComponent> dropMovement = ComponentMapper.getFor(DropMovementComponent.class);
	public static final ComponentMapper<JumpComponent> jump = ComponentMapper.getFor(JumpComponent.class);
	public static final ComponentMapper<CameraComponent> camera = ComponentMapper.getFor(CameraComponent.class);
	public static final ComponentMapper<WorldComponent> world = ComponentMapper.getFor(WorldComponent.class);
	public static final ComponentMapper<PathComponent> path = ComponentMapper.getFor(PathComponent.class);
	public static final ComponentMapper<FollowComponent> follow = ComponentMapper.getFor(FollowComponent.class);
	public static final ComponentMapper<AIControllerComponent> aiController = ComponentMapper.getFor(AIControllerComponent.class);
	public static final ComponentMapper<AIStateMachineComponent> aism = ComponentMapper.getFor(AIStateMachineComponent.class);
	public static final ComponentMapper<WanderingComponent> wandering = ComponentMapper.getFor(WanderingComponent.class);
	public static final ComponentMapper<TargetComponent> target = ComponentMapper.getFor(TargetComponent.class);
	public static final ComponentMapper<LevelComponent> level = ComponentMapper.getFor(LevelComponent.class);
	public static final ComponentMapper<CollisionComponent> collision = ComponentMapper.getFor(CollisionComponent.class);
	public static final ComponentMapper<EngineComponent> engine = ComponentMapper.getFor(EngineComponent.class);
	public static final ComponentMapper<OffsetComponent> offset = ComponentMapper.getFor(OffsetComponent.class);
	public static final ComponentMapper<ParentComponent> parent = ComponentMapper.getFor(ParentComponent.class);
	public static final ComponentMapper<SwordComponent> sword = ComponentMapper.getFor(SwordComponent.class);
	public static final ComponentMapper<SwingComponent> swing = ComponentMapper.getFor(SwingComponent.class);
	public static final ComponentMapper<HealthComponent> heatlh = ComponentMapper.getFor(HealthComponent.class);
	public static final ComponentMapper<SwordStatsComponent> swordStats = ComponentMapper.getFor(SwordStatsComponent.class);
	public static final ComponentMapper<TypeComponent> type = ComponentMapper.getFor(TypeComponent.class);
	public static final ComponentMapper<AttackComponent> attack = ComponentMapper.getFor(AttackComponent.class);
	public static final ComponentMapper<MoneyComponent> money = ComponentMapper.getFor(MoneyComponent.class);
	public static final ComponentMapper<BlinkComponent> blink = ComponentMapper.getFor(BlinkComponent.class);
	public static final ComponentMapper<ForceComponent> force = ComponentMapper.getFor(ForceComponent.class);
	public static final ComponentMapper<DropSpawnComponent> dropSpawn = ComponentMapper.getFor(DropSpawnComponent.class);
}
