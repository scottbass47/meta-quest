package com.fullspectrum.component;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * Convenience class for accessing components
 * 
 * @author Scott
 *
 */
public class Mappers {

	// Global
	public static final ComponentMapper<EntityComponent> entity = ComponentMapper.getFor(EntityComponent.class);
	public static final ComponentMapper<EngineComponent> engine = ComponentMapper.getFor(EngineComponent.class);
	public static final ComponentMapper<WorldComponent> world = ComponentMapper.getFor(WorldComponent.class);
	public static final ComponentMapper<LevelComponent> level = ComponentMapper.getFor(LevelComponent.class);
	public static final ComponentMapper<DeathComponent> death = ComponentMapper.getFor(DeathComponent.class);
	public static final ComponentMapper<TimerComponent> timer = ComponentMapper.getFor(TimerComponent.class);
	
	// AI
	public static final ComponentMapper<BehaviorComponent> behavior = ComponentMapper.getFor(BehaviorComponent.class);
	public static final ComponentMapper<PathComponent> path = ComponentMapper.getFor(PathComponent.class);
	public static final ComponentMapper<FollowComponent> follow = ComponentMapper.getFor(FollowComponent.class);
	public static final ComponentMapper<AIControllerComponent> aiController = ComponentMapper.getFor(AIControllerComponent.class);
	public static final ComponentMapper<TargetComponent> target = ComponentMapper.getFor(TargetComponent.class);
	public static final ComponentMapper<WanderingComponent> wandering = ComponentMapper.getFor(WanderingComponent.class);
	public static final ComponentMapper<FlowFieldComponent> flowField = ComponentMapper.getFor(FlowFieldComponent.class);
	public static final ComponentMapper<FlowFollowComponent> flowFollow = ComponentMapper.getFor(FlowFollowComponent.class);
	
	// Movement
	public static final ComponentMapper<GroundMovementComponent> groundMovement = ComponentMapper.getFor(GroundMovementComponent.class);
	public static final ComponentMapper<DropMovementComponent> dropMovement = ComponentMapper.getFor(DropMovementComponent.class);
	public static final ComponentMapper<FlyingComponent> flying = ComponentMapper.getFor(FlyingComponent.class);
	public static final ComponentMapper<LadderComponent> ladder = ComponentMapper.getFor(LadderComponent.class);
	public static final ComponentMapper<FrameMovementComponent> frameMovement = ComponentMapper.getFor(FrameMovementComponent.class);
	
	// Positioning / Physics
	public static final ComponentMapper<PositionComponent> position =  ComponentMapper.getFor(PositionComponent.class);
	public static final ComponentMapper<BodyComponent> body =  ComponentMapper.getFor(BodyComponent.class);
	public static final ComponentMapper<VelocityComponent> velocity =  ComponentMapper.getFor(VelocityComponent.class);
	public static final ComponentMapper<SpeedComponent> speed =  ComponentMapper.getFor(SpeedComponent.class);
	public static final ComponentMapper<OffsetComponent> offset = ComponentMapper.getFor(OffsetComponent.class);
	public static final ComponentMapper<ParentComponent> parent = ComponentMapper.getFor(ParentComponent.class);
	public static final ComponentMapper<ChildrenComponent> children = ComponentMapper.getFor(ChildrenComponent.class); 
	public static final ComponentMapper<DirectionComponent> direction =  ComponentMapper.getFor(DirectionComponent.class);
	public static final ComponentMapper<EaseComponent> ease =  ComponentMapper.getFor(EaseComponent.class);

	// Render
	public static final ComponentMapper<RenderComponent> render =  ComponentMapper.getFor(RenderComponent.class);
	public static final ComponentMapper<TextureComponent> texture =  ComponentMapper.getFor(TextureComponent.class);
	public static final ComponentMapper<AnimationComponent> animation =  ComponentMapper.getFor(AnimationComponent.class);
	public static final ComponentMapper<FacingComponent> facing =  ComponentMapper.getFor(FacingComponent.class);
	public static final ComponentMapper<TintComponent> tint = ComponentMapper.getFor(TintComponent.class);
	public static final ComponentMapper<TextRenderComponent> textRender =  ComponentMapper.getFor(TextRenderComponent.class);
	public static final ComponentMapper<ShaderComponent> shader = ComponentMapper.getFor(ShaderComponent.class);
	
	// State Machine
	public static final ComponentMapper<FSMComponent> fsm =  ComponentMapper.getFor(FSMComponent.class);
	public static final ComponentMapper<ESMComponent> esm =  ComponentMapper.getFor(ESMComponent.class);
	public static final ComponentMapper<AISMComponent> aism = ComponentMapper.getFor(AISMComponent.class);
	public static final ComponentMapper<ASMComponent> asm = ComponentMapper.getFor(ASMComponent.class);
	
	// Player
	public static final ComponentMapper<KnightComponent> knight = ComponentMapper.getFor(KnightComponent.class);
	public static final ComponentMapper<RogueComponent> rogue = ComponentMapper.getFor(RogueComponent.class);
	
	// Other
	public static final ComponentMapper<StateComponent> state =  ComponentMapper.getFor(StateComponent.class);
	public static final ComponentMapper<InputComponent> input =  ComponentMapper.getFor(InputComponent.class);
	public static final ComponentMapper<JumpComponent> jump = ComponentMapper.getFor(JumpComponent.class);
	public static final ComponentMapper<CameraComponent> camera = ComponentMapper.getFor(CameraComponent.class);
	public static final ComponentMapper<CollisionComponent> collision = ComponentMapper.getFor(CollisionComponent.class);
	public static final ComponentMapper<SwordComponent> sword = ComponentMapper.getFor(SwordComponent.class);
	public static final ComponentMapper<SwingComponent> swing = ComponentMapper.getFor(SwingComponent.class);
	public static final ComponentMapper<HealthComponent> heatlh = ComponentMapper.getFor(HealthComponent.class);
	public static final ComponentMapper<SwordStatsComponent> swordStats = ComponentMapper.getFor(SwordStatsComponent.class);
	public static final ComponentMapper<BulletStatsComponent> bulletStats = ComponentMapper.getFor(BulletStatsComponent.class);
	public static final ComponentMapper<TypeComponent> type = ComponentMapper.getFor(TypeComponent.class);
	public static final ComponentMapper<AttackComponent> attack = ComponentMapper.getFor(AttackComponent.class);
	public static final ComponentMapper<MoneyComponent> money = ComponentMapper.getFor(MoneyComponent.class);
	public static final ComponentMapper<BlinkComponent> blink = ComponentMapper.getFor(BlinkComponent.class);
	public static final ComponentMapper<ForceComponent> force = ComponentMapper.getFor(ForceComponent.class);
	public static final ComponentMapper<DropComponent> drop = ComponentMapper.getFor(DropComponent.class);
	public static final ComponentMapper<BarrierComponent> barrier = ComponentMapper.getFor(BarrierComponent.class);
	public static final ComponentMapper<WallComponent> wall = ComponentMapper.getFor(WallComponent.class);
	public static final ComponentMapper<CombustibleComponent> combustible = ComponentMapper.getFor(CombustibleComponent.class);
	public static final ComponentMapper<KnockBackComponent> knockBack = ComponentMapper.getFor(KnockBackComponent.class);
	public static final ComponentMapper<ProjectileComponent> projectile = ComponentMapper.getFor(ProjectileComponent.class);
	public static final ComponentMapper<AbilityComponent> ability = ComponentMapper.getFor(AbilityComponent.class);
	public static final ComponentMapper<InvincibilityComponent> inviciblity = ComponentMapper.getFor(InvincibilityComponent.class);
	public static final ComponentMapper<BobComponent> bob = ComponentMapper.getFor(BobComponent.class);
	public static final ComponentMapper<WingComponent> wing = ComponentMapper.getFor(WingComponent.class);
	public static final ComponentMapper<DamageComponent> damage = ComponentMapper.getFor(DamageComponent.class);
	public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
	public static final ComponentMapper<SpawnComponent> spawn = ComponentMapper.getFor(SpawnComponent.class);
	public static final ComponentMapper<SpawnerPoolComponent> spawnerPool = ComponentMapper.getFor(SpawnerPoolComponent.class);
	public static final ComponentMapper<LevelSwitchComponent> levelSwitch = ComponentMapper.getFor(LevelSwitchComponent.class);
	public static final ComponentMapper<ImmuneComponent> immune = ComponentMapper.getFor(ImmuneComponent.class);
	public static final ComponentMapper<EffectComponent> effect = ComponentMapper.getFor(EffectComponent.class);

}

