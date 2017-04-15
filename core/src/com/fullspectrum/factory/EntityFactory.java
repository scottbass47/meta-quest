package com.fullspectrum.factory;

import static com.fullspectrum.physics.collision.CollisionBodyType.*;

import java.util.Comparator;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Sort;
import com.fullspectrum.ability.ManaBombAbility;
import com.fullspectrum.ability.knight.AntiMagneticAbility;
import com.fullspectrum.ability.knight.BlacksmithAbility;
import com.fullspectrum.ability.knight.DashSlashAbility;
import com.fullspectrum.ability.knight.KickAbility;
import com.fullspectrum.ability.knight.OverheadSwingAbility;
import com.fullspectrum.ability.knight.ParryAbility;
import com.fullspectrum.ability.knight.SlamAbility;
import com.fullspectrum.ability.knight.SpinSliceAbility;
import com.fullspectrum.ability.knight.TornadoAbility;
import com.fullspectrum.ability.rogue.BoomerangAbility;
import com.fullspectrum.ability.rogue.DashAbility;
import com.fullspectrum.ability.rogue.ExecuteAbility;
import com.fullspectrum.ability.rogue.HomingKnivesAbility;
import com.fullspectrum.ability.rogue.SlingshotAbility;
import com.fullspectrum.ability.rogue.VanishAbility;
import com.fullspectrum.ai.AIBehavior;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.assets.Asset;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.ASMComponent;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.AttackComponent;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BehaviorComponent;
import com.fullspectrum.component.BlinkComponent;
import com.fullspectrum.component.BobComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.ChildrenComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.CollisionListenerComponent;
import com.fullspectrum.component.CombustibleComponent;
import com.fullspectrum.component.ControlledMovementComponent;
import com.fullspectrum.component.DamageComponent;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.DeathComponent.DeathBehavior;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.DirectionComponent.Direction;
import com.fullspectrum.component.DropComponent;
import com.fullspectrum.component.DropMovementComponent;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.EffectComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.EntityComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.FlowFieldComponent;
import com.fullspectrum.component.FlowFollowComponent;
import com.fullspectrum.component.FlyingComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.FrameMovementComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.ImmuneComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.InvincibilityComponent;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.KnightComponent;
import com.fullspectrum.component.KnightComponent.KnightAttack;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.OffsetComponent;
import com.fullspectrum.component.ParentComponent;
import com.fullspectrum.component.PathComponent;
import com.fullspectrum.component.PlayerComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.ProjectileComponent;
import com.fullspectrum.component.RemoveComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.RenderLevelComponent;
import com.fullspectrum.component.RogueComponent;
import com.fullspectrum.component.RotationComponent;
import com.fullspectrum.component.ShaderComponent;
import com.fullspectrum.component.SpawnComponent;
import com.fullspectrum.component.SpawnerPoolComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.StateComponent;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.component.TextRenderComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TimeListener;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TintComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.component.WanderingComponent;
import com.fullspectrum.component.WingComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.effects.EffectType;
import com.fullspectrum.entity.CoinType;
import com.fullspectrum.entity.DropType;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.entity.EntityLoader;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.entity.EntityStats;
import com.fullspectrum.factory.ProjectileFactory.ProjectileData;
import com.fullspectrum.fsm.AIState;
import com.fullspectrum.fsm.AIStateMachine;
import com.fullspectrum.fsm.AnimationStateMachine;
import com.fullspectrum.fsm.EntityState;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.GlobalChangeListener;
import com.fullspectrum.fsm.MultiTransition;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateChangeListener;
import com.fullspectrum.fsm.StateChangeResolver;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateObject;
import com.fullspectrum.fsm.StateObjectCreator;
import com.fullspectrum.fsm.transition.CollisionTransitionData;
import com.fullspectrum.fsm.transition.CollisionTransitionData.CollisionType;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.fsm.transition.LOSTransitionData;
import com.fullspectrum.fsm.transition.RandomTransitionData;
import com.fullspectrum.fsm.transition.RangeTransitionData;
import com.fullspectrum.fsm.transition.TimeTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionObject;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.fsm.transition.Transitions;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;
import com.fullspectrum.level.EntityGrabber;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.LevelHelper;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.movement.BoomerangCurveMovement;
import com.fullspectrum.movement.BoomerangLineMovement;
import com.fullspectrum.movement.Movement;
import com.fullspectrum.physics.BodyProperties;
import com.fullspectrum.physics.FixtureType;
import com.fullspectrum.physics.collision.CollisionBodyType;
import com.fullspectrum.physics.collision.CollisionData;
import com.fullspectrum.physics.collision.FixtureInfo;
import com.fullspectrum.physics.collision.behavior.BoomerangBehavior;
import com.fullspectrum.physics.collision.behavior.DamageOnCollideBehavior;
import com.fullspectrum.physics.collision.behavior.DeathOnCollideBehavior;
import com.fullspectrum.physics.collision.behavior.SensorBehavior;
import com.fullspectrum.physics.collision.behavior.SpawnExplosionBehavior;
import com.fullspectrum.physics.collision.filter.CollisionFilter;
import com.fullspectrum.physics.collision.filter.PlayerFilter;
import com.fullspectrum.render.RenderLevel;
import com.fullspectrum.shader.Shader;
import com.fullspectrum.utils.EntityUtils;
import com.fullspectrum.utils.PhysicsUtils;

public class EntityFactory {

	private static AssetLoader assets = AssetLoader.getInstance();
	public static Engine engine;
	public static World world;
	public static Level level;
	
	private EntityFactory(){}
	
	// ---------------------------------------------
	// -                   PLAYER                  -
	// ---------------------------------------------
	public static Entity createPlayer(Input input, float x, float y) {
//		// Stats
//		EntityStats playerStats = EntityLoader.get(EntityIndex.PLAYER);
//		EntityStats knightStats = EntityLoader.get(EntityIndex.KNIGHT);
//		EntityStats rogueStats = EntityLoader.get(EntityIndex.ROGUE);
//		EntityStats mageStats = EntityLoader.get(EntityIndex.MAGE);
//		
//		// Setup Animations
//		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
//		animMap.put(EntityAnim.IDLE, assets.getAnimation(Assets.KNIGHT_IDLE));
//		animMap.put(EntityAnim.RUNNING, assets.getAnimation(Assets.KNIGHT_RUN));
//		animMap.put(EntityAnim.JUMP, assets.getAnimation(Assets.KNIGHT_JUMP));
//		animMap.put(EntityAnim.FALLING, assets.getAnimation(Assets.KNIGHT_FALL));
//		animMap.put(EntityAnim.RANDOM_IDLE, assets.getAnimation(Assets.KNIGHT_IDLE));
//		animMap.put(EntityAnim.RISE, assets.getAnimation(Assets.KNIGHT_RISE));
//		animMap.put(EntityAnim.LAND, assets.getAnimation(Assets.KNIGHT_LAND));
//		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Assets.KNIGHT_APEX));
//		animMap.put(EntityAnim.CLIMBING, assets.getAnimation(Assets.KNIGHT_IDLE));
//		animMap.put(EntityAnim.SWING, assets.getAnimation(Assets.SHADOW_PUNCH));
//		animMap.put(EntityAnim.WALL_SLIDING, assets.getAnimation(Assets.SHADOW_IDLE));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_1, assets.getAnimation(Assets.KNIGHT_CHAIN1_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_2, assets.getAnimation(Assets.KNIGHT_CHAIN2_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_3, assets.getAnimation(Assets.KNIGHT_CHAIN3_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_4, assets.getAnimation(Assets.KNIGHT_CHAIN4_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_1, assets.getAnimation(Assets.KNIGHT_CHAIN1_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_2, assets.getAnimation(Assets.KNIGHT_CHAIN2_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_3, assets.getAnimation(Assets.KNIGHT_CHAIN3_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_4, assets.getAnimation(Assets.KNIGHT_CHAIN4_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_1, assets.getAnimation(Assets.KNIGHT_CHAIN1_SWING));
//		animMap.put(EntityAnim.SWING_2, assets.getAnimation(Assets.KNIGHT_CHAIN2_SWING));
//		animMap.put(EntityAnim.SWING_3, assets.getAnimation(Assets.KNIGHT_CHAIN3_SWING));
//		animMap.put(EntityAnim.SWING_4, assets.getAnimation(Assets.KNIGHT_CHAIN4_SWING));
//
//		Entity player = new EntityBuilder("player")
//				.animation(animMap)
//				.physics("player.json", x, y, true)
//				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0), true)
//				.build();
//		player.add(engine.createComponent(MoneyComponent.class));
//		player.add(engine.createComponent(PlayerComponent.class));
//		player.add(engine.createComponent(InputComponent.class).set(input));
//		player.add(engine.createComponent(TypeComponent.class).set(EntityType.FRIENDLY).setCollideWith(EntityType.FRIENDLY.getOpposite()));
//		player.add(engine.createComponent(BarrierComponent.class).set(playerStats.get("shield"), playerStats.get("shield"), playerStats.get("shield_rate"), playerStats.get("shield_delay")));
//		player.getComponent(DeathComponent.class).set(new DeathBehavior(){
//			@Override
//			public void onDeath(Entity entity) {
//				Mappers.heatlh.get(entity).health = Mappers.heatlh.get(entity).maxHealth;
//				Mappers.death.get(entity).makeAlive();
//			}
//		});
//
//		EntityStateMachine knightESM = createKnight(engine, world, level, x, y, player, knightStats);
//		EntityStateMachine rogueESM = createRogue(engine, world, level, x, y, player, rogueStats);
//		EntityStateMachine mageESM = createMage(engine, world, level, x, y, player, mageStats);
//		StateMachine<EntityStates, StateObject> rogueAttackFSM = createRogueAttackMachine(player, rogueStats);
//
//		StateMachine<PlayerState, StateObject> playerStateMachine = new StateMachine<PlayerState, StateObject>(player, new StateObjectCreator(), PlayerState.class, StateObject.class);
//		playerStateMachine.setDebugName("Player SM");
//		
//		playerStateMachine.createState(PlayerState.KNIGHT)
//			.add(engine.createComponent(HealthComponent.class).set(knightStats.get("health"), knightStats.get("health")))
//			.add(engine.createComponent(ESMComponent.class).set(knightESM))
////			.add(engine.createComponent(TintComponent.class).set(new Color(123 / 255f, 123 / 255f, 184 / 255f, 1.0f)))
//			.add(engine.createComponent(AbilityComponent.class))
//			.addSubstateMachine(knightESM);
//		
//		playerStateMachine.createState(PlayerState.ROGUE)
//			.add(engine.createComponent(HealthComponent.class).set(rogueStats.get("health"), rogueStats.get("health")))
//			.add(engine.createComponent(ESMComponent.class).set(rogueESM))
////			.add(engine.createComponent(FSMComponent.class).set(rogueAttackFSM))
//			.add(engine.createComponent(TintComponent.class).set(new Color(176 / 255f, 47 / 255f, 42 / 255f, 1.0f)))
//			.add(engine.createComponent(AbilityComponent.class))
//			.addSubstateMachine(rogueESM)
//			.addSubstateMachine(rogueAttackFSM);
//		
//		playerStateMachine.createState(PlayerState.MAGE)
//			.add(engine.createComponent(HealthComponent.class).set(mageStats.get("health"), mageStats.get("health")))
//			.add(engine.createComponent(ESMComponent.class).set(mageESM))
//			.add(engine.createComponent(TintComponent.class).set(new Color(165 / 255f, 65 / 255f, 130 / 255f, 1.0f)))
//			.add(engine.createComponent(AbilityComponent.class)
//					.add(AbilityType.MANA_BOMB, assets.getAnimation(Assets.blueCoin).getKeyFrame(0.0f), mageStats.get("mana_bomb_cooldown")))
//			.addSubstateMachine(mageESM);
//		
//		InputTransitionData rightCycleData = new InputTransitionData(Type.ALL, true);
//		rightCycleData.triggers.add(new InputTrigger(Actions.CYCLE_RIGHT, true));
//		
//		InputTransitionData leftCycleData = new InputTransitionData(Type.ALL, true);
//		leftCycleData.triggers.add(new InputTrigger(Actions.CYCLE_LEFT, true));
//		
//		// Knight, Rogue, Mage
//		playerStateMachine.addTransition(PlayerState.KNIGHT, Transitions.INPUT, rightCycleData, PlayerState.ROGUE);
//		playerStateMachine.addTransition(PlayerState.KNIGHT, Transitions.INPUT, leftCycleData, PlayerState.MAGE);
//		playerStateMachine.addTransition(PlayerState.ROGUE, Transitions.INPUT, rightCycleData, PlayerState.MAGE);
//		playerStateMachine.addTransition(PlayerState.ROGUE, Transitions.INPUT, leftCycleData, PlayerState.KNIGHT);
//		playerStateMachine.addTransition(PlayerState.MAGE, Transitions.INPUT, rightCycleData, PlayerState.KNIGHT);
//		playerStateMachine.addTransition(PlayerState.MAGE, Transitions.INPUT, leftCycleData, PlayerState.ROGUE);
//		
//		playerStateMachine.setDebugName("Player FSM");
////		System.out.println(playerStateMachine.printTransitions(true));
//		playerStateMachine.changeState(PlayerState.KNIGHT);
//		player.add(engine.createComponent(FSMComponent.class).set(playerStateMachine));
//		return player;
		return null;
	}
	
	public static Entity createKnight(float x, float y){
		final EntityStats knightStats = EntityLoader.get(EntityIndex.KNIGHT);
		
		// Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.KNIGHT_RUN));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.KNIGHT_FALL));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.KNIGHT_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_1, assets.getAnimation(Asset.KNIGHT_CHAIN1_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_2, assets.getAnimation(Asset.KNIGHT_CHAIN2_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_3, assets.getAnimation(Asset.KNIGHT_CHAIN3_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_4, assets.getAnimation(Asset.KNIGHT_CHAIN4_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_1, assets.getAnimation(Asset.KNIGHT_CHAIN1_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_2, assets.getAnimation(Asset.KNIGHT_CHAIN2_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_3, assets.getAnimation(Asset.KNIGHT_CHAIN3_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_4, assets.getAnimation(Asset.KNIGHT_CHAIN4_ANTICIPATION));
		animMap.put(EntityAnim.SWING_1, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.SWING_2, assets.getAnimation(Asset.KNIGHT_CHAIN2_SWING));
		animMap.put(EntityAnim.SWING_3, assets.getAnimation(Asset.KNIGHT_CHAIN3_SWING));
		animMap.put(EntityAnim.SWING_4, assets.getAnimation(Asset.KNIGHT_CHAIN4_SWING));
		animMap.put(EntityAnim.PARRY_BLOCK, assets.getAnimation(Asset.KNIGHT_PARRY_BLOCK));
		animMap.put(EntityAnim.PARRY_SWING, assets.getAnimation(Asset.KNIGHT_PARRY_SWING));
		animMap.put(EntityAnim.KICK, assets.getAnimation(Asset.KNIGHT_KICK));
		animMap.put(EntityAnim.OVERHEAD_SWING, assets.getAnimation(Asset.KNIGHT_OVERHEAD_SWING));
		animMap.put(EntityAnim.SLAM, assets.getAnimation(Asset.KNIGHT_SLAM));
		animMap.put(EntityAnim.DASH, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.SPIN_SLICE, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.TORNADO_INIT, assets.getAnimation(Asset.KNIGHT_TORNADO_INIT));
		animMap.put(EntityAnim.TORNADO_SWING, assets.getAnimation(Asset.KNIGHT_TORNADO_SWING));
		
		Entity knight = new EntityBuilder("knight", EntityType.FRIENDLY)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, knightStats.get("health"))
			.build();
		
		// Abilities
		AntiMagneticAbility antiMagneticAbility = new AntiMagneticAbility(
				knightStats.get("anti_magnetic_cooldown"), 
				Actions.ABILITY_1,
				knightStats.get("anti_magnetic_radius"), 
				knightStats.get("anti_magnetic_duration"));
		antiMagneticAbility.deactivate();
		
		ParryAbility parryAbility = new ParryAbility(
				knightStats.get("parry_cooldown"), 
				Actions.ABILITY_1,
				knightStats.get("parry_max_time"),
				knightStats.get("parry_stun_duration"),
				animMap.get(EntityAnim.PARRY_SWING),
				engine.createComponent(SwingComponent.class)
					.set(3.0f, 2.0f, 90, -135, 4 * GameVars.ANIM_FRAME, 0.0f, knightStats.get("parry_knockback")));
		parryAbility.deactivate();
		
		KickAbility kickAbility = new KickAbility(
				knightStats.get("kick_cooldown"), 
				Actions.ABILITY_1,
				3 * GameVars.ANIM_FRAME, 
				knightStats.get("kick_range"), 
				knightStats.get("kick_knockback"), 
				knightStats.get("kick_damage"),
				animMap.get(EntityAnim.KICK));
		kickAbility.deactivate();
		
		OverheadSwingAbility overheadSwingAbility = new OverheadSwingAbility(
				knightStats.get("overhead_swing_cooldown"),
				Actions.ABILITY_2, 
				animMap.get(EntityAnim.OVERHEAD_SWING),
				engine.createComponent(SwingComponent.class).set(
						knightStats.get("overhead_swing_rx"), 
						knightStats.get("overhead_swing_ry"), 
						120.0f, 
						-40.0f, 
						9 * GameVars.ANIM_FRAME,
						knightStats.get("overhead_swing_damage"),
						knightStats.get("overhead_swing_knockback")));
		overheadSwingAbility.deactivate();
		
		SlamAbility slamAbility = new SlamAbility(
				knightStats.get("slam_cooldown"), 
				Actions.ABILITY_3, 
				animMap.get(EntityAnim.SLAM),
				10,
				knightStats.get("slam_range"),
				knightStats.get("slam_damage"),
				knightStats.get("slam_knockback"),
				knightStats.get("slam_stun_duration"));
		slamAbility.deactivate();
		
		BlacksmithAbility blacksmithAbility = new BlacksmithAbility(
				knightStats.get("blacksmith_cooldown"), 
				Actions.ABILITY_3, 
				knightStats.get("blacksmith_duration"),
				knightStats.get("blacksmith_conversion_chance"), 
				knightStats.get("blacksmith_conversion_percent"), 
				knightStats.get("blacksmith_max_shield"));
		blacksmithAbility.deactivate();
		
		// INCOMPLETE Make it so dash slash can be used in conjunction with chaining
		DashSlashAbility dashSlashAbility = new DashSlashAbility(
				knightStats.get("dash_slash_cooldown"),
				Actions.ABILITY_1, 
				knightStats.get("dash_slash_duration"),
				knightStats.get("dash_slash_distance"),
				knightStats.get("dash_slash_damage"),
				knightStats.get("dash_slash_knock_up"));

		SpinSliceAbility spinSliceAbility = new SpinSliceAbility(
				knightStats.get("spin_slice_cooldown"),
				Actions.ABILITY_2,
				animMap.get(EntityAnim.SPIN_SLICE),
				engine.createComponent(SwingComponent.class).set(
						2.5f, 
						2.5f, 
						0, 
						360, 
						5 * GameVars.ANIM_FRAME, 
						knightStats.get("spin_slice_damage"), 
						knightStats.get("spin_slice_knockback")));
		
		TornadoAbility tornadoAbility = new TornadoAbility(
				knightStats.get("tornado_cooldown"),
				Actions.ABILITY_3, 
				knightStats.get("tornado_duration"),
				knightStats.get("tornado_damage"),
				knightStats.get("tornado_knockback"), 
				knightStats.get("tornado_range"),
				5);
		
		// Player Related Components
		knight.getComponent(ImmuneComponent.class).add(EffectType.KNOCKBACK).add(EffectType.STUN);
		knight.add(engine.createComponent(MoneyComponent.class));
		knight.add(engine.createComponent(PlayerComponent.class));
		knight.add(engine.createComponent(BarrierComponent.class)
				.set(knightStats.get("shield"), 
					 knightStats.get("shield"), 
					 knightStats.get("shield_rate"), 
					 knightStats.get("shield_delay")));
		knight.add(engine.createComponent(AbilityComponent.class)
				.add(antiMagneticAbility)
				.add(parryAbility)
				.add(kickAbility)
				.add(overheadSwingAbility)
				.add(slamAbility)
				.add(blacksmithAbility)
				.add(dashSlashAbility)
				.add(spinSliceAbility)
				.add(tornadoAbility));
		
		KnightComponent knightComp = engine.createComponent(KnightComponent.class).set((int)knightStats.get("max_chains"));
		
		float swordDamage = knightStats.get("sword_damage");
		float knockback = knightStats.get("sword_knockback");
		
		// Setup swings
		SwingComponent swing1 = engine.createComponent(SwingComponent.class).set(1.75f, 1.0f, 120.0f, -180.0f, 0.0f, swordDamage, knockback);
		SwingComponent swing2 = engine.createComponent(SwingComponent.class).set(1.75f, 0.75f, 150.0f, -180.0f, 0.0f, swordDamage, knockback);
		SwingComponent swing3 = engine.createComponent(SwingComponent.class).set(1.75f, 1.25f, 120.0f, -120.0f, 0.0f, swordDamage, knockback);
		SwingComponent swing4 = engine.createComponent(SwingComponent.class).set(1.75f, 1.0f, 135.0f, -120.0f, 0.0f, swordDamage, knockback);
		
		// Setup attacks
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_1, EntityAnim.SWING_ANTICIPATION_1, EntityAnim.SWING_1, swing1);
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_2, EntityAnim.SWING_ANTICIPATION_2, EntityAnim.SWING_2, swing2);
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_3, EntityAnim.SWING_ANTICIPATION_3, EntityAnim.SWING_3, swing3);
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_4, EntityAnim.SWING_ANTICIPATION_4, EntityAnim.SWING_4, swing4);
		
		knight.add(knightComp);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Knight ESM", engine, knight)
			.idle()
			.run(knightStats.get("ground_speed"))
			.jump(knightStats.get("jump_force"), knightStats.get("air_speed"), true, true)
			.fall(knightStats.get("air_speed"), true)
			.climb(5.0f)
			.build();
		
		// Notes
		//	- If transitioning from running, jumping, or falling state skip initial swing animation
		//	- Move forwards slight amount (more if you're coming from running state)
		//	- After swing, move towards anticipation state
		//	- Animations are chosen randomly but NOT repeated when chaining
		//	- Swing animations and anticipation frames have to match up
		//	- Swing always goes to anticipation, which then either goes back to swing or to idle
		
		// Unpack animations
		Array<EntityAnim> idleToSwingAnims = new Array<EntityAnim>();
		Array<EntityAnim> anticipationAnims = new Array<EntityAnim>();
		Array<EntityAnim> swingAnims = new Array<EntityAnim>();

		for(KnightAttack attack : Mappers.knight.get(knight).getAttacks()){
			idleToSwingAnims.add(attack.getIdleAnticipation());
			anticipationAnims.add(attack.getAnticipationAnimation());
			swingAnims.add(attack.getSwingAnimation());
		}
		
		// INCOMPLETE Damage modifier when continuously chaining
		esm.createState(EntityStates.IDLE_TO_SWING)
			.addAnimations(idleToSwingAnims)
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					KnightComponent knightComp = Mappers.knight.get(entity);
					int random = MathUtils.random(knightComp.numAttacks() - 1);
					knightComp.index = random;
					knightComp.first = false;
					
					// Switch to proper animation
					// CLEANUP GROSS!!! 
					Mappers.timer.get(entity).add("anim_timer", 0.0f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							ESMComponent esmComp = Mappers.esm.get(entity);
							AnimationStateMachine asm = esmComp.first().getCurrentStateObject().getASM();
							asm.changeState(Mappers.knight.get(entity).getCurrentAttack().getIdleAnticipation());
						}
					});
					
					// Lock Abilities 
					Mappers.ability.get(entity).lockAllBlocking();
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					
				}
				
			});
		
		esm.createState(EntityStates.SWING_ATTACK)
			.addAnimations(swingAnims) // CLEANUP See entity state.
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					KnightComponent knightComp = Mappers.knight.get(entity);
					BodyComponent bodyComp = Mappers.body.get(entity);
					Body body = bodyComp.body;
					FacingComponent facingComp = Mappers.facing.get(entity);
					
					// Remove gravity and make the player a bullet
					body.setGravityScale(0.0f);
					body.setBullet(true);
					
					float duration = GameVars.ANIM_FRAME;
					float distance = 0.5f;
					boolean first = knightComp.first;

					// Not in a combo yet, pick random attack
					if(first){
						int random = MathUtils.random(knightComp.numAttacks() - 1);
						knightComp.index = random;
						knightComp.first = false;
						
						// Slightly larger distance traveled if you're coming from a running state
						if(prevState == EntityStates.RUNNING){
							distance = 0.75f;
						}
					}
					
					distance *= facingComp.facingRight ? 1 : -1;
					
					if(prevState != EntityStates.IDLE_TO_SWING){
						knightComp.index++;
						if(knightComp.index >= knightComp.numAttacks()) knightComp.index = 0;
					}
					
					// Apply force
					if(first || prevState == EntityStates.IDLE_TO_SWING){
						entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class).set(distance / duration, 0.0f));
					}
					else{
						float toX = knightComp.lungeX;
						float toY = knightComp.lungeY;
						
						float fromX = body.getPosition().x;
						float fromY = body.getPosition().y;
						
						float distanceX = toX - fromX;
						float distanceY = toY - fromY;
						
						entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class).set(distanceX / duration, distanceY / duration));
					}

					// CLEANUP VERY FRAGILE... This only works because we know the state machine system updates before the timer and force system
					Mappers.timer.get(entity).add("lunge_distance", GameVars.ANIM_FRAME + GameVars.UPS_INV, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
						}
					});
					
					// Add swing
					// CLEANUP Copy swing b/c when removed if the pooled engine is implemented the data will get reset
					SwingComponent currSwing = knightComp.getCurrentAttack().getSwingComp();
					Engine engine = Mappers.engine.get(entity).engine;
					entity.add(engine.createComponent(SwingComponent.class).set(
							currSwing.rx, 
							currSwing.ry, 
							currSwing.startAngle, 
							currSwing.endAngle, 
							currSwing.delay + GameVars.UPS_INV + GameVars.ANIM_FRAME, 
							currSwing.damage,
							currSwing.knockback).setEffects(currSwing.effects));
					Mappers.swing.get(entity).shouldSwing = true;
				
					// CLEANUP GROSS!!! 
					Mappers.timer.get(entity).add("anim_timer", 0.0f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							ESMComponent esmComp = Mappers.esm.get(entity);
							AnimationStateMachine asm = esmComp.first().getCurrentStateObject().getASM();
							asm.changeState(Mappers.knight.get(entity).getCurrentAttack().getSwingAnimation());
						}
					});
					
					// Lock Abilities
					Mappers.ability.get(entity).lockAllBlocking();
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.body.get(entity).body.setBullet(false);
					Mappers.body.get(entity).body.setGravityScale(0.2f);
					entity.remove(SwingComponent.class);
				}
			});
		
		esm.createState(EntityStates.SWING_ANTICIPATION)
			.addAnimations(anticipationAnims)
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					// Set velocity to 0
					Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
					Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
					
					// CLEANUP GROSS!!! 
					Mappers.timer.get(entity).add("anim_timer", 0.0f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							ESMComponent esmComp = Mappers.esm.get(entity);
							AnimationStateMachine asm = esmComp.first().getCurrentStateObject().getASM();
							asm.changeState(Mappers.knight.get(entity).getCurrentAttack().getAnticipationAnimation());
						}
					});
				}
				
				@Override
				public void onExit(State nextState, Entity entity) {
					if(nextState != EntityStates.SWING_ATTACK){
						// Put things back to normal
						KnightComponent knightComp = Mappers.knight.get(entity);
						knightComp.first = true;
						knightComp.chains = 0;
						knightComp.hitAnEnemy = false;
						knightComp.hitEnemies.clear();
						
						Mappers.body.get(entity).body.setGravityScale(1.0f);
						Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
						Mappers.ability.get(entity).unlockAllBlocking();
					}else{
						Mappers.knight.get(entity).chains++;
					}
				}
			});
		
		// SWINGING TRANSITIONS
		// ******************************************
		
		InputTransitionData attackPress = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK).build();
		MultiTransition chainAttack = new MultiTransition(Transitions.INPUT, attackPress)
				.and(new Transition() {
					// For combo attack, enemy needs to be in range 
					@Override
					public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
						LevelComponent levelComp = Mappers.level.get(entity);
						LevelHelper helper = levelComp.levelHelper;
						KnightComponent knightComp = Mappers.knight.get(entity);
						
						// You can't chain if you've hit your max number of chains or haven't hit an enemy
						if(knightComp.chains >= knightComp.maxChains || !knightComp.hitAnEnemy) return false;
						
						Array<Entity> entities = helper.getEntities(new EntityGrabber() {
							@Override
							public boolean validEntity(Entity me, Entity other) {
								if(Mappers.type.get(other).same(Mappers.type.get(me))) return false;
								if(Mappers.heatlh.get(other).health <= 0) return false;
								if(Mappers.knight.get(me).hitEnemies.contains(other)) return false;
								
								Body myBody = Mappers.body.get(me).body;
								Body otherBody = Mappers.body.get(other).body;
								
								// Can only chain to enemies in front of you
								FacingComponent facingComp = Mappers.facing.get(me);
								
								float myX = myBody.getPosition().x;
								float myY = myBody.getPosition().y;
								float otherX = otherBody.getPosition().x;
								float otherY = otherBody.getPosition().y;

								float minX = 0.5f;
								float maxX = knightStats.get("chain_max_horizontal_range");
								float yRange = knightStats.get("chain_max_vertical_range");
								
								// Construct box in front of you
								float closeX = facingComp.facingRight ? myX + minX : myX - minX;
								float farX = facingComp.facingRight ? myX + maxX : myX - maxX;
								float top = myY + yRange;
								float bottom = myY - yRange;
								
								// If the enemy is within the box, the last check you need to do is whether or not the enemy is visible
								if(((facingComp.facingRight && otherX >= closeX && otherX <= farX) || (!facingComp.facingRight && otherX >= farX && otherX <= closeX)) 
										&& otherY <= top && otherY >= bottom){
									
									// Ray Trace
									float angle = MathUtils.atan2(otherY - myY, otherX - myX) * MathUtils.radiansToDegrees;
									
									// Check to see what quadrant the angle is in and select two vertices of hit box
									Rectangle myHitbox = Mappers.body.get(me).getAABB();
									Rectangle otherHitbox = Mappers.body.get(other).getAABB();
									
									float x1 = 0.0f;
									float y1 = 0.0f;
									float x2 = 0.0f;
									float y2 = 0.0f;
									
									float off = 0.25f;
									
									float toX1 = 0.0f;
									float toY1 = 0.0f;
									float toX2 = 0.0f;
									float toY2 = 0.0f;

									// Quadrant 1 or 3
									if((angle >= 0 && angle <= 90) || (angle >= -180 && angle <= -90)){
										// Use upper left and lower right
										x1 = myX - myHitbox.width * 0.5f + off;
										y1 = myY + myHitbox.height * 0.5f - off;
										x2 = myX + myHitbox.width * 0.5f - off;
										y2 = myY - myHitbox.height * 0.5f + off;
										toX1 = otherX - otherHitbox.width * 0.5f;
										toY1 = otherY + otherHitbox.height * 0.5f;
										toX2 = otherX + otherHitbox.width * 0.5f;
										toY2 = otherY - otherHitbox.height * 0.5f;
									}
									// Quadrant 2 or 4
									else{
										// Use lower left and upper right
										x1 = myX - myHitbox.width * 0.5f + off;
										y1 = myY - myHitbox.height * 0.5f + off;
										x2 = myX + myHitbox.width * 0.5f - off;
										y2 = myY + myHitbox.height * 0.5f - off;
										toX1 = otherX - otherHitbox.width * 0.5f;
										toY1 = otherY - otherHitbox.height * 0.5f;
										toX2 = otherX + otherHitbox.width * 0.5f;
										toY2 = otherY + otherHitbox.height * 0.5f;
									}
									
//									if(DebugInput.isToggled(DebugToggle.SHOW_CHAIN_BOX)){
//										DebugRender.setType(ShapeType.Line);
//										DebugRender.setColor(Color.CYAN);
//										DebugRender.rect(facingComp.facingRight ? closeX : farX, bottom, Math.abs(farX - closeX), top - bottom);
//										DebugRender.line(x1, y1, toX1, toY1);
//										DebugRender.line(x2, y2, toX2, toY2);
//									}
//									// Slope of normal line is undefined
//									if(MathUtils.isEqual(myY - otherY, 0.0f)){
//										toX1 = otherX;
//										toY1 = y1;
//										toX2 = otherX;
//										toY2 = y2;
//									}else if(MathUtils.isEqual(myX - otherX, 0.0f)){
//										toX1 = x1;
//										toY1 = otherY;
//										toX2 = x2;
//										toY2 = otherY;
//									}
//									else{
//										// Negative reciprocal b/c its the slope of the normal line
//										float slope = (myY - otherY) / (myX - otherX);
//										float slopeNorm = -1.0f / slope;
//										
//										// Find intersect
//										// y - y1 = m(x - x1)
//										// y = mx - mx1 + y1
//										// b = -mx1 + y1
//										float b1 = -slope * x1 + y1;
//										float b2 = -slope * x2 + y2;
//										float bNorm = -slopeNorm * otherX + otherY;
//										
//										// y1 = slope * x1 + b1
//										// y2 = slope * x2 + b2
//										// yNorm = slopeNorm * x? + bNorm
//										
//										// slopeNorm * x + bNorm = slope * x + b1
//										// slopeNorm * x - slope * x = b1 - bNorm
//										// x * (slopeNorm - slope) = b1 - bNorm
//										// x = (b1 - bNorm) / (slopeNorm - slope)
//										
//										toX1 = (b1 - bNorm) / (slopeNorm - slope);
//										toY1 = slopeNorm * toX1 + bNorm;
//										toX2 = (b2 - bNorm) / (slopeNorm - slope);
//										toY2 = slopeNorm * toX2 + bNorm;
//									}
									
									Level level = Mappers.level.get(me).level;
									return level.performRayTrace(x1, y1, toX1, toY1) && level.performRayTrace(x2, y2, toX2, toY2);
								}
								return false;
							}

							@SuppressWarnings("unchecked")
							@Override
							public Family componentsNeeded() {
								return Family.all(LevelComponent.class, TypeComponent.class, HealthComponent.class, BodyComponent.class).get();
							}
						});
						
						if(entities.size == 0) return false;
						final Entity copy = entity;
						Sort.instance().sort(entities, new Comparator<Entity>() {
							@Override
							public int compare(Entity o1, Entity o2) {
								float d1 = PhysicsUtils.getDistanceSqr(Mappers.body.get(copy).body, Mappers.body.get(o1).body);
								float d2 = PhysicsUtils.getDistanceSqr(Mappers.body.get(copy).body, Mappers.body.get(o2).body);
								return d1 == d2 ? 0 : (d1 < d2 ? -1 : 1);
							}
						});
						
						Entity toChain = entities.first();
						Vector2 chainPos = Mappers.body.get(toChain).body.getPosition();
						
						knightComp.lungeX = chainPos.x;
						knightComp.lungeY = chainPos.y;
						
						return true;
					}
					
					@Override
					public boolean allowMultiple() {
						return false;
					}
					
					@Override
					public String toString() {
						return "Can Chain";
					}
				});
		
		Transition enemyNotHit = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return !Mappers.knight.get(entity).hitAnEnemy;
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Enemy Not Hit";
			}
		};
		
		Transition maxChain = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return Mappers.knight.get(entity).chains >= Mappers.knight.get(entity).maxChains;
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Max Chain";
			}
		};
		
		// 
		MultiTransition exitChaining = new MultiTransition(maxChain).or(enemyNotHit).or(Transitions.TIME, new TimeTransitionData(0.3f));
		MultiTransition attackTransition = new MultiTransition(Transitions.INPUT, attackPress).and(Transitions.TIME, new TimeTransitionData(knightStats.get("sword_delay")));
		
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), attackTransition, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.IDLING, attackTransition, EntityStates.IDLE_TO_SWING);
		esm.addTransition(EntityStates.IDLE_TO_SWING, Transitions.ANIMATION_FINISHED, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.SWING_ANTICIPATION);
		esm.addTransition(EntityStates.SWING_ANTICIPATION, chainAttack, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ANTICIPATION, exitChaining, EntityStates.IDLING);
		
		// ******************************************
		
		// Ability States
		esm.createState(EntityStates.PARRY_BLOCK)
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(DirectionComponent.class))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.PARRY_BLOCK);
		
		esm.createState(EntityStates.PARRY_SWING)
			.add(engine.createComponent(FrameMovementComponent.class).set("frames_parry_swing"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.PARRY_SWING);
		
		esm.createState(EntityStates.KICK)
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(DirectionComponent.class))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.KICK);
		
		esm.createState(EntityStates.OVERHEAD_SWING)
			.add(engine.createComponent(FrameMovementComponent.class).set("frames_overhead_swing"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.OVERHEAD_SWING);
		
		esm.createState(EntityStates.SLAM)
			.add(engine.createComponent(FrameMovementComponent.class).set("frames_slam"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SLAM);
		
		esm.createState(EntityStates.DASH)
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.DASH);
		
		esm.createState(EntityStates.SPIN_SLICE)
			.add(engine.createComponent(FrameMovementComponent.class).set("frames_slam"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SPIN_SLICE);
		
		esm.createState(EntityStates.TORNADO)
			.add(engine.createComponent(FrameMovementComponent.class).set("frames_tornado"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.TORNADO_INIT)
			.addAnimation(EntityAnim.TORNADO_SWING)
			.addAnimTransition(EntityAnim.TORNADO_INIT, Transitions.ANIMATION_FINISHED, EntityAnim.TORNADO_SWING);
				
		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, false));

		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

//		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
//		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
		
		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transitions.INPUT, ladderInputData)
					.and(Transitions.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transitions.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transitions.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transitions.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transitions.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transitions.INPUT, bothData, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.LANDED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		return knight;
	}
	
	private static StateMachine<EntityStates, StateObject> createRogueAttackMachine(Entity player, final EntityStats rogueStats){
		final float projectileSpeed = rogueStats.get("projectile_speed");
		final float projectileDamage = rogueStats.get("projectile_damage");
		StateMachine<EntityStates, StateObject> rogueSM = new StateMachine<EntityStates, StateObject>(player, new StateObjectCreator(), EntityStates.class, StateObject.class);
		rogueSM.setDebugName("Rogue Attack SM");
		rogueSM.createState(EntityStates.IDLING);
		rogueSM.createState(EntityStates.PROJECTILE_ATTACK)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.rogue.get(entity).doThrowingAnim = true;
					Mappers.timer.get(entity).add("delayed_throw", 0.2f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							AnimationStateMachine upperBodyASM = Mappers.asm.get(entity).get(EntityAnim.IDLE_ARMS);
							if(upperBodyASM != null && upperBodyASM.getCurrentState() == EntityAnim.THROW_ARMS) {
								ProjectileFactory.spawnThrowingKnife(entity, 5.0f, 0.0f, projectileSpeed, projectileDamage, 0.0f);
							}
						}
					});
				}

				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		
		InputTransitionData attacking = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK).build();
		Transition canThrowTransition = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				AnimationStateMachine upperBodyASM = Mappers.asm.get(entity).get(EntityAnim.IDLE_ARMS);
				return upperBodyASM != null && isDefaultRogueState((EntityAnim) upperBodyASM.getCurrentState());
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Can Throw";
			}
		};
		
		MultiTransition throwTransition = new MultiTransition(Transitions.INPUT, attacking).and(canThrowTransition);
		
		rogueSM.addTransition(EntityStates.IDLING, throwTransition, EntityStates.PROJECTILE_ATTACK);
		rogueSM.addTransition(EntityStates.PROJECTILE_ATTACK, Transitions.TIME, new TimeTransitionData(0.8f), EntityStates.IDLING);
		
		rogueSM.changeState(EntityStates.IDLING);
//		rogueSM.setDebugOutput(true);
		return rogueSM;
	}
	
	private static boolean isBackpedaling(Entity rogue){
		FacingComponent facingComp = Mappers.facing.get(rogue);
		DirectionComponent directionComp = Mappers.direction.get(rogue);
		
		boolean facingRight = facingComp.facingRight;
		return (facingRight && directionComp.direction == Direction.LEFT) || (!facingRight && directionComp.direction == Direction.RIGHT);
	}
	
	public static boolean isDefaultRogueState(EntityAnim anim){
		return anim == EntityAnim.JUMP_ARMS ||
				anim == EntityAnim.RISE_ARMS ||
				anim == EntityAnim.APEX_ARMS ||
				anim == EntityAnim.FALL_ARMS ||
				anim == EntityAnim.RUN_ARMS ||
				anim == EntityAnim.BACK_PEDAL_ARMS ||
				anim == EntityAnim.IDLE_ARMS;
	}
	
	public static boolean isActiveRogueState(EntityAnim anim){
		return anim != null && !isDefaultRogueState(anim);
	}
	
	public static Entity createRogue(float x, float y){
		final EntityStats rogueStats = EntityLoader.get(EntityIndex.ROGUE);
		
		// Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.ROGUE_IDLE_LEGS));
		animMap.put(EntityAnim.IDLE_ARMS, assets.getAnimation(Asset.ROGUE_IDLE_ARMS));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.KNIGHT_FALL));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.KNIGHT_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.ROGUE_RUN_LEGS));
		animMap.put(EntityAnim.RUN_ARMS, assets.getAnimation(Asset.ROGUE_RUN_ARMS));
		animMap.put(EntityAnim.THROW_ARMS, assets.getAnimation(Asset.ROGUE_THROW_ARMS));
		animMap.put(EntityAnim.BACK_PEDAL, assets.getAnimation(Asset.ROGUE_BACK_PEDAL_LEGS));
		animMap.put(EntityAnim.BACK_PEDAL_ARMS, assets.getAnimation(Asset.ROGUE_BACK_PEDAL_ARMS));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.ROGUE_APEX_LEGS));
		animMap.put(EntityAnim.APEX_ARMS, assets.getAnimation(Asset.ROGUE_APEX_ARMS));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.ROGUE_FALL_LEGS));
		animMap.put(EntityAnim.FALL_ARMS, assets.getAnimation(Asset.ROGUE_FALL_ARMS));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.ROGUE_RISE_LEGS));
		animMap.put(EntityAnim.RISE_ARMS, assets.getAnimation(Asset.ROGUE_RISE_ARMS));
		animMap.put(EntityAnim.SLINGHOT_ARMS, assets.getAnimation(Asset.ROGUE_DYNAMITE_ARMS));
		animMap.put(EntityAnim.SMOKE_BOMB_ARMS, assets.getAnimation(Asset.ROGUE_SMOKE_BOMB_ARMS));
		animMap.put(EntityAnim.BOOMERANG_ARMS, assets.getAnimation(Asset.ROGUE_BOOMERANG_ARMS));
		animMap.put(EntityAnim.HOMING_KNIVES_THROW, assets.getAnimation(Asset.ROGUE_HOMING_KNIVES_THROW));
		animMap.put(EntityAnim.DASH, assets.getAnimation(Asset.ROGUE_HOMING_KNIVES_THROW));
		animMap.put(EntityAnim.EXECUTE, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
		
		Entity rogue = new EntityBuilder("rogue", EntityType.FRIENDLY)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, rogueStats.get("health"))
			.build();
		
		// Player Related Components
		rogue.getComponent(ImmuneComponent.class).add(EffectType.KNOCKBACK).add(EffectType.STUN);
		rogue.add(engine.createComponent(MoneyComponent.class));
		rogue.add(engine.createComponent(PlayerComponent.class));
		rogue.add(engine.createComponent(BarrierComponent.class)
				.set(rogueStats.get("shield"), 
					 rogueStats.get("shield"), 
					 rogueStats.get("shield_rate"), 
					 rogueStats.get("shield_delay")));
		rogue.add(engine.createComponent(RogueComponent.class));
		
		Mappers.timer.get(rogue).add("backpedal_timer", GameVars.UPS_INV, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Input input = Mappers.input.get(entity).input;
				FacingComponent facingComp = Mappers.facing.get(entity);
				RogueComponent rogueComp = Mappers.rogue.get(entity);
				AnimationStateMachine upperBodyASM = Mappers.asm.get(entity).get(EntityAnim.IDLE_ARMS);
				
				if(input != null && (input.isPressed(Actions.ATTACK) || upperBodyASM == null || !isDefaultRogueState((EntityAnim)upperBodyASM.getCurrentState()))){
					facingComp.locked = true;
					rogueComp.facingElapsed = 0.0f;
				} else {
					// If you're not attacking and you're in a default state, then add to the elapsed time
					if(input != null){
						boolean left = input.isPressed(Actions.MOVE_LEFT);
						boolean right = input.isPressed(Actions.MOVE_RIGHT);
						if(left == right){
							rogueComp.facingElapsed = rogueComp.facingDelay;
						}
					}
					rogueComp.facingElapsed += GameVars.UPS_INV;
					facingComp.locked = rogueComp.facingElapsed < rogueComp.facingDelay;
				}
			}
		});

		SlingshotAbility slingshotAbility = new SlingshotAbility(
				rogueStats.get("slingshot_cooldown"),
				Actions.ABILITY_1,
				animMap.get(EntityAnim.SLINGHOT_ARMS), 
				rogueStats.get("slingshot_knockback"),
				rogueStats.get("slingshot_damage"));
		slingshotAbility.deactivate();
		
		HomingKnivesAbility homingKnivesAbility = new HomingKnivesAbility(
				rogueStats.get("homing_knives_cooldown"), 
				Actions.ABILITY_1,
				animMap.get(EntityAnim.HOMING_KNIVES_THROW), 
				(int)rogueStats.get("homing_knives_per_cluster"),
				rogueStats.get("homing_knives_damage"),
				rogueStats.get("homing_knives_range"),
				rogueStats.get("homing_knives_speed"));
		
		VanishAbility vanishAbility = new VanishAbility(
				rogueStats.get("vanish_cooldown"),
				Actions.ABILITY_2,
				rogueStats.get("vanish_duration"));
		
		DashAbility dashAbility = new DashAbility(
				rogueStats.get("dash_cooldown"),
				Actions.ABILITY_3,
				rogueStats.get("dash_distance"),
				rogueStats.get("dash_speed"));
		dashAbility.deactivate();
		
		BoomerangAbility boomerangAbility = new BoomerangAbility(
				rogueStats.get("boomerang_cooldown"), 
				Actions.ABILITY_3,
				rogueStats.get("boomerang_speed"),
				rogueStats.get("boomerang_damage"),
				rogueStats.get("boomerang_max_duration"));
		boomerangAbility.deactivate();
		
		ExecuteAbility executeAbility = new ExecuteAbility(
				rogueStats.get("execute_cooldown"), 
				Actions.ABILITY_3, 
				animMap.get(EntityAnim.EXECUTE));
		
		rogue.add(engine.createComponent(AbilityComponent.class)
				.add(slingshotAbility)
				.add(homingKnivesAbility)
				.add(vanishAbility)
				.add(dashAbility)
				.add(boomerangAbility)
				.add(executeAbility));
		
		createRogueAttackMachine(rogue, rogueStats);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Rogue ESM", engine, rogue)
			.idle()
			.run(rogueStats.get("ground_speed"))
			.jump(rogueStats.get("jump_force"), rogueStats.get("air_speed"), true, true)
			.fall(rogueStats.get("air_speed"), true)
			.climb(rogueStats.get("climb_speed"))
			.build();
		
		Transition backpedalingTransition = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return isBackpedaling(entity);
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Backpedaling";
			}
		};
		Transition notBackpedalingTransition = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return !isBackpedaling(entity);
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Not Backpedaling";
			}
		};
		
		AnimationStateMachine upperBodyASM = new AnimationStateMachine(rogue, new StateObjectCreator());
		upperBodyASM.setDebugName("Rogue Upper Body ASM");
		
		upperBodyASM.createState(EntityAnim.INIT)
			.setChangeResolver(new StateChangeResolver() {
				@Override
				public State resolve(Entity entity, State oldState) {
					ESMComponent esmComp = Mappers.esm.get(entity);
					EntityStateMachine esm = esmComp.get(EntityStates.IDLING);
					RogueComponent rogueComp = Mappers.rogue.get(entity);
					
					EntityStates entityState = esm.getCurrentState();
					EntityAnim oldAnim = (EntityAnim) oldState;
					
					// If there is no active ability or you just transitioned from an active state go to default arms
					if(rogueComp.animState == null || isActiveRogueState(oldAnim)){
						switch(entityState){
						case IDLING:
							return EntityAnim.IDLE_ARMS;
						case RUNNING:
							return isBackpedaling(entity) ? EntityAnim.BACK_PEDAL_ARMS : EntityAnim.RUN_ARMS;
						case JUMPING:
							return EntityAnim.RISE_ARMS;
						case FALLING:
							return isActiveRogueState(oldAnim) ? EntityAnim.FALL_ARMS : EntityAnim.APEX_ARMS;
						default:
							throw new RuntimeException("State: " + entityState + " is not a split body state.");
						}
					} else {
						// Set animation to the active animation
						final EntityAnim anim = rogueComp.animState;
						final float time = rogueComp.animTime;
						Mappers.timer.get(entity).add("delayed_time_set", 0.0f, false, new TimeListener() {
							@Override
							public void onTime(Entity entity) {
								Mappers.asm.get(entity).get(anim).setTime(time);
							}
						});
						return anim;
					}
				}
			});
			
		upperBodyASM.createState(EntityAnim.IDLE_ARMS);
		upperBodyASM.createState(EntityAnim.RUN_ARMS);
		upperBodyASM.createState(EntityAnim.BACK_PEDAL_ARMS);
		upperBodyASM.createState(EntityAnim.RISE_ARMS);
		upperBodyASM.createState(EntityAnim.APEX_ARMS);
		upperBodyASM.createState(EntityAnim.FALL_ARMS);
		upperBodyASM.createState(EntityAnim.THROW_ARMS).addChangeListener(new StateChangeListener() {
			@Override
			public void onEnter(State prevState, Entity entity) {
				Mappers.rogue.get(entity).doThrowingAnim = false;
			}

			@Override
			public void onExit(State nextState, Entity entity) {
			}
		});
		upperBodyASM.createState(EntityAnim.SLINGHOT_ARMS);
		upperBodyASM.createState(EntityAnim.FLASH_POWDER_ARMS);
		upperBodyASM.createState(EntityAnim.BOOMERANG_ARMS);
		upperBodyASM.createState(EntityAnim.SMOKE_BOMB_ARMS);
		
		upperBodyASM.setGlobalChangeListener(new GlobalChangeListener() {
			@Override
			public void onChange(Entity entity, State oldState, State newState) {
				boolean exit = newState == null;
				EntityAnim state = exit ? (EntityAnim) oldState : (EntityAnim) newState;
				RogueComponent rogueComp = Mappers.rogue.get(entity);
				
				if(isDefaultRogueState(state)){
					// not an active state
					rogueComp.animState = null;
					rogueComp.animTime = 0.0f;
					
					if(!exit){
						ASMComponent asmComp = Mappers.asm.get(entity);
						final AnimationStateMachine upperBodyASM = asmComp.get(EntityAnim.RUN_ARMS);
						final AnimationStateMachine lowerBodyASM = asmComp.get(EntityAnim.RUN);
	
						// CLEANUP More 0 second timers....
						Mappers.timer.get(entity).add("delayed_time_set", 0.0f, false, new TimeListener() {
							@Override
							public void onTime(Entity entity) {
								upperBodyASM.setTime(lowerBodyASM.getAnimationTime());							
							}
						});
					}
					
				}
				else {
					// active state
					// save the time and the state
					rogueComp.animState = state;
					rogueComp.animTime = Mappers.asm.get(entity).get(EntityAnim.RUN_ARMS).getAnimationTime();
				}
			}
		});
		
		// Upper Body Transitions
		upperBodyASM.addTransition(EntityAnim.APEX_ARMS, Transitions.ANIMATION_FINISHED, EntityAnim.FALL_ARMS);
		upperBodyASM.addTransition(EntityAnim.RUN_ARMS, backpedalingTransition, EntityAnim.BACK_PEDAL_ARMS);
		upperBodyASM.addTransition(EntityAnim.BACK_PEDAL_ARMS, notBackpedalingTransition, EntityAnim.RUN_ARMS);
		upperBodyASM.addTransition(upperBodyASM.one(
						EntityAnim.APEX_ARMS, 
						EntityAnim.RUN_ARMS, 
						EntityAnim.BACK_PEDAL_ARMS, 
						EntityAnim.FALL_ARMS, 
						EntityAnim.JUMP_ARMS, 
						EntityAnim.RISE_ARMS,
						EntityAnim.IDLE_ARMS), new Transition() {
							@Override
							public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
								return Mappers.rogue.get(entity).doThrowingAnim;
							}
							
							@Override
							public boolean allowMultiple() {
								return false;
							}
						}, EntityAnim.THROW_ARMS);
		upperBodyASM.addTransition(EntityAnim.THROW_ARMS, Transitions.ANIMATION_FINISHED, EntityAnim.INIT);
		upperBodyASM.addTransition(EntityAnim.SMOKE_BOMB_ARMS, Transitions.ANIMATION_FINISHED, EntityAnim.INIT);
		upperBodyASM.addTransition(EntityAnim.BOOMERANG_ARMS, Transitions.ANIMATION_FINISHED, EntityAnim.INIT);
		
		AnimationStateMachine lowerBodyASM = new AnimationStateMachine(rogue, new StateObjectCreator());
		lowerBodyASM.setDebugName("Rogue Lower Body ASM");

		lowerBodyASM.createState(EntityAnim.INIT).setChangeResolver(new StateChangeResolver() {
			@Override
			public State resolve(Entity entity, State oldState) {
				ESMComponent esmComp = Mappers.esm.get(entity);
				EntityStateMachine esm = esmComp.get(EntityStates.IDLING);

				EntityStates entityState = esm.getCurrentState();
				
				switch(entityState){
				case IDLING:
					return EntityAnim.IDLE;
				case RUNNING:
					return isBackpedaling(entity) ? EntityAnim.BACK_PEDAL : EntityAnim.RUN;
				case JUMPING:
					return EntityAnim.RISE;
				case FALLING:
					return EntityAnim.FALLING;
				default: 
					throw new RuntimeException("State: " + entityState + " is not a split body state.");
				}
			}
		});
		
		lowerBodyASM.createState(EntityAnim.IDLE);
		lowerBodyASM.createState(EntityAnim.RUN);
		lowerBodyASM.createState(EntityAnim.BACK_PEDAL);
		lowerBodyASM.createState(EntityAnim.JUMP);
		lowerBodyASM.createState(EntityAnim.RISE);
		lowerBodyASM.createState(EntityAnim.JUMP_APEX);
		lowerBodyASM.createState(EntityAnim.FALLING);
		
		lowerBodyASM.addTransition(EntityAnim.RUN, backpedalingTransition, EntityAnim.BACK_PEDAL);
		lowerBodyASM.addTransition(EntityAnim.BACK_PEDAL, notBackpedalingTransition, EntityAnim.RUN);
		
		// Add machines to split body states
		EntityState idleState = esm.getState(EntityStates.IDLING);
		idleState.removeAnimations();
		idleState.addSubstateMachine(lowerBodyASM);
		idleState.addSubstateMachine(upperBodyASM);
		
		EntityState runState = esm.getState(EntityStates.RUNNING);
		runState.removeAnimations();
		runState.addSubstateMachine(lowerBodyASM);
		runState.addSubstateMachine(upperBodyASM);
		
		EntityState jumpState = esm.getState(EntityStates.JUMPING);
		jumpState.removeAnimations();
		jumpState.addSubstateMachine(lowerBodyASM);
		jumpState.addSubstateMachine(upperBodyASM);
		
		EntityState fallState = esm.getState(EntityStates.FALLING);
		fallState.removeAnimations();
		fallState.addSubstateMachine(lowerBodyASM);
		fallState.addSubstateMachine(upperBodyASM);
		
//		esm.createState(EntityStates.DASH)
//			.addAnimation(EntityAnim.SWING)
//			.addChangeListener(new StateChangeListener(){
//				@Override
//				public void onEnter(State prevState, Entity entity) {
//					BodyComponent bodyComp = Mappers.body.get(entity);
//					FacingComponent facingComp = Mappers.facing.get(entity);
//					ForceComponent forceComp = Mappers.engine.get(entity).engine.createComponent(ForceComponent.class);
//					
//					Body body = bodyComp.body;
//					body.setLinearVelocity(body.getLinearVelocity().x, 0);
//					body.setGravityScale(0.0f);
//					
//					// CLEANUP Dash speed should be in config file
//					forceComp.set(facingComp.facingRight ? 30f : -30f, 0);
//					entity.add(forceComp);
//				}
//
//				@Override
//				public void onExit(State nextState, Entity entity) {
//					BodyComponent bodyComp = Mappers.body.get(entity);
//					Body body = bodyComp.body;
//					
//					body.setLinearVelocity(0.0f, 0.0f);
//					body.setGravityScale(1.0f);
//				}
//			});
				
		esm.createState(EntityStates.HOMING_KNIVES)
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(DirectionComponent.class))
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.addAnimation(EntityAnim.HOMING_KNIVES_THROW);
		
		esm.createState(EntityStates.EXECUTE)
			.addAnimation(EntityAnim.EXECUTE);
			
		esm.createState(EntityStates.DASH)
			.addAnimation(EntityAnim.DASH);
		
		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));

		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
		
		MultiTransition idleTransition = new MultiTransition(Transitions.INPUT, idleData).or(Transitions.INPUT, bothData);

		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		
		InputTransitionData ladderInputData = new InputTransitionData.Builder(Type.ANY_ONE, true)
					.add(Actions.MOVE_UP)
					.add(Actions.MOVE_DOWN)
					.build();
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transitions.INPUT, ladderInputData)
			.and(Transitions.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
//		CollisionTransitionData onRightWallData = new CollisionTransitionData(CollisionType.RIGHT_WALL, true);
//		CollisionTransitionData onLeftWallData = new CollisionTransitionData(CollisionType.LEFT_WALL, true);
//		CollisionTransitionData offRightWallData = new CollisionTransitionData(CollisionType.RIGHT_WALL, false);
//		CollisionTransitionData offLeftWallData = new CollisionTransitionData(CollisionType.LEFT_WALL, false);
//
//		InputTransitionData rightWallInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_RIGHT).build();
//		InputTransitionData leftWallInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_LEFT).build();
//		
//		MultiTransition rightSlideTransition = new MultiTransition(Transitions.FALLING)
//			.and(Transitions.COLLISION, onRightWallData)
//			.and(Transitions.INPUT, rightWallInput);
//		
//		MultiTransition leftSlideTransition = new MultiTransition(Transitions.FALLING)
//			.and(Transitions.FALLING)
//			.and(Transitions.COLLISION, onLeftWallData)
//			.and(Transitions.INPUT, leftWallInput);
		
//		MultiTransition wallSlideTransition = new MultiTransition(rightSlideTransition).or(leftSlideTransition);
//		
//		InputTransitionData offRightWallInput = new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_RIGHT).build();
//		InputTransitionData offLeftWallInput = new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_LEFT).build();
		
//		MultiTransition offRightWall = new MultiTransition(Transitions.COLLISION, onRightWallData)
//				.and(Transitions.INPUT, offRightWallInput);
//		MultiTransition offLeftWall = new MultiTransition(Transitions.COLLISION, onLeftWallData)
//				.and(Transitions.INPUT, offLeftWallInput);
		
		// (offWallLeft && offWallRight) || (onWallLeft && offInputLeft) || (onWallRight && offInputRight)
//		MultiTransition offWall = new MultiTransition(new MultiTransition(Transitions.COLLISION, offRightWallData)
//				.and(Transitions.COLLISION, offLeftWallData))
//				.or(offRightWall)
//				.or(offLeftWall);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transitions.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transitions.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, EntityStates.WALL_SLIDING).exclude(EntityStates.JUMPING), Transitions.LANDED, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), idleTransition, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.LANDED, EntityStates.IDLING);
//		esm.addTransition(EntityStates.FALLING, wallSlideTransition, EntityStates.WALL_SLIDING);
//		esm.addTransition(EntityStates.WALL_SLIDING, offWall, EntityStates.FALLING);
//		esm.addTransition(EntityStates.WALL_SLIDING, Transitions.INPUT, jumpData, EntityStates.WALL_JUMP);
		
		esm.changeState(EntityStates.IDLING);
		
//		esm.addTransition(EntityStates.DASH, Transitions.COLLISION, onRightWallData, EntityStates.FALLING);
//		esm.addTransition(EntityStates.DASH, Transitions.COLLISION, onLeftWallData, EntityStates.FALLING);
//		System.out.print(esm.printTransitions(false));
		return rogue;
	}
	
	public static Entity createMage(float x, float y){
//		Entity sword = createSword(engine, world, level, player, x, y, 100);
		
		final EntityStats mageStats = EntityLoader.get(EntityIndex.MAGE);
		
		// Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.MAGE_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.MAGE_RUN));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.KNIGHT_FALL));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.KNIGHT_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.KNIGHT_APEX));
		animMap.put(EntityAnim.SWING_1, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
		
		Entity mage = new EntityBuilder("mage", EntityType.FRIENDLY)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, mageStats.get("health"))
			.build();
		
		// Player Related Components
		mage.getComponent(ImmuneComponent.class).add(EffectType.KNOCKBACK).add(EffectType.STUN);
		mage.add(engine.createComponent(MoneyComponent.class));
		mage.add(engine.createComponent(PlayerComponent.class));
		mage.add(engine.createComponent(BarrierComponent.class)
				.set(mageStats.get("shield"), 
					 mageStats.get("shield"), 
					 mageStats.get("shield_rate"), 
					 mageStats.get("shield_delay")));
		mage.add(engine.createComponent(AbilityComponent.class)
			.add(new ManaBombAbility(mageStats.get("mana_bomb_cooldown"), Actions.ATTACK)));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Mage ESM", engine, mage)
			.idle()
			.run(mageStats.get("ground_speed"))
			.jump(mageStats.get("jump_force"), mageStats.get("air_speed"), true, true)
			.fall(mageStats.get("air_speed"), true)
			.climb(mageStats.get("climb_speed"))
//			.swingAttack(sword, 150f, 210f, 0.6f, 25f)
			.build();
		
		esm.createState(EntityStates.PROJECTILE_ATTACK)
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(DirectionComponent.class))
			.add(engine.createComponent(GroundMovementComponent.class))
			.addAnimation(EntityAnim.SWING_1)
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					ProjectileData data = ProjectileFactory.initProjectile(entity, 0.0f, 0.0f, 0.0f);
					Entity bomb = EntityFactory.createManaBomb(data.x, data.y, data.angle, mageStats.get("mana_bomb_damage"), 5.0f, EntityType.FRIENDLY);
					EntityManager.addEntity(bomb);
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					
				}
			});
				
		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));

		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

//		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
//		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
		
		// Attack
//		InputTransitionData attackData = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();
//		AbilityTransitionData manaBombAbility = new AbilityTransitionData(AbilityType.MANA_BOMB);
//		MultiTransition attackTransition = new MultiTransition(Transitions.INPUT, attackData)
//				.and(Transitions.ABILITY, manaBombAbility);
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transitions.INPUT, ladderInputData)
			.and(Transitions.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
		
		esm.addTransition(TransitionTag.GROUND_STATE, Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transitions.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transitions.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transitions.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transitions.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transitions.INPUT, bothData, EntityStates.IDLING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transitions.INPUT, diveData, EntityStates.DIVING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.SWING_ATTACK), attackTransition, EntityStates.SWING_ATTACK);
//		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.LANDED, EntityStates.IDLING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.PROJECTILE_ATTACK), attackTransition, EntityStates.PROJECTILE_ATTACK);
		esm.addTransition(EntityStates.PROJECTILE_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
//		esm.addTransition(EntityStates.BASE_ATTACK, Transitions.TIME, new TimeTransitionData(0.2f), EntityStates.IDLING);
		
//		System.out.print(esm.printTransitions(true));

		esm.changeState(EntityStates.IDLING);
		
//		fsm.disableState(EntityStates.DIVING);
//		esm.changeState(EntityStates.IDLING);
		return mage;
	}
	
	// ---------------------------------------------
	// -                 ENEMIES                   -
	// ---------------------------------------------
	public static Entity createAIPlayer(float x, float y) {
		// Stats
		EntityStats stats = EntityLoader.get(EntityIndex.AI_PLAYER);
		NavMesh mesh = NavMesh.get(EntityIndex.AI_PLAYER);
		PathFinder pathFinder = new PathFinder(mesh);
		
		// Setup Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.KNIGHT_RUN));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.KNIGHT_FALL));
		animMap.put(EntityAnim.RANDOM_IDLE, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.KNIGHT_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.SWING, assets.getAnimation(Asset.KNIGHT_OVERHEAD_SWING));
		
		// Controller
		AIController controller = new AIController();
		
		// Setup Player
		Entity player = new EntityBuilder(EntityIndex.AI_PLAYER.getName(), EntityType.ENEMY)
				.animation(animMap)
				.mob(controller, stats.get("health"))
				.physics("player.json", x, y, true)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0), true)
				.build();
		float baseMoney = stats.get("money");
		int money = (int)MathUtils.random(baseMoney - 0.1f * baseMoney, baseMoney + 0.1f * baseMoney);
		player.add(engine.createComponent(MoneyComponent.class).set(money));
		player.add(engine.createComponent(AIControllerComponent.class).set(controller));
		player.add(engine.createComponent(TargetComponent.class));
		player.add(engine.createComponent(PathComponent.class).set(pathFinder));
		player.add(engine.createComponent(TintComponent.class).set(Color.RED));

		EntityStateMachine esm = new StateFactory.EntityStateBuilder("AI Player ESM", engine, player)
			.idle()
			.run(stats.get("ground_speed"))
			.fall(stats.get("air_speed"), true)
			.jump(stats.get("jump_force"), stats.get("air_speed"), true, true)
			.climb(stats.get("climb_speed"))
			.swingAttack(2.5f, 1.0f, 150f, -90f, 0.4f, stats.get("sword_damage"), stats.get("sword_knockback"))
			.build();
		
		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));

		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));

		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		TimeTransitionData attackCooldown = new TimeTransitionData(0.5f);
		
		MultiTransition attackTransition = new MultiTransition(Transitions.INPUT, attackData)
				.and(Transitions.TIME, attackCooldown);
		
		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
		
		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
		
		MultiTransition ladderTransition = new MultiTransition(Transitions.INPUT, ladderInputData)
				.and(Transitions.COLLISION, ladderCollisionData);
		
		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);

		esm.addTransition(TransitionTag.GROUND_STATE, Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transitions.INPUT, runningData, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transitions.INPUT, jumpData, EntityStates.JUMPING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING), Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transitions.LANDED, EntityStates.IDLING);
		esm.addTransition(EntityStates.RUNNING, Transitions.INPUT, idleData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transitions.INPUT, bothData, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.SWING_ATTACK), attackTransition, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.COLLISION, ladderFall, EntityStates.FALLING);
		esm.addTransition(EntityStates.CLIMBING, Transitions.LANDED, EntityStates.IDLING);
//		System.out.print(esm.printTransitions());
		
		esm.changeState(EntityStates.IDLING);
		
		AIStateMachine aism = new AIStateMachine(player);
		aism.setDebugName("AI Player AISM");
		aism.createState(AIState.WANDERING)
			.add(engine.createComponent(WanderingComponent.class).set(20, 1.5f));
		
		aism.createState(AIState.FOLLOWING)
			.add(engine.createComponent(FollowComponent.class));
		
		aism.createState(AIState.ATTACKING)
			.add(engine.createComponent(AttackComponent.class));
		
		LOSTransitionData inSightData = new LOSTransitionData(true);
		LOSTransitionData outOfSightData = new LOSTransitionData(false);
		
		RangeTransitionData wanderInRange = new RangeTransitionData();
		wanderInRange.distance = 15.0f;
		wanderInRange.fov = 180.0f;
		wanderInRange.inRange = true;
		
		MultiTransition wanderToFollow = new MultiTransition(Transitions.RANGE, wanderInRange)
			.and(Transitions.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData followOutOfRange = new RangeTransitionData();
		followOutOfRange.distance = 15.0f;
		followOutOfRange.fov = 180.0f;
		followOutOfRange.inRange = false;

		MultiTransition followToWander = new MultiTransition(Transitions.RANGE, followOutOfRange)
			.or(Transitions.LINE_OF_SIGHT, outOfSightData);
		
		RangeTransitionData inAttackRange = new RangeTransitionData();
		inAttackRange.distance = 1.5f;
		inAttackRange.fov = 180.0f;
		inAttackRange.inRange = true;
		
		MultiTransition toAttackTransition = new MultiTransition(Transitions.RANGE, inAttackRange)
			.and(Transitions.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData outOfAttackRange = new RangeTransitionData();
		outOfAttackRange.distance = 2.5f;
		outOfAttackRange.fov = 180.0f;
		outOfAttackRange.inRange = false;
		
		MultiTransition fromAttackTransition = new MultiTransition(Transitions.RANGE, outOfAttackRange)
			.or(Transitions.LINE_OF_SIGHT, outOfSightData);
		
//		InvalidEntityData invalidEntity = new InvalidEntityData(toFollow);
		
		aism.addTransition(AIState.WANDERING, wanderToFollow, AIState.FOLLOWING);
		aism.addTransition(AIState.FOLLOWING, followToWander, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.FOLLOWING, AIState.ATTACKING), Transitions.INVALID_ENTITY/*, invalidEntity*/, AIState.WANDERING);
		aism.addTransition(AIState.FOLLOWING, toAttackTransition, AIState.ATTACKING);
		aism.addTransition(AIState.ATTACKING, fromAttackTransition, AIState.FOLLOWING);
		
		aism.changeState(AIState.WANDERING);
//		System.out.println(aism.printTransitions());
		return player;
	}
	
	public static Entity createSpitter(float x, float y){
		// Stats
		final EntityStats stats = EntityLoader.get(EntityIndex.SPITTER);
		
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.SPITTER_IDLE));
		animMap.put(EntityAnim.DYING, assets.getAnimation(Asset.SPITTER_DEATH));
		animMap.put(EntityAnim.ATTACK, assets.getAnimation(Asset.SPITTER_ATTACK));
//		animMap.put(EntityAnim.FLAPPING, assets.getAnimation(Assets.spitterWings));
		AIController controller = new AIController();
		Entity entity = new EntityBuilder(EntityIndex.SPITTER.getName(), EntityType.ENEMY)
				.animation(animMap)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
				.physics("spitter.json", new BodyProperties.Builder().setGravityScale(0.0f).build(), x, y, false)
				.mob(controller, stats.get("health"))
				.build();
		
		float baseMoney = stats.get("money");
		int money = (int)MathUtils.random(baseMoney - 0.1f * baseMoney, baseMoney + 0.1f * baseMoney);
		entity.add(engine.createComponent(AIControllerComponent.class).set(controller));
		entity.add(engine.createComponent(TargetComponent.class).set(new TargetComponent.DefaultTargetBehavior(15.0f * 15.0f)));
		entity.add(engine.createComponent(MoneyComponent.class).set(money));
		entity.add(engine.createComponent(BobComponent.class).set(2.0f, 4.0f * GameVars.PPM_INV)); // 0.5f loop (2 cycles in one second), 16 pixel height
		entity.getComponent(DeathComponent.class).add(new DeathBehavior(){
			@Override
			public void onDeath(Entity entity) {
				Mappers.body.get(entity).body.setActive(false);
				Mappers.wing.get(entity).wings.add(new RemoveComponent());
				Mappers.esm.get(entity).first().changeState(EntityStates.DYING);
			}
		});
		
		Entity wings = createWings(entity, x, y, -0.8f, 0.5f, assets.getAnimation(Asset.SPITTER_WINGS));
		entity.add(engine.createComponent(WingComponent.class).set(wings));
		entity.add(engine.createComponent(ChildrenComponent.class).add(wings));
		EntityManager.addEntity(wings);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Spitter ESM", engine, entity)
			.build();
		
		// Wings
//		AnimationStateMachine wingsASM = new AnimationStateMachine(entity, new StateObjectCreator());
//		wingsASM.createState(EntityAnim.FLAPPING);
//		wingsASM.changeState(EntityAnim.FLAPPING);
		
		esm.createState(EntityStates.FLYING)
			.add(engine.createComponent(SpeedComponent.class).set(stats.get("air_speed")))
			.add(engine.createComponent(FlyingComponent.class))
			.add(engine.createComponent(FlowFieldComponent.class))
			.addAnimation(EntityAnim.IDLE);
		
		esm.createState(EntityStates.PROJECTILE_ATTACK)
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(FlyingComponent.class))
			.add(engine.createComponent(FlowFieldComponent.class))
			.addAnimation(EntityAnim.ATTACK)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.timer.get(entity).add("spit_delay", 0.4f, false, new TimeListener(){
						@Override
						public void onTime(Entity entity) {
							ProjectileFactory.spawnSpitProjectile(entity, 5.0f, 2.0f, stats.get("projectile_speed"), stats.get("projectile_damage"), 0.0f, stats.get("projectile_time"));
						}
					});
				}

				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		
		esm.createState(EntityStates.DYING)
			.addAnimation(EntityAnim.DYING)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					// Remove wings
//					Mappers.asm.get(entity).remove(Mappers.asm.get(entity).get(EntityAnim.FLAPPING));
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					entity.add(new RemoveComponent());
				}
			});
		
		esm.changeState(EntityStates.FLYING);
		
		// Attack Input
		InputTransitionData attackInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();
		TimeTransitionData attackCooldown = new TimeTransitionData(2.0f);
		
		// Attack Transitions
		esm.addTransition(EntityStates.FLYING, new MultiTransition(Transitions.INPUT, attackInput).and(Transitions.TIME, attackCooldown), EntityStates.PROJECTILE_ATTACK);
		esm.addTransition(EntityStates.PROJECTILE_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.FLYING);

		// After death
		esm.addTransition(EntityStates.DYING, Transitions.ANIMATION_FINISHED, EntityStates.FLYING);
		
		AIStateMachine aism = new  AIStateMachine(entity);
		aism.setDebugName("Spitter AISM");
		aism.createState(AIState.WANDERING);
		aism.createState(AIState.FOLLOWING)
			.add(engine.createComponent(FlowFollowComponent.class))
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.aiController.get(entity).controller.releaseAll();
				}
			});
		aism.createState(AIState.ATTACKING)
			.add(engine.createComponent(AttackComponent.class))
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
				}
				
				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.aiController.get(entity).controller.releaseAll();
				}
			});
		
//		LOSTransitionData inSightData = new LOSTransitionData(true);
//		LOSTransitionData outOfSightData = new LOSTransitionData(false);
		
		RangeTransitionData wanderInRange = new RangeTransitionData();
		wanderInRange.distance = 15.0f;
		wanderInRange.inRange = true;
		
		MultiTransition wanderToFollow = new MultiTransition(Transitions.RANGE, wanderInRange);
//			.and(Transitions.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData followOutOfRange = new RangeTransitionData();
		followOutOfRange.distance = 15.0f;
		followOutOfRange.inRange = false;

		MultiTransition followToWander = new MultiTransition(Transitions.RANGE, followOutOfRange);
//			.or(Transitions.LINE_OF_SIGHT, outOfSightData);
		
		RangeTransitionData inAttackRange = new RangeTransitionData();
		inAttackRange.distance = 8.0f;
		inAttackRange.fov = 30.0f;
		inAttackRange.inRange = true;
		
		MultiTransition toAttackTransition = new MultiTransition(Transitions.RANGE, inAttackRange);
//			.and(Transitions.LINE_OF_SIGHT, inSightData);
		
		RangeTransitionData outOfAttackRange = new RangeTransitionData();
		outOfAttackRange.distance = 9.0f;
		outOfAttackRange.fov = 30.0f;
		outOfAttackRange.inRange = false;
		
		MultiTransition fromAttackTransition = new MultiTransition(Transitions.RANGE, outOfAttackRange);
//			.or(Transitions.LINE_OF_SIGHT, outOfSightData);
		
//		InvalidEntityData invalidEntity = new InvalidEntityData(toFollow);
		
		aism.addTransition(AIState.WANDERING, wanderToFollow, AIState.FOLLOWING);
		aism.addTransition(AIState.FOLLOWING, followToWander, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.FOLLOWING, AIState.ATTACKING), Transitions.INVALID_ENTITY/*, invalidEntity*/, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.WANDERING, AIState.FOLLOWING), toAttackTransition, AIState.ATTACKING);
		aism.addTransition(AIState.ATTACKING, fromAttackTransition, AIState.FOLLOWING);
		
		aism.changeState(AIState.WANDERING);

		return entity;
	}
	
	public static Entity createSlime(float x, float y){
		// Stats
		final EntityStats stats = EntityLoader.get(EntityIndex.SLIME);
		
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.SLIME_IDLE));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.SLIME_JUMP));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.SLIME_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.SLIME_APEX));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.SLIME_FALL));
		animMap.put(EntityAnim.LAND, assets.getAnimation(Asset.SLIME_LAND));
		
		AIController controller = new AIController();
		
		Entity slime = new EntityBuilder(EntityIndex.SLIME.getName(), EntityType.ENEMY)
				.mob(controller, stats.get("health"))
				.physics("slime.json", x, y, true)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
				.animation(animMap)
				.build();
		float baseMoney = stats.get("money");
		int money = (int)MathUtils.random(baseMoney - 0.1f * baseMoney, baseMoney + 0.1f * baseMoney);
		slime.add(engine.createComponent(AIControllerComponent.class).set(controller));
		slime.add(engine.createComponent(MoneyComponent.class).set(money));
		slime.add(engine.createComponent(DamageComponent.class).set(stats.get("damage")));
		
		final float SPEED = stats.get("air_speed");
		final float JUMP_FORCE = stats.get("jump_force");
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Slime ESM", engine, slime)
				.idle()
				.fall(SPEED, true)
				.jump(JUMP_FORCE, SPEED, false, false)
				.build();
		
		esm.getState(EntityStates.JUMPING)
				.addChangeListener(new StateChangeListener() {
					@Override
					public void onEnter(State prevState, Entity entity) {
						// Save jump data to use later
						InputComponent inputComp = Mappers.input.get(entity);
						final float jForce = Mappers.jump.get(entity).maxForce;
						final float jMultiplier = inputComp.input.getValue(Actions.JUMP);
						final boolean facingRight = Mappers.facing.get(entity).facingRight;
						final float rMultiplier = facingRight ? inputComp.input.getValue(Actions.MOVE_RIGHT) : inputComp.input.getValue(Actions.MOVE_LEFT);
						
						Mappers.aiController.get(entity).controller.releaseAll();
						entity.remove(JumpComponent.class);
						
						Mappers.timer.get(entity).add("jump", 0.25f, false, new TimeListener() {
							@Override
							public void onTime(Entity entity) {
								AIController controller = Mappers.aiController.get(entity).controller;
								controller.press(facingRight ? Actions.MOVE_RIGHT : Actions.MOVE_LEFT, rMultiplier);
								entity.add(Mappers.engine.get(entity).engine.createComponent(JumpComponent.class).set(jForce));
								Mappers.jump.get(entity).multiplier = jMultiplier;
							}
						});
					}

					@Override
					public void onExit(State nextState, Entity entity) {
					}
				});
		
		esm.createState(EntityStates.LANDING)
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.addAnimation(EntityAnim.LAND);
		
		MultiTransition jumpTransition = new MultiTransition(Transitions.INPUT, new InputTransitionData.Builder(Type.ALL, true).add(Actions.JUMP, true).build());
		
		esm.addTransition(EntityStates.FALLING, Transitions.LANDED, EntityStates.LANDING);
		esm.addTransition(EntityStates.LANDING, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(EntityStates.IDLING, jumpTransition, EntityStates.JUMPING);
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE, TransitionTag.AIR_STATE), Transitions.FALLING, EntityStates.FALLING);
		
		esm.changeState(EntityStates.IDLING);
		
		AIStateMachine aism = new AIStateMachine(slime);
		aism.setDebugName("Slime AISM");
		aism.createState(AIState.WANDERING)
				.add(engine.createComponent(BehaviorComponent.class).set(new AIBehavior() {
					@Override
					public void update(Entity entity, float deltaTime) {
						if(Mappers.esm.get(entity).first().getCurrentState() != EntityStates.IDLING) return;
						
						TimerComponent timerComp = Mappers.timer.get(entity);
						if(!timerComp.timers.containsKey("jump_delay")){
							timerComp.add("jump_delay", MathUtils.random(2.5f, 3.5f), false, new TimeListener() {
								@Override
								public void onTime(Entity entity) {
									AIController controller = Mappers.aiController.get(entity).controller;
									boolean right = MathUtils.randomBoolean();
									float xMult = MathUtils.random(0.5f, 1.0f);
									
									controller.releaseAll();
									if(right){
										controller.press(Actions.MOVE_RIGHT, xMult);
									}else{
										controller.press(Actions.MOVE_LEFT, xMult);
									}
									controller.justPress(Actions.JUMP);
								}
							});
						}
					}
				}));
		
		aism.createState(AIState.ATTACKING)
				.add(engine.createComponent(BehaviorComponent.class).set(new AIBehavior() {
					@Override
					public void update(Entity entity, float deltaTime) {
					}
				}));
		
		
		aism.changeState(AIState.WANDERING);
		return slime;
	}
	
	public static Entity createSpawner(float x, float y){
		EntityStats stats = EntityLoader.get(EntityIndex.SPAWNER);
		AIController controller = new AIController();
		
		Entity spawner = new EntityBuilder(EntityIndex.SPAWNER.getName(), EntityType.ENEMY)
				.physics("spawner.json", new BodyProperties.Builder().setSleepingAllowed(false).build(), x, y, false)
				.mob(controller, stats.get("health"))
				.build();
		
		float baseMoney = stats.get("money");
		int money = (int)MathUtils.random(baseMoney - 0.1f * baseMoney, baseMoney + 0.1f * baseMoney);
		spawner.add(engine.createComponent(MoneyComponent.class).set(money));
	
		spawner.add(engine.createComponent(SpawnerPoolComponent.class));
		SpawnerPoolComponent spawnerPool = Mappers.spawnerPool.get(spawner);
		for(String attr : stats.getStatsMap().keys()){
			EntityIndex index = EntityIndex.get(attr);
			if(index != null){
				spawnerPool.add(index, stats.get(attr));
			}
		}
		spawner.getComponent(ImmuneComponent.class).add(EffectType.KNOCKBACK).add(EffectType.STUN);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Spawner ESM", engine, spawner).build();
		
		esm.createState(EntityStates.IDLING);
		esm.createState(EntityStates.BASE_ATTACK)
			.add(engine.createComponent(SpawnComponent.class));

		float spawnDelay = stats.get("spawn_delay");
		RandomTransitionData rtd = new RandomTransitionData(spawnDelay - 0.1f * spawnDelay, spawnDelay + 0.1f * spawnDelay);
		
		esm.addTransition(EntityStates.IDLING, Transitions.RANDOM, rtd, EntityStates.BASE_ATTACK);
		esm.addTransition(EntityStates.BASE_ATTACK, Transitions.TIME, new TimeTransitionData(0.1f), EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		return spawner;
	}
	
	public static Entity createWings(Entity owner, float x, float y, float xOff, float yOff, Animation flapping){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, flapping);
		Entity wings = new EntityBuilder("wings of " + Mappers.entity.get(owner).name, Mappers.type.get(owner).type)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
				.animation(animMap)
				.build();
		wings.add(engine.createComponent(PositionComponent.class).set(x, y));
		wings.add(engine.createComponent(StateComponent.class).set(EntityAnim.IDLE));
		wings.add(engine.createComponent(ParentComponent.class).set(owner));
		wings.add(engine.createComponent(OffsetComponent.class).set(xOff, yOff, true));
		wings.add(engine.createComponent(EffectComponent.class));
		return wings;
	}

	// ---------------------------------------------
	// -                   DROPS                   -
	// ---------------------------------------------
	public static Entity createCoin(float x, float y, float fx, float fy, int amount){
		Animation animation = null;
		CoinType coinType = CoinType.getCoin(amount);
		switch(coinType){
		case BLUE:
			animation = assets.getAnimation(Asset.COIN_BLUE);
			break;
		case GOLD:
			animation = assets.getAnimation(Asset.COIN_GOLD);
			break;
		case SILVER:
			animation = assets.getAnimation(Asset.COIN_SILVER);
			break;
		}
		Entity coin = createDrop(x, y, fx, fy, "coin.json", animation, assets.getAnimation(Asset.COIN_EXPLOSION), DropType.COIN);
		coin.add(engine.createComponent(MoneyComponent.class).set(amount));
		return coin;
	}
	
	private static Entity createDrop(float x, float y, float fx, float fy, String physicsBody, Animation dropIdle, Animation dropDisappear, DropType type){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.DROP_IDLE, dropIdle);
		animMap.put(EntityAnim.DROP_DISAPPEAR, dropDisappear);
		
		Entity drop = new EntityBuilder(type.name().toLowerCase(), EntityType.NEUTRAL)
				.animation(animMap)
				.physics(physicsBody, x, y, false)
				.render(dropIdle.getKeyFrame(0), false)
				.build();
		
		drop.add(engine.createComponent(ForceComponent.class).set(fx, fy));
		drop.add(engine.createComponent(DropComponent.class).set(type));

		drop.getComponent(DropComponent.class).canPickUp = false;
		drop.getComponent(TimerComponent.class).add("pickup_delay", 0.5f, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.drop.get(entity).canPickUp = true;
			}
		});
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Drop ESM", engine, drop).build();
		esm.createState(EntityStates.IDLING)
			.add(engine.createComponent(DropMovementComponent.class))
			.addAnimation(EntityAnim.DROP_IDLE);
		
		esm.createState(EntityStates.DYING)
			.add(engine.createComponent(BlinkComponent.class).addBlink(2.0f, 0.4f).addBlink(2.0f, 0.2f).addBlink(1.0f, 0.1f))
			.addAnimation(EntityAnim.DROP_IDLE)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					if(entity.getComponent(RenderComponent.class) == null){
						entity.add(new RenderComponent());
					}
				}
			});
		
		esm.createState(EntityStates.CLEAN_UP)
			.addAnimation(EntityAnim.DROP_DISAPPEAR)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
				}
				
				@Override
				public void onExit(State nextState, Entity entity) {
					entity.add(new RemoveComponent());
				}
			});
			
		TimeTransitionData timeToBlink = new TimeTransitionData(10.0f);
		TimeTransitionData timeToDisappear = new TimeTransitionData(5.0f);
		
		esm.addTransition(EntityStates.IDLING, Transitions.TIME, timeToBlink, EntityStates.DYING);
		esm.addTransition(EntityStates.DYING, Transitions.TIME, timeToDisappear, EntityStates.CLEAN_UP);
		esm.addTransition(EntityStates.CLEAN_UP, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		return drop;
	}
	
	// ---------------------------------------------
	// -                PROJECTILES                -
	// ---------------------------------------------
	public static Entity createBullet(float speed, float angle, float x, float y, float damage, boolean isArc, EntityType type) {
		return new ProjectileBuilder("bullet", type, x, y, speed, angle)
				.addDamage(damage)
				.makeArced(isArc)
				.build();
	}
	
	public static Entity createThrowingKnife(float x, float y, float speed, float angle, float damage, EntityType type){
		Entity knife = new ProjectileBuilder("knife", type, x, y, speed, angle)
				.addDamage(damage)
				.render(true)
				.animate(assets.getAnimation(Asset.ROGUE_THROWING_KNIFE))
				.build();
		return knife;
	}
	
	public static Entity createHomingKnife(Vector2 fromPos, Vector2 toPos, float time, float damage, EntityType type){
		Entity knife = new ProjectileBuilder("homing_knife", type, fromPos.x, fromPos.y, Vector2.dst(fromPos.x, fromPos.y, toPos.x, toPos.y) / time, MathUtils.radiansToDegrees * MathUtils.atan2(toPos.y - fromPos.y, toPos.x - fromPos.x))
				.addDamage(damage)
				.render(false)
				.animate(assets.getAnimation(Asset.ROGUE_THROWING_KNIFE))
				.build();
		knife.getComponent(StateComponent.class).time = MathUtils.random(8) * GameVars.ANIM_FRAME;

		CollisionData data = Mappers.collisionListener.get(knife).collisionData;
		FixtureInfo info = new FixtureInfo();
		
		CollisionFilter filter = new CollisionFilter.Builder()
				.addBodyTypes(TILE)
				.allEntityTypes()
				.build();
		
		info.addBehavior(filter, new DeathOnCollideBehavior());
		data.setFixtureInfo(FixtureType.BULLET, info);
		return knife;
	}
	
	public static Entity createBoomerang(Entity parent, float x, float y, float speed, float turnSpeed, float angle, float damage, EntityType type){
		Entity boomerang = new ProjectileBuilder("boomerang", type, "boomerang.json", x, y, speed, angle)
				.addDamage(damage)
				.render(true)
				.animate(assets.getAnimation(Asset.ROGUE_BOOMERANG_PROJECTILE))
				.controlledMovements(
						new BoomerangLineMovement(speed, angle),
						new BoomerangCurveMovement(parent, speed, turnSpeed, angle < 90),
						new BoomerangLineMovement(speed, 0.0f))
				.build();
		
		CollisionListenerComponent listenerComp = Mappers.collisionListener.get(boomerang);
		CollisionData data = listenerComp.collisionData;

		FixtureInfo info = new FixtureInfo();
		
		CollisionFilter filter = new CollisionFilter.Builder()
				.addBodyTypes(MOB)
				.addEntityTypes(type.getOpposite())
				.build();
		
		info.addBehavior(filter, new DamageOnCollideBehavior());
		
		filter = new CollisionFilter.Builder()
				.addCustomFilter(new PlayerFilter())
				.build();
		
		info.addBehavior(filter, new BoomerangBehavior());
	
		filter = new CollisionFilter.Builder()
				.addBodyTypes(TILE)
				.allEntityTypes()
				.build();
		
		info.addBehavior(filter, new SensorBehavior());
		data.setFixtureInfo(FixtureType.BULLET, info);
		
		return boomerang;
	}
	
	public static Entity createSpitProjectile(float speed, float angle, float x, float y, float damage, float airTime, EntityType type){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.PROJECTILE_INIT, assets.getAnimation(Asset.SPIT_INIT));
		animMap.put(EntityAnim.PROJECTILE_FLY, assets.getAnimation(Asset.SPIT_FLY));
		animMap.put(EntityAnim.PROJECTILE_DEATH, assets.getAnimation(Asset.SPIT_DEATH));
		
		Entity spit = new ProjectileBuilder("spit", type, x, y, speed, angle)
				.addDamage(damage)
				.render(true)
				.animate(
						animMap.get(EntityAnim.PROJECTILE_INIT), 
						animMap.get(EntityAnim.PROJECTILE_FLY), 
						animMap.get(EntityAnim.PROJECTILE_DEATH))
				.addTimedDeath(airTime)
				.addStateMachine()
				.build();
		
		return spit;
	}
	
	public static Entity createExplosiveProjectile(float speed, float angle, float x, float y, float damage, boolean isArc, EntityType type, String physicsBody, float radius, float damageDropOffRate, float knockback, Animation init, Animation fly, Animation death){
		// CLEANUP Generic explosive projectile uses all mana bomb stuff
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.PROJECTILE_INIT, init);
		animMap.put(EntityAnim.PROJECTILE_FLY, fly);
		animMap.put(EntityAnim.PROJECTILE_DEATH, death);
		
		Entity explosive = new ProjectileBuilder("explosive_projectile", type, physicsBody, x, y, speed, angle)
				.render(true)
				.makeArced(isArc)
				.makeExplosive(radius, speed, damage, damageDropOffRate, knockback)
				.animate(animMap.get(EntityAnim.PROJECTILE_INIT), animMap.get(EntityAnim.PROJECTILE_FLY), animMap.get(EntityAnim.PROJECTILE_DEATH))
				.addStateMachine()
				.addDamage(damage)
				.build();
		return explosive;
	}
	
	public static Entity createManaBomb(float x, float y, float angle, float damage, float knockback, EntityType type){
		return createExplosiveProjectile(10.0f, angle, x, y, damage, true, type, "mana_bomb.json", 5.0f, 0.0f, knockback,
				null, 
				new Animation(0.1f, assets.getAnimation(Asset.MANA_BOMB_EXPLOSION).getKeyFrames()[0]), 
				assets.getAnimation(Asset.MANA_BOMB_EXPLOSION));
	}
	
	public static Entity createSlingshotProjectile(float x, float y, float angle, float damage, float knockback, EntityType type){
		return createExplosiveProjectile(10.0f, angle, x, y, damage, true, type, "mana_bomb.json", 5.0f, 0.0f, knockback,
				null, 
				assets.getAnimation(Asset.ROGUE_DYNAMITE_PROJECTILE), 
				assets.getAnimation(Asset.SMOKE_BOMB));
	}
	
	public static Entity createExplosiveParticle(Entity parent, float speed, float angle, float x, float y){
		CombustibleComponent combustibleComp = Mappers.combustible.get(parent);
		Entity particle = new ProjectileBuilder("explosive_particle", Mappers.type.get(parent).type, "explosive_particle.json", x, y, speed, angle)
				.addTimedDeath(combustibleComp.radius / combustibleComp.speed)
				.build();
		particle.add(engine.createComponent(ParentComponent.class).set(parent));
		
		return particle;
	}
	
	private static class ProjectileBuilder {
		
		private Entity projectile;
		
		public ProjectileBuilder(String name, EntityType type, float x, float y, float speed, float angle){
			this(name, type, "bullet.json", x, y, speed, angle);
		}
		
		public ProjectileBuilder(String name, EntityType type, String physics, float x, float y, float speed, float angle){
			projectile = new EntityBuilder(name, type)
					.physics(physics, new BodyProperties.Builder().setGravityScale(0.0f).build(), x, y, false)
					.build();
			projectile.add(engine.createComponent(ProjectileComponent.class).set(x, y, speed, angle, false));
			projectile.add(engine.createComponent(TypeComponent.class).set(type).setCollideWith(type.getOpposite()));
			projectile.add(engine.createComponent(ForceComponent.class).set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle)));
		}
		
		public ProjectileBuilder addDamage(float damage){
			projectile.add(engine.createComponent(DamageComponent.class).set(damage));
			return this;
		}
		
		/**
		 * Use this method for easy chaining. If arced is false, nothing happens. If arced is true, makeArced() is called.
		 */
		public ProjectileBuilder makeArced(boolean arced){
			if(!arced) return this;
			return makeArced();
		}
		
		public ProjectileBuilder makeArced(){
			Mappers.projectile.get(projectile).isArc = true;
			Mappers.body.get(projectile).body.setGravityScale(1.0f);
			return this;
		}
		
		public ProjectileBuilder makeExplosive(final float radius, float speed, final float damage, float damageDropOffRate, final float knockback){
			CollisionListenerComponent listenerComp = Mappers.collisionListener.get(projectile);
			CollisionData data = listenerComp.collisionData;
			
			FixtureInfo info = data.getFixtureInfo(FixtureType.BULLET);
			
			CollisionFilter filter = new CollisionFilter.Builder()
					.addBodyTypes(CollisionBodyType.MOB, CollisionBodyType.TILE)
					.allEntityTypes()
					.removeEntityType(Mappers.type.get(projectile).type)
					.build();
			
			info.addBehavior(filter, new SpawnExplosionBehavior(radius, damage, knockback));
			return this;
		}
		
		public ProjectileBuilder render(boolean facing){
			return render(null, facing);
		}
		
		public ProjectileBuilder render(TextureRegion frame, boolean facing){
			projectile = new EntityBuilder(projectile).render(frame, facing).build();
			return this;
		}
		
		public ProjectileBuilder animate(Animation fly){
			ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
			animMap.put(EntityAnim.PROJECTILE_FLY, fly);
			
			fly.setFrameDuration(GameVars.ANIM_FRAME * 0.5f);
			
			projectile.add(engine.createComponent(StateComponent.class).set(EntityAnim.PROJECTILE_FLY));
			projectile = new EntityBuilder(projectile).animation(animMap).build();
			return this;
		}
		
		public ProjectileBuilder animate(Animation init, Animation fly, Animation death){
			if(fly == null) throw new IllegalArgumentException("Flying animation can't be null.");
			ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
			if(init != null) animMap.put(EntityAnim.PROJECTILE_INIT, init);
			if(death != null) animMap.put(EntityAnim.PROJECTILE_DEATH, death);
			
			animMap.put(EntityAnim.PROJECTILE_FLY, fly);
			fly.setFrameDuration(GameVars.ANIM_FRAME * 0.5f);
			
			projectile = new EntityBuilder(projectile).animation(animMap).build();
			return this;
		}
		
		public ProjectileBuilder addStateMachine(){
			if(Mappers.animation.get(projectile) == null) throw new RuntimeException("Make sure you give the projectile animation capabilities before adding a state machine.");
			EntityStateMachine esm = new StateFactory.EntityStateBuilder(Mappers.entity.get(projectile).name + " ESM", engine, projectile).build();
			
			ArrayMap<State, Animation> animMap = Mappers.animation.get(projectile).animations;
			if(animMap.containsKey(EntityAnim.PROJECTILE_INIT)){
				esm.createState(EntityStates.PROJECTILE_INIT)
					.addAnimation(EntityAnim.PROJECTILE_INIT);
				esm.addTransition(EntityStates.PROJECTILE_INIT, Transitions.ANIMATION_FINISHED, EntityStates.PROJECTILE_FLY);
			}
			
			if(animMap.containsKey(EntityAnim.PROJECTILE_DEATH)){
				esm.createState(EntityStates.PROJECTILE_DEATH)
					.addAnimation(EntityAnim.PROJECTILE_DEATH)
					.addChangeListener(new StateChangeListener() {
						@Override
						public void onExit(State nextState, Entity entity) {
							entity.add(new RemoveComponent());
						}
						
						@Override
						public void onEnter(State prevState, Entity entity) {
							
						}
					});
				esm.addTransition(EntityStates.PROJECTILE_DEATH, Transitions.ANIMATION_FINISHED, EntityStates.PROJECTILE_FLY);
				
				// Add a death behavior to stop bullet collision and change the state
				Mappers.death.get(projectile).add(new DeathBehavior() {
					@Override
					public void onDeath(Entity entity) {
						Mappers.body.get(entity).body.setActive(false);
						Mappers.esm.get(entity).get(EntityStates.PROJECTILE_DEATH).changeState(EntityStates.PROJECTILE_DEATH);
					}
				});
			}
			esm.createState(EntityStates.PROJECTILE_FLY)
				.addAnimation(EntityAnim.PROJECTILE_FLY);
			
			if(esm.hasState(EntityStates.PROJECTILE_INIT)) {
				esm.changeState(EntityStates.PROJECTILE_INIT);
			} else{
				esm.changeState(EntityStates.PROJECTILE_FLY);
			}
			return this;
		}
		
		public ProjectileBuilder addTimedDeath(float time){
			Mappers.timer.get(projectile).add("projectile_life", time, false, new TimeListener() {
				@Override
				public void onTime(Entity entity) {
					Mappers.death.get(entity).triggerDeath();
				}
			});
			return this;
		}
		
		public ProjectileBuilder controlledMovement(Movement movement){
			projectile.remove(ForceComponent.class);
			projectile.add(engine.createComponent(ControlledMovementComponent.class).set(movement));
			return this;
		}
		
		public ProjectileBuilder controlledMovements(Movement... movements){
			projectile.remove(ForceComponent.class);
			ControlledMovementComponent comp = EntityUtils.add(projectile, ControlledMovementComponent.class);
			comp.addAll(movements);
			return this;
		}
		
		public ProjectileBuilder makeRotatable(){
			Mappers.rotation.get(projectile).automatic = true;
			return this;
		}
		
		public ProjectileBuilder disableTileCollisions(){
			CollisionData data = Mappers.collisionListener.get(projectile).collisionData;
			FixtureInfo info = data.getFixtureInfo(FixtureType.BULLET);
			info.addBehavior(new CollisionFilter.Builder().addBodyTypes(TILE).allEntityTypes().build(), new SensorBehavior());
			return this;
		}
		
		public Entity build(){
			return projectile;
		}
	}
	
	// ---------------------------------------------
	// -                EXPLOSIVES                 -
	// ---------------------------------------------
	public static Entity createExplosion(float x, float y, float radius, float damage, float knockback, EntityType type){
		final float SPEED = 15.0f;
		
		Entity explosion = new EntityBuilder("explosion", type).build();
		explosion.add(engine.createComponent(PositionComponent.class).set(x, y));
		EntityUtils.add(explosion, CombustibleComponent.class).set(radius, SPEED, damage, 5.0f, knockback).shouldExplode = true; // HACK speed and drop off is hardcoded
		explosion.add(engine.createComponent(TypeComponent.class).set(type).setCollideWith(type.getOpposite()));
		
		// Setup timed death after explosive particles are dead
		Mappers.timer.get(explosion).add("death", radius / SPEED, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.death.get(entity).triggerDeath();
			}
		});
		
		return explosion;
	}
	
	public static Entity createSmoke(float x, float y, Animation smokeAnimation){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, smokeAnimation);
		
		Entity smoke = new EntityBuilder("smoke", EntityType.NEUTRAL)
				.render(false)
				.animation(animMap)
				.build();
		smoke.add(engine.createComponent(StateComponent.class).set(EntityAnim.IDLE));
		smoke.add(engine.createComponent(PositionComponent.class).set(x, y));
		
		Mappers.timer.get(smoke).add("death", smokeAnimation.getAnimationDuration(), false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.death.get(entity).triggerDeath();
			}
		});
		return smoke;	
	}
	
	// ----------------------------------------------
	// -                DAMAGE TEXT                 -
	// ----------------------------------------------
	public static Entity createDamageText(String text, Color color, BitmapFont font, float x, float y, float speed){
		Entity entity = new EntityBuilder("damage text", EntityType.NEUTRAL).build();
		entity.add(engine.createComponent(TextRenderComponent.class).set(font, color, text));
		entity.add(engine.createComponent(PositionComponent.class).set(x, y));
		entity.add(engine.createComponent(VelocityComponent.class).set(0, speed));
		entity.getComponent(TimerComponent.class).add("text_life", 0.4f, false, new TimeListener(){
			@Override
			public void onTime(Entity entity) {
				entity.add(new RemoveComponent());
			}
		});
		return entity;
	}
	
	// ---------------------------------------------
	// -                PARTICLES                  -
	// ---------------------------------------------
	public static Entity createParticle(Animation animation, float x, float y){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.JUMP, animation);
		
		Entity entity = new EntityBuilder("particle", EntityType.NEUTRAL)
				.render(animation.getKeyFrame(0.0f), false)
				.animation(animMap)
				.build();
		entity.add(engine.createComponent(StateComponent.class).set(EntityAnim.JUMP));
		entity.add(engine.createComponent(PositionComponent.class).set(x, y));
		
		Mappers.timer.get(entity).add("particle_life", animation.getAnimationDuration(), false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.death.get(entity).triggerDeath();
			}
		});
		
		return entity;
	}
	
	// ---------------------------------------------
	// -                   CAMERA                  -
	// ---------------------------------------------
	public static Entity createCamera(OrthographicCamera worldCamera){
		Entity camera = new EntityBuilder("camera", EntityType.NEUTRAL).build();
		CameraComponent cameraComp = engine.createComponent(CameraComponent.class);
		cameraComp.locked = true;
		cameraComp.camera = worldCamera;
		cameraComp.x = worldCamera.position.x;
		cameraComp.y = worldCamera.position.y;
		cameraComp.minX = 0f;
		cameraComp.minY = 0f;
		cameraComp.windowMinX = -2f;
		cameraComp.windowMinY = 0f;
		cameraComp.windowMaxX = 2f;
		cameraComp.windowMaxY = 0f;
		cameraComp.zoom = 3.0f;
		camera.add(cameraComp);
		return camera;
	}
	
	// ---------------------------------------------
	// -                   TILES                   -
	// ---------------------------------------------
	public static Entity createTile(Body body){
		Entity tile = new EntityBuilder("tile", EntityType.NEUTRAL).build();
		tile.add(engine.createComponent(BodyComponent.class).set(body));
		return tile;
	}

	public static class EntityBuilder{
		private Entity entity;
		
		/**
		 * Setups base entity with Engine, World and Level components.
		 * @param engine
		 * @param world
		 * @param level
		 */
		public EntityBuilder(String name, EntityType type){
			entity = engine.createEntity();
			EntityUtils.setTargetable(entity, true);
			entity.add(engine.createComponent(EntityComponent.class).set(name));
			entity.add(engine.createComponent(EngineComponent.class).set(engine));
			entity.add(engine.createComponent(WorldComponent.class).set(world));
			entity.add(engine.createComponent(LevelComponent.class).set(level, entity));
			entity.add(engine.createComponent(TimerComponent.class));
			entity.add(engine.createComponent(DeathComponent.class));		
			entity.add(engine.createComponent(TypeComponent.class).set(type));
		}
		
		/**
		 * Use to decorate existing entities with new components
		 * @param entity
		 */
		public EntityBuilder(Entity entity){
			this.entity = entity;
		}
		
		/**
		 * Adds Render, Texture, Shader and optional Facing components.
		 * 
		 * @param frame
		 * @param facing
		 * @return
		 */
		public EntityBuilder render(TextureRegion frame, boolean facing, Shader shader, int renderLevel){ 
			entity.add(engine.createComponent(RotationComponent.class));
			entity.add(engine.createComponent(RenderComponent.class));
			entity.add(engine.createComponent(RenderLevelComponent.class).set(renderLevel));
			entity.add(engine.createComponent(TextureComponent.class).set(frame));
			if(facing) entity.add(engine.createComponent(FacingComponent.class));
			entity.add(engine.createComponent(ShaderComponent.class).set(shader));
			return this;
		}
		
		/**
		 * Adds Render, Texture and optional Facing components.
		 * 
		 * @param frame
		 * @param facing
		 * @return
		 */
		public EntityBuilder render(TextureRegion frame, boolean facing){ 
			return render(frame, facing, null, RenderLevel.ENTITY);
		}
		
		/**
		 * Adds Render, Texture and optional Facing components.
		 * 
		 * @param frame
		 * @param facing
		 * @return
		 */
		public EntityBuilder render(boolean facing){ 
			return render(null, facing);
		}
		
		/**
		 * Adds Animation component.
		 * @param animMap
		 * @return
		 */
		public EntityBuilder animation(ArrayMap<State, Animation> animMap){
			entity.add(engine.createComponent(AnimationComponent.class));
			entity.getComponent(AnimationComponent.class).animations.putAll(animMap);
			return this;
		}
		
		/**
		 * Adds Body, Position, Velocity and optional Collision components.
		 * 
		 * @param body
		 * @param x
		 * @param y
		 * @param collideable
		 * @return
		 */
		public EntityBuilder physics(String physicsBody, BodyProperties properties, float x, float y, boolean collideable){
			entity.add(engine.createComponent(BodyComponent.class)
					.set(PhysicsUtils.createPhysicsBody(Gdx.files.internal("body/" + physicsBody), world, new Vector2(x, y), entity, properties)));
			entity.add(engine.createComponent(PositionComponent.class).set(x, y));
			entity.add(engine.createComponent(VelocityComponent.class));
			if(collideable) entity.add(engine.createComponent(CollisionComponent.class));
			return this;
		}
		
		/**
		 * Adds Body, Position, Velocity and optional Collision components. 
		 * 
		 * @param physicsBody
		 * @param x
		 * @param y
		 * @param collideable
		 * @return
		 */
		public EntityBuilder physics(String physicsBody, float x, float y, boolean collideable){
			return physics(physicsBody, null, x, y, collideable);
		}
		
		/**
		 * Adds Input, Health, Effect and Invincibility components.
		 * 
		 * @param input
		 * @param type
		 * @param health
		 * @return
		 */ 
		public EntityBuilder mob(Input input, float health){
			entity.add(engine.createComponent(InputComponent.class).set(input));
			entity.add(engine.createComponent(HealthComponent.class).set(health, health));
			entity.add(engine.createComponent(InvincibilityComponent.class));
			entity.add(engine.createComponent(EffectComponent.class));
			entity.add(engine.createComponent(ImmuneComponent.class));
			return this;
		}
		
		public Entity build(){
			return entity;
		}
	}
}