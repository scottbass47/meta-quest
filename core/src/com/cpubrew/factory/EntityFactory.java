package com.cpubrew.factory;

import static com.cpubrew.entity.EntityStatus.ENEMY;
import static com.cpubrew.entity.EntityStatus.FRIENDLY;
import static com.cpubrew.entity.EntityStatus.NEUTRAL;
import static com.cpubrew.entity.EntityType.ARROW;
import static com.cpubrew.entity.EntityType.BALLOON_PELLET;
import static com.cpubrew.entity.EntityType.BALLOON_TRAP;
import static com.cpubrew.entity.EntityType.BASE_TILE;
import static com.cpubrew.entity.EntityType.BOOMERANG;
import static com.cpubrew.entity.EntityType.BULLET;
import static com.cpubrew.entity.EntityType.CAMERA;
import static com.cpubrew.entity.EntityType.COIN;
import static com.cpubrew.entity.EntityType.DAMAGE_TEXT;
import static com.cpubrew.entity.EntityType.DYNAMITE;
import static com.cpubrew.entity.EntityType.EXPLOSION;
import static com.cpubrew.entity.EntityType.EXPLOSIVE_PARTICLE;
import static com.cpubrew.entity.EntityType.GOAT;
import static com.cpubrew.entity.EntityType.HOMING_KNIFE;
import static com.cpubrew.entity.EntityType.KNIGHT;
import static com.cpubrew.entity.EntityType.LEVEL_TRIGGER;
import static com.cpubrew.entity.EntityType.MANA_BOMB;
import static com.cpubrew.entity.EntityType.MONK;
import static com.cpubrew.entity.EntityType.PARTICLE;
import static com.cpubrew.entity.EntityType.ROGUE;
import static com.cpubrew.entity.EntityType.SMOKE;
import static com.cpubrew.entity.EntityType.SPAWNER;
import static com.cpubrew.entity.EntityType.THROWING_KNIFE;
import static com.cpubrew.entity.EntityType.WINGS;
import static com.cpubrew.physics.collision.CollisionBodyType.MOB;
import static com.cpubrew.physics.collision.CollisionBodyType.PROJECTILE;
import static com.cpubrew.physics.collision.CollisionBodyType.TILE;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.branch.Selector;
import com.badlogic.gdx.ai.btree.branch.Sequence;
import com.badlogic.gdx.ai.btree.decorator.AlwaysSucceed;
import com.badlogic.gdx.ai.btree.leaf.Wait;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ArrayMap;
import com.cpubrew.ability.knight.AntiMagneticAbility;
import com.cpubrew.ability.knight.BlacksmithAbility;
import com.cpubrew.ability.knight.DashSlashAbility;
import com.cpubrew.ability.knight.KickAbility;
import com.cpubrew.ability.knight.OverheadSwingAbility;
import com.cpubrew.ability.knight.ParryAbility;
import com.cpubrew.ability.knight.SlamAbility;
import com.cpubrew.ability.knight.SpinSliceAbility;
import com.cpubrew.ability.knight.TornadoAbility;
import com.cpubrew.ability.monk.InstaWallAbility;
import com.cpubrew.ability.monk.PoisonDebuffAbility;
import com.cpubrew.ability.monk.WindBurstAbility;
import com.cpubrew.ability.rogue.BalloonTrapAbility;
import com.cpubrew.ability.rogue.BoomerangAbility;
import com.cpubrew.ability.rogue.BowAbility;
import com.cpubrew.ability.rogue.DashAbility;
import com.cpubrew.ability.rogue.DynamiteAbility;
import com.cpubrew.ability.rogue.ExecuteAbility;
import com.cpubrew.ability.rogue.FlashPowderAbility;
import com.cpubrew.ability.rogue.HomingKnivesAbility;
import com.cpubrew.ability.rogue.VanishAbility;
import com.cpubrew.ai.AIController;
import com.cpubrew.ai.PathFinder;
import com.cpubrew.ai.tasks.AttackTask;
import com.cpubrew.ai.tasks.CalculatePathTask;
import com.cpubrew.ai.tasks.FlyTask;
import com.cpubrew.ai.tasks.FollowPathTask;
import com.cpubrew.ai.tasks.InLoSTask;
import com.cpubrew.ai.tasks.InRangeTask;
import com.cpubrew.ai.tasks.InStateTask;
import com.cpubrew.ai.tasks.OnGroundTask;
import com.cpubrew.ai.tasks.OnTileTask;
import com.cpubrew.ai.tasks.ReleaseControlsTask;
import com.cpubrew.ai.tasks.TargetOnPlatformTask;
import com.cpubrew.ai.tasks.InRangeTask.RangeTest;
import com.cpubrew.ai.tasks.InStateTask.SMType;
import com.cpubrew.assets.Asset;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.audio.AudioLocator;
import com.cpubrew.audio.Sounds;
import com.cpubrew.component.AIControllerComponent;
import com.cpubrew.component.ASMComponent;
import com.cpubrew.component.AbilityComponent;
import com.cpubrew.component.AnimationComponent;
import com.cpubrew.component.BTComponent;
import com.cpubrew.component.BarrierComponent;
import com.cpubrew.component.BlinkComponent;
import com.cpubrew.component.BobComponent;
import com.cpubrew.component.BodyComponent;
import com.cpubrew.component.CameraComponent;
import com.cpubrew.component.ChildrenComponent;
import com.cpubrew.component.CollisionComponent;
import com.cpubrew.component.CollisionListenerComponent;
import com.cpubrew.component.CombustibleComponent;
import com.cpubrew.component.ControlledMovementComponent;
import com.cpubrew.component.DamageComponent;
import com.cpubrew.component.DeathComponent;
import com.cpubrew.component.DirectionComponent;
import com.cpubrew.component.DropComponent;
import com.cpubrew.component.DropMovementComponent;
import com.cpubrew.component.ESMComponent;
import com.cpubrew.component.EffectComponent;
import com.cpubrew.component.EngineComponent;
import com.cpubrew.component.EntityComponent;
import com.cpubrew.component.FacingComponent;
import com.cpubrew.component.ForceComponent;
import com.cpubrew.component.FrameMovementComponent;
import com.cpubrew.component.GroundMovementComponent;
import com.cpubrew.component.HealthComponent;
import com.cpubrew.component.ImmuneComponent;
import com.cpubrew.component.InputComponent;
import com.cpubrew.component.InvincibilityComponent;
import com.cpubrew.component.JumpComponent;
import com.cpubrew.component.KnightComponent;
import com.cpubrew.component.LevelComponent;
import com.cpubrew.component.LevelSwitchComponent;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.MoneyComponent;
import com.cpubrew.component.MonkComponent;
import com.cpubrew.component.NoMovementComponent;
import com.cpubrew.component.OffsetComponent;
import com.cpubrew.component.ParentComponent;
import com.cpubrew.component.PathComponent;
import com.cpubrew.component.PlayerComponent;
import com.cpubrew.component.PositionComponent;
import com.cpubrew.component.ProjectileComponent;
import com.cpubrew.component.PropertyComponent;
import com.cpubrew.component.RemoveComponent;
import com.cpubrew.component.RenderComponent;
import com.cpubrew.component.RenderLevelComponent;
import com.cpubrew.component.RogueComponent;
import com.cpubrew.component.RotationComponent;
import com.cpubrew.component.ShaderComponent;
import com.cpubrew.component.SpawnComponent;
import com.cpubrew.component.SpawnerPoolComponent;
import com.cpubrew.component.SpeedComponent;
import com.cpubrew.component.StateComponent;
import com.cpubrew.component.StatusComponent;
import com.cpubrew.component.SwingComponent;
import com.cpubrew.component.TargetComponent;
import com.cpubrew.component.TextRenderComponent;
import com.cpubrew.component.TextureComponent;
import com.cpubrew.component.TimeListener;
import com.cpubrew.component.TimerComponent;
import com.cpubrew.component.VelocityComponent;
import com.cpubrew.component.WeightComponent;
import com.cpubrew.component.WorldComponent;
import com.cpubrew.component.DeathComponent.DeathBehavior;
import com.cpubrew.component.DirectionComponent.Direction;
import com.cpubrew.component.InvincibilityComponent.InvincibilityType;
import com.cpubrew.component.TargetComponent.TargetBehavior;
import com.cpubrew.debug.DebugRender;
import com.cpubrew.debug.DebugVars;
import com.cpubrew.effects.EffectDef;
import com.cpubrew.effects.EffectType;
import com.cpubrew.effects.KnockBackDef;
import com.cpubrew.entity.CoinType;
import com.cpubrew.entity.DelayedAction;
import com.cpubrew.entity.DropType;
import com.cpubrew.entity.EntityAnim;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.entity.EntityLoader;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.entity.EntityStates;
import com.cpubrew.entity.EntityStats;
import com.cpubrew.entity.EntityStatus;
import com.cpubrew.entity.EntityType;
import com.cpubrew.factory.ProjectileFactory.ProjectileData;
import com.cpubrew.fsm.AnimationStateMachine;
import com.cpubrew.fsm.EntityState;
import com.cpubrew.fsm.EntityStateMachine;
import com.cpubrew.fsm.GlobalChangeListener;
import com.cpubrew.fsm.MultiTransition;
import com.cpubrew.fsm.State;
import com.cpubrew.fsm.StateChangeListener;
import com.cpubrew.fsm.StateChangeResolver;
import com.cpubrew.fsm.StateMachine;
import com.cpubrew.fsm.StateObject;
import com.cpubrew.fsm.StateObjectCreator;
import com.cpubrew.fsm.transition.CollisionTransitionData;
import com.cpubrew.fsm.transition.InputTransitionData;
import com.cpubrew.fsm.transition.RandomTransitionData;
import com.cpubrew.fsm.transition.TimeTransitionData;
import com.cpubrew.fsm.transition.Transition;
import com.cpubrew.fsm.transition.TransitionObject;
import com.cpubrew.fsm.transition.TransitionTag;
import com.cpubrew.fsm.transition.Transitions;
import com.cpubrew.fsm.transition.CollisionTransitionData.CollisionType;
import com.cpubrew.fsm.transition.InputTransitionData.Type;
import com.cpubrew.game.GameVars;
import com.cpubrew.input.Actions;
import com.cpubrew.input.Input;
import com.cpubrew.level.Level;
import com.cpubrew.level.NavMesh;
import com.cpubrew.movement.BoomerangCurveMovement;
import com.cpubrew.movement.BoomerangLineMovement;
import com.cpubrew.movement.Movement;
import com.cpubrew.physics.BodyBuilder;
import com.cpubrew.physics.BodyProperties;
import com.cpubrew.physics.FixtureType;
import com.cpubrew.physics.collision.CollisionBodyType;
import com.cpubrew.physics.collision.CollisionData;
import com.cpubrew.physics.collision.FixtureInfo;
import com.cpubrew.physics.collision.behavior.BalloonTrapBehavior;
import com.cpubrew.physics.collision.behavior.BoomerangBehavior;
import com.cpubrew.physics.collision.behavior.DamageOnCollideBehavior;
import com.cpubrew.physics.collision.behavior.DeathOnCollideBehavior;
import com.cpubrew.physics.collision.behavior.SensorBehavior;
import com.cpubrew.physics.collision.behavior.SpawnExplosionBehavior;
import com.cpubrew.physics.collision.filter.CollisionFilter;
import com.cpubrew.physics.collision.filter.PlayerFilter;
import com.cpubrew.render.RenderLevel;
import com.cpubrew.shader.Shader;
import com.cpubrew.utils.EntityUtils;
import com.cpubrew.utils.Maths;
import com.cpubrew.utils.PhysicsUtils;
import com.cpubrew.utils.RenderUtils;

public class EntityFactory {

	private static AssetLoader assets = AssetLoader.getInstance();
	public static Engine engine;
	public static World world;
	public static Level level;
	public static int ID;
	
	// -------------
	// Bugs / Todos
	// -------------
	
	// Entity
	// TODO Make muzzle flash a separate particle
	// TODO Refactor shader system to handle layering effects / priorities
	// TODO Make time underground +/- random amount 
	// BUG Rocky sometimes gets stuck in a loop swinging (can't replicate)
	// BUG Auto-saving iterator() nested
	// BUG DamageOnCollide should do a preSolve check
	// BUG Small glitch in bird ai when entering range for swoop attack
	// BUG Drill gremlin doesn't stay restricted to platform
	
	// ------------
	// Optimization
	// ------------
	
	// Physics
	// PERFORMANCE Add in Box2d filtering system
	
	private EntityFactory(){
	}
	
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
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.KNIGHT_RUN));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.KNIGHT_FALL));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.KNIGHT_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getAnimation(Asset.KNIGHT_IDLE));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_1, assets.getAnimation(Asset.KNIGHT_CHAIN1_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_2, assets.getAnimation(Asset.KNIGHT_CHAIN2_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_3, assets.getAnimation(Asset.KNIGHT_CHAIN3_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_4, assets.getAnimation(Asset.KNIGHT_CHAIN4_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_1, assets.getAnimation(Asset.KNIGHT_CHAIN1_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_2, assets.getAnimation(Asset.KNIGHT_CHAIN2_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_3, assets.getAnimation(Asset.KNIGHT_CHAIN3_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_4, assets.getAnimation(Asset.KNIGHT_CHAIN4_ANTICIPATION));
		animMap.put(EntityAnim.SWING, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
//		animMap.put(EntityAnim.SWING_2, assets.getAnimation(Asset.KNIGHT_CHAIN2_SWING));
//		animMap.put(EntityAnim.SWING_3, assets.getAnimation(Asset.KNIGHT_CHAIN3_SWING));
//		animMap.put(EntityAnim.SWING_4, assets.getAnimation(Asset.KNIGHT_CHAIN4_SWING));
		animMap.put(EntityAnim.PARRY_BLOCK, assets.getAnimation(Asset.KNIGHT_PARRY_BLOCK));
		animMap.put(EntityAnim.PARRY_SWING, assets.getAnimation(Asset.KNIGHT_PARRY_SWING));
		animMap.put(EntityAnim.KICK, assets.getAnimation(Asset.KNIGHT_KICK));
		animMap.put(EntityAnim.OVERHEAD_SWING, assets.getAnimation(Asset.KNIGHT_OVERHEAD_SWING));
		animMap.put(EntityAnim.SLAM, assets.getAnimation(Asset.KNIGHT_SLAM));
		animMap.put(EntityAnim.DASH, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.SPIN_SLICE, assets.getAnimation(Asset.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.TORNADO_INIT, assets.getAnimation(Asset.KNIGHT_TORNADO_INIT));
		animMap.put(EntityAnim.TORNADO_SWING, assets.getAnimation(Asset.KNIGHT_TORNADO_SWING));
		animMap.put(EntityAnim.ROLL, assets.getAnimation(Asset.KNIGHT_ROLL));
		
		Entity knight = new EntityBuilder(KNIGHT, FRIENDLY)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, knightStats.get("health"), knightStats.get("weight"))
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
					.set(3.0f, 2.0f, 90, -135, 4 * GameVars.ANIM_FRAME, 0.0f, knightStats.get("parry_knockback")).breakProjectiles());
		
		KickAbility kickAbility = new KickAbility(
				knightStats.get("kick_cooldown"), 
				Actions.ABILITY_2,
				3 * GameVars.ANIM_FRAME, 
				knightStats.get("kick_range"), 
				knightStats.get("kick_knockback"), 
				knightStats.get("kick_damage"),
				animMap.get(EntityAnim.KICK));
		
		OverheadSwingAbility overheadSwingAbility = new OverheadSwingAbility(
				knightStats.get("overhead_swing_cooldown"),
				Actions.ABILITY_3, 
				animMap.get(EntityAnim.OVERHEAD_SWING),
				engine.createComponent(SwingComponent.class).set(
						knightStats.get("overhead_swing_rx"), 
						knightStats.get("overhead_swing_ry"), 
						120.0f, 
						-40.0f, 
						5 * GameVars.ANIM_FRAME,
						knightStats.get("overhead_swing_damage"),
						knightStats.get("overhead_swing_knockback")));
		
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
		dashSlashAbility.deactivate();
		
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
		spinSliceAbility.deactivate();
		
		TornadoAbility tornadoAbility = new TornadoAbility(
				knightStats.get("tornado_cooldown"),
				Actions.ABILITY_3, 
				knightStats.get("tornado_duration"),
				knightStats.get("tornado_damage"),
				knightStats.get("tornado_knockback"), 
				knightStats.get("tornado_range"),
				5);
		tornadoAbility.deactivate();
		
		// Player Related Components
		knight.getComponent(ImmuneComponent.class).add(EffectType.STUN);
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
//		SwingComponent swing2 = engine.createComponent(SwingComponent.class).set(1.75f, 0.75f, 150.0f, -180.0f, 0.0f, swordDamage, knockback);
//		SwingComponent swing3 = engine.createComponent(SwingComponent.class).set(1.75f, 1.25f, 120.0f, -120.0f, 0.0f, swordDamage, knockback);
//		SwingComponent swing4 = engine.createComponent(SwingComponent.class).set(1.75f, 1.0f, 135.0f, -120.0f, 0.0f, swordDamage, knockback);
//		
//		// Setup attacks
//		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_1, EntityAnim.SWING_ANTICIPATION_1, EntityAnim.SWING_1, swing1);
//		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_2, EntityAnim.SWING_ANTICIPATION_2, EntityAnim.SWING_2, swing2);
//		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_3, EntityAnim.SWING_ANTICIPATION_3, EntityAnim.SWING_3, swing3);
//		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_4, EntityAnim.SWING_ANTICIPATION_4, EntityAnim.SWING_4, swing4);
		
		knight.add(knightComp);
		
//		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Knight ESM", engine, knight)
//			.idle()
//			.run(knightStats.get("ground_speed"))
//			.jump(knightStats.get("jump_force"), knightStats.get("jump_float_amount"), knightStats.get("air_speed"), true, true)
//			.fall(knightStats.get("air_speed"), true)
//			.climb(5.0f)
//			.build();
		
		EntityStateMachine esm = StateFactory.createBaseBipedal(knight, knightStats);

		// Notes
		//	- If transitioning from running, jumping, or falling state skip initial swing animation
		//	- Move forwards slight amount (more if you're coming from running state)
		//	- After swing, move towards anticipation state
		//	- Animations are chosen randomly but NOT repeated when chaining
		//	- Swing animations and anticipation frames have to match up
		//	- Swing always goes to anticipation, which then either goes back to swing or to idle
		
		// Unpack animations
//		Array<EntityAnim> idleToSwingAnims = new Array<EntityAnim>();
//		Array<EntityAnim> anticipationAnims = new Array<EntityAnim>();
//		Array<EntityAnim> swingAnims = new Array<EntityAnim>();
//
//		for(KnightAttack attack : Mappers.knight.get(knight).getAttacks()){
//			idleToSwingAnims.add(attack.getIdleAnticipation());
//			anticipationAnims.add(attack.getAnticipationAnimation());
//			swingAnims.add(attack.getSwingAnimation());
//		}
//		
//		// INCOMPLETE Damage modifier when continuously chaining
//		esm.createState(EntityStates.IDLE_TO_SWING)
//			.addAnimations(idleToSwingAnims)
//			.addTag(TransitionTag.STATIC_STATE)
//			.addChangeListener(new StateChangeListener() {
//				@Override
//				public void onEnter(State prevState, Entity entity) {
//					KnightComponent knightComp = Mappers.knight.get(entity);
//					int random = MathUtils.random(knightComp.numAttacks() - 1);
//					knightComp.index = random;
//					knightComp.first = false;
//					
//					// Switch to proper animation
//					// CLEANUP GROSS!!! 
//					Mappers.timer.get(entity).add("anim_timer", 0.0f, false, new TimeListener() {
//						@Override
//						public void onTime(Entity entity) {
//							ESMComponent esmComp = Mappers.esm.get(entity);
//							AnimationStateMachine asm = esmComp.first().getCurrentStateObject().getASM();
//							asm.changeState(Mappers.knight.get(entity).getCurrentAttack().getIdleAnticipation());
//						}
//					});
//					
//					// Lock Abilities 
//					Mappers.ability.get(entity).lockAllBlocking();
//				}
//
//				@Override
//				public void onExit(State nextState, Entity entity) {
//					
//				}
//				
//			});
//		
//		esm.createState(EntityStates.SWING_ATTACK)
//			.addAnimations(swingAnims) // CLEANUP See entity state.
//			.addTag(TransitionTag.STATIC_STATE)
//			.addChangeListener(new StateChangeListener() {
//				@Override
//				public void onEnter(State prevState, Entity entity) {
//					KnightComponent knightComp = Mappers.knight.get(entity);
//					BodyComponent bodyComp = Mappers.body.get(entity);
//					Body body = bodyComp.body;
//					FacingComponent facingComp = Mappers.facing.get(entity);
//					
//					// Remove gravity and make the player a bullet
//					body.setGravityScale(0.0f);
//					body.setBullet(true);
//					
//					float duration = GameVars.ANIM_FRAME;
//					float distance = 0.5f;
//					boolean first = knightComp.first;
//
//					// Not in a combo yet, pick random attack
//					if(first){
//						int random = MathUtils.random(knightComp.numAttacks() - 1);
//						knightComp.index = random;
//						knightComp.first = false;
//						
//						// Slightly larger distance traveled if you're coming from a running state
//						if(prevState == EntityStates.RUNNING){
//							distance = 0.75f;
//						}
//					}
//					
//					distance *= facingComp.facingRight ? 1 : -1;
//					
//					if(prevState != EntityStates.IDLE_TO_SWING){
//						knightComp.index++;
//						if(knightComp.index >= knightComp.numAttacks()) knightComp.index = 0;
//					}
//					
//					// Apply force
//					if(first || prevState == EntityStates.IDLE_TO_SWING){
//						entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class).set(distance / duration, 0.0f));
//					}
//					else{
//						float toX = knightComp.lungeX;
//						float toY = knightComp.lungeY;
//						
//						float fromX = body.getPosition().x;
//						float fromY = body.getPosition().y;
//						
//						float distanceX = toX - fromX;
//						float distanceY = toY - fromY;
//						
//						entity.add(Mappers.engine.get(entity).engine.createComponent(ForceComponent.class).set(distanceX / duration, distanceY / duration));
//					}
//
//					// CLEANUP VERY FRAGILE... This only works because we know the state machine system updates before the timer and force system
//					Mappers.timer.get(entity).add("lunge_distance", GameVars.ANIM_FRAME + GameVars.UPS_INV, false, new TimeListener() {
//						@Override
//						public void onTime(Entity entity) {
//							Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
//						}
//					});
//					
//					// Add swing
//					// CLEANUP Copy swing b/c when removed if the pooled engine is implemented the data will get reset
//					SwingComponent currSwing = knightComp.getCurrentAttack().getSwingComp();
//					Engine engine = Mappers.engine.get(entity).engine;
//					entity.add(engine.createComponent(SwingComponent.class).set(
//							currSwing.rx, 
//							currSwing.ry, 
//							currSwing.startAngle, 
//							currSwing.endAngle, 
//							currSwing.delay + GameVars.UPS_INV + GameVars.ANIM_FRAME, 
//							currSwing.damage,
//							currSwing.knockback).setEffects(currSwing.effects));
//					Mappers.swing.get(entity).shouldSwing = true;
//				
//					// CLEANUP GROSS!!! 
//					Mappers.timer.get(entity).add("anim_timer", 0.0f, false, new TimeListener() {
//						@Override
//						public void onTime(Entity entity) {
//							ESMComponent esmComp = Mappers.esm.get(entity);
//							AnimationStateMachine asm = esmComp.first().getCurrentStateObject().getASM();
//							asm.changeState(Mappers.knight.get(entity).getCurrentAttack().getSwingAnimation());
//						}
//					});
//					
//					// Lock Abilities
//					Mappers.ability.get(entity).lockAllBlocking();
//				}
//
//				@Override
//				public void onExit(State nextState, Entity entity) {
//					Mappers.body.get(entity).body.setBullet(false);
//					Mappers.body.get(entity).body.setGravityScale(0.2f);
//					entity.remove(SwingComponent.class);
//				}
//			});
//		
//		esm.createState(EntityStates.SWING_ANTICIPATION)
//			.addAnimations(anticipationAnims)
//			.addTag(TransitionTag.STATIC_STATE)
//			.addChangeListener(new StateChangeListener() {
//				@Override
//				public void onEnter(State prevState, Entity entity) {
//					// Set velocity to 0
//					Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
//					Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
//					
//					// CLEANUP GROSS!!! 
//					Mappers.timer.get(entity).add("anim_timer", 0.0f, false, new TimeListener() {
//						@Override
//						public void onTime(Entity entity) {
//							ESMComponent esmComp = Mappers.esm.get(entity);
//							AnimationStateMachine asm = esmComp.first().getCurrentStateObject().getASM();
//							asm.changeState(Mappers.knight.get(entity).getCurrentAttack().getAnticipationAnimation());
//						}
//					});
//				}
//				
//				@Override
//				public void onExit(State nextState, Entity entity) {
//					if(nextState != EntityStates.SWING_ATTACK){
//						// Put things back to normal
//						KnightComponent knightComp = Mappers.knight.get(entity);
//						knightComp.first = true;
//						knightComp.chains = 0;
//						knightComp.hitAnEnemy = false;
//						knightComp.hitEnemies.clear();
//						
//						Mappers.body.get(entity).body.setGravityScale(1.0f);
//						Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
//						Mappers.ability.get(entity).unlockAllBlocking();
//					}else{
//						Mappers.knight.get(entity).chains++;
//					}
//				}
//			});
//		
//		// SWINGING TRANSITIONS
//		// ******************************************
//		
//		InputTransitionData attackPress = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK).build();
//		MultiTransition chainAttack = new MultiTransition(Transitions.INPUT, attackPress)
//				.and(new Transition() {
//					// For combo attack, enemy needs to be in range 
//					@Override
//					public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
//						LevelComponent levelComp = Mappers.level.get(entity);
//						LevelHelper helper = levelComp.levelHelper;
//						KnightComponent knightComp = Mappers.knight.get(entity);
//						
//						// You can't chain if you've hit your max number of chains or haven't hit an enemy
//						if(knightComp.chains >= knightComp.maxChains || !knightComp.hitAnEnemy) return false;
//						
//						Array<Entity> entities = helper.getEntities(new EntityGrabber() {
//							@Override
//							public boolean validEntity(Entity me, Entity other) {
//								if(Mappers.status.get(other).same(Mappers.status.get(me))) return false;
//								if(Mappers.heatlh.get(other).health <= 0) return false;
//								if(Mappers.knight.get(me).hitEnemies.contains(other)) return false;
//								
//								Body myBody = Mappers.body.get(me).body;
//								Body otherBody = Mappers.body.get(other).body;
//								
//								// Can only chain to enemies in front of you
//								FacingComponent facingComp = Mappers.facing.get(me);
//								
//								float myX = myBody.getPosition().x;
//								float myY = myBody.getPosition().y;
//								float otherX = otherBody.getPosition().x;
//								float otherY = otherBody.getPosition().y;
//
//								float minX = 0.5f;
//								float maxX = knightStats.get("chain_max_horizontal_range");
//								float yRange = knightStats.get("chain_max_vertical_range");
//								
//								// Construct box in front of you
//								float closeX = facingComp.facingRight ? myX + minX : myX - minX;
//								float farX = facingComp.facingRight ? myX + maxX : myX - maxX;
//								float top = myY + yRange;
//								float bottom = myY - yRange;
//								
//								// If the enemy is within the box, the last check you need to do is whether or not the enemy is visible
//								if(((facingComp.facingRight && otherX >= closeX && otherX <= farX) || (!facingComp.facingRight && otherX >= farX && otherX <= closeX)) 
//										&& otherY <= top && otherY >= bottom){
//									
//									// Ray Trace
//									float angle = MathUtils.atan2(otherY - myY, otherX - myX) * MathUtils.radiansToDegrees;
//									
//									// Check to see what quadrant the angle is in and select two vertices of hit box
//									Rectangle myHitbox = Mappers.body.get(me).getAABB();
//									Rectangle otherHitbox = Mappers.body.get(other).getAABB();
//									
//									float x1 = 0.0f;
//									float y1 = 0.0f;
//									float x2 = 0.0f;
//									float y2 = 0.0f;
//									
//									float off = 0.25f;
//									
//									float toX1 = 0.0f;
//									float toY1 = 0.0f;
//									float toX2 = 0.0f;
//									float toY2 = 0.0f;
//
//									// Quadrant 1 or 3
//									if((angle >= 0 && angle <= 90) || (angle >= -180 && angle <= -90)){
//										// Use upper left and lower right
//										x1 = myX - myHitbox.width * 0.5f + off;
//										y1 = myY + myHitbox.height * 0.5f - off;
//										x2 = myX + myHitbox.width * 0.5f - off;
//										y2 = myY - myHitbox.height * 0.5f + off;
//										toX1 = otherX - otherHitbox.width * 0.5f;
//										toY1 = otherY + otherHitbox.height * 0.5f;
//										toX2 = otherX + otherHitbox.width * 0.5f;
//										toY2 = otherY - otherHitbox.height * 0.5f;
//									}
//									// Quadrant 2 or 4
//									else{
//										// Use lower left and upper right
//										x1 = myX - myHitbox.width * 0.5f + off;
//										y1 = myY - myHitbox.height * 0.5f + off;
//										x2 = myX + myHitbox.width * 0.5f - off;
//										y2 = myY + myHitbox.height * 0.5f - off;
//										toX1 = otherX - otherHitbox.width * 0.5f;
//										toY1 = otherY - otherHitbox.height * 0.5f;
//										toX2 = otherX + otherHitbox.width * 0.5f;
//										toY2 = otherY + otherHitbox.height * 0.5f;
//									}
//									
////									if(DebugInput.isToggled(DebugToggle.SHOW_CHAIN_BOX)){
////										DebugRender.setType(ShapeType.Line);
////										DebugRender.setColor(Color.CYAN);
////										DebugRender.rect(facingComp.facingRight ? closeX : farX, bottom, Math.abs(farX - closeX), top - bottom);
////										DebugRender.line(x1, y1, toX1, toY1);
////										DebugRender.line(x2, y2, toX2, toY2);
////									}
////									// Slope of normal line is undefined
////									if(MathUtils.isEqual(myY - otherY, 0.0f)){
////										toX1 = otherX;
////										toY1 = y1;
////										toX2 = otherX;
////										toY2 = y2;
////									}else if(MathUtils.isEqual(myX - otherX, 0.0f)){
////										toX1 = x1;
////										toY1 = otherY;
////										toX2 = x2;
////										toY2 = otherY;
////									}
////									else{
////										// Negative reciprocal b/c its the slope of the normal line
////										float slope = (myY - otherY) / (myX - otherX);
////										float slopeNorm = -1.0f / slope;
////										
////										// Find intersect
////										// y - y1 = m(x - x1)
////										// y = mx - mx1 + y1
////										// b = -mx1 + y1
////										float b1 = -slope * x1 + y1;
////										float b2 = -slope * x2 + y2;
////										float bNorm = -slopeNorm * otherX + otherY;
////										
////										// y1 = slope * x1 + b1
////										// y2 = slope * x2 + b2
////										// yNorm = slopeNorm * x? + bNorm
////										
////										// slopeNorm * x + bNorm = slope * x + b1
////										// slopeNorm * x - slope * x = b1 - bNorm
////										// x * (slopeNorm - slope) = b1 - bNorm
////										// x = (b1 - bNorm) / (slopeNorm - slope)
////										
////										toX1 = (b1 - bNorm) / (slopeNorm - slope);
////										toY1 = slopeNorm * toX1 + bNorm;
////										toX2 = (b2 - bNorm) / (slopeNorm - slope);
////										toY2 = slopeNorm * toX2 + bNorm;
////									}
//									
//									Level level = Mappers.level.get(me).level;
//									return level.performRayTrace(x1, y1, toX1, toY1) && level.performRayTrace(x2, y2, toX2, toY2);
//								}
//								return false;
//							}
//
//							@SuppressWarnings("unchecked")
//							@Override
//							public Family componentsNeeded() {
//								return Family.all(LevelComponent.class, StatusComponent.class, HealthComponent.class, BodyComponent.class).get();
//							}
//						});
//						
//						if(entities.size == 0) return false;
//						final Entity copy = entity;
//						Sort.instance().sort(entities, new Comparator<Entity>() {
//							@Override
//							public int compare(Entity o1, Entity o2) {
//								float d1 = PhysicsUtils.getDistanceSqr(Mappers.body.get(copy).body, Mappers.body.get(o1).body);
//								float d2 = PhysicsUtils.getDistanceSqr(Mappers.body.get(copy).body, Mappers.body.get(o2).body);
//								return d1 == d2 ? 0 : (d1 < d2 ? -1 : 1);
//							}
//						});
//						
//						Entity toChain = entities.first();
//						Vector2 chainPos = Mappers.body.get(toChain).body.getPosition();
//						
//						knightComp.lungeX = chainPos.x;
//						knightComp.lungeY = chainPos.y;
//						
//						return true;
//					}
//					
//					@Override
//					public boolean allowMultiple() {
//						return false;
//					}
//					
//					@Override
//					public String toString() {
//						return "Can Chain";
//					}
//				});
//		
//		Transition enemyNotHit = new Transition() {
//			@Override
//			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
//				return !Mappers.knight.get(entity).hitAnEnemy;
//			}
//			
//			@Override
//			public boolean allowMultiple() {
//				return false;
//			}
//			
//			@Override
//			public String toString() {
//				return "Enemy Not Hit";
//			}
//		};
//		
//		Transition maxChain = new Transition() {
//			@Override
//			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
//				return Mappers.knight.get(entity).chains >= Mappers.knight.get(entity).maxChains;
//			}
//			
//			@Override
//			public boolean allowMultiple() {
//				return false;
//			}
//			
//			@Override
//			public String toString() {
//				return "Max Chain";
//			}
//		};
//		
//		// 
//		MultiTransition exitChaining = new MultiTransition(maxChain).or(enemyNotHit).or(Transitions.TIME, new TimeTransitionData(0.3f));
//		MultiTransition attackTransition = new MultiTransition(Transitions.INPUT, attackPress).and(Transitions.TIME, new TimeTransitionData(knightStats.get("sword_delay")));
//		
//		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), attackTransition, EntityStates.SWING_ATTACK);
//		esm.addTransition(EntityStates.IDLING, attackTransition, EntityStates.IDLE_TO_SWING);
//		esm.addTransition(EntityStates.IDLE_TO_SWING, Transitions.ANIMATION_FINISHED, EntityStates.SWING_ATTACK);
//		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.SWING_ANTICIPATION);
//		esm.addTransition(EntityStates.SWING_ANTICIPATION, chainAttack, EntityStates.SWING_ATTACK);
//		esm.addTransition(EntityStates.SWING_ANTICIPATION, exitChaining, EntityStates.IDLING);
		
		final FrameMovementComponent swingFrames = engine.createComponent(FrameMovementComponent.class).set("knight/frames_swing_attack");
		
		esm.createState(EntityStates.SWING_ATTACK)
			.add(engine.createComponent(SwingComponent.class).set(swing1.rx, swing1.ry, swing1.startAngle, swing1.endAngle, swing1.delay, swing1.damage, swing1.knockback))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SWING)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					if(prevState == EntityStates.JUMPING || prevState == EntityStates.FALLING) {
						entity.add(engine.createComponent(SpeedComponent.class).set(knightStats.get("air_speed")));
						entity.add(engine.createComponent(DirectionComponent.class));
						entity.add(engine.createComponent(GroundMovementComponent.class));
					} else {
						entity.add(swingFrames);
					}
					
					Mappers.ability.get(entity).lockAllBlocking();
					Mappers.timer.get(entity).add("swing_delay", GameVars.ANIM_FRAME * 1, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							Mappers.swing.get(entity).shouldSwing = true;
						}
					});
					
					// Lock facing
					Mappers.facing.get(entity).locked = entity.getComponent(FrameMovementComponent.class) != null;
					
					// Set gravity scale to 0
//					Body body = Mappers.body.get(entity).body;
//					body.setGravityScale(0.2f);
//					body.setLinearVelocity(0.0f, 0.0f);
				}
	
				@Override
				public void onExit(State nextState, Entity entity) {
					entity.remove(GroundMovementComponent.class);
					entity.remove(SpeedComponent.class);
					entity.remove(DirectionComponent.class);
					entity.remove(FrameMovementComponent.class);
	
					Mappers.facing.get(entity).locked = false;
//					Mappers.body.get(entity).body.setGravityScale(1.0f);
					Mappers.ability.get(entity).unlockAllBlocking();
				}
			});
		
		// Attack transitions
		InputTransitionData attackInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();
		
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE, TransitionTag.AIR_STATE), Transitions.INPUT, attackInput, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		// ******************************************
		
		// Ability States
		esm.createState(EntityStates.PARRY_BLOCK)
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(DirectionComponent.class))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.PARRY_BLOCK);
		
		esm.createState(EntityStates.PARRY_SWING)
			.add(engine.createComponent(FrameMovementComponent.class).set("knight/frames_parry_swing"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.PARRY_SWING);
		
		esm.createState(EntityStates.KICK)
			.add(engine.createComponent(FrameMovementComponent.class).set("knight/frames_kick"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.KICK);
		
		esm.createState(EntityStates.OVERHEAD_SWING)
			.add(engine.createComponent(FrameMovementComponent.class).set("knight/frames_overhead_swing"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.OVERHEAD_SWING);
		
		esm.createState(EntityStates.SLAM)
			.add(engine.createComponent(FrameMovementComponent.class).set("knight/frames_slam"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SLAM);
		
		esm.createState(EntityStates.DASH)
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.DASH);
		
		esm.createState(EntityStates.SPIN_SLICE)
			.add(engine.createComponent(FrameMovementComponent.class).set("knight/frames_slam"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SPIN_SLICE);
		
		esm.createState(EntityStates.TORNADO)
			.add(engine.createComponent(FrameMovementComponent.class).set("knight/frames_tornado"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.TORNADO_INIT)
			.addAnimation(EntityAnim.TORNADO_SWING)
			.addAnimTransition(EntityAnim.TORNADO_INIT, Transitions.ANIMATION_FINISHED, EntityAnim.TORNADO_SWING);
				
		// Build the roll body
		Rectangle hitbox = Mappers.body.get(knight).getAABB();
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.DynamicBody;
		bdef.fixedRotation = true;
	
		PolygonShape polyShape = new PolygonShape();
		polyShape.setAsBox(hitbox.width * 0.5f, 0.05f, new Vector2(0, -hitbox.height * 0.5f + 0.05f), 0.0f);
		
		FixtureDef fdef = new FixtureDef();
		fdef.friction = 0.0f;
		fdef.shape = polyShape;
		
		Body rollBody = PhysicsUtils.createPhysics(world, knight, bdef, fdef, CollisionBodyType.MOB, FixtureType.ROLL);
		polyShape.dispose();

		Mappers.knight.get(knight).rollBody = rollBody;
		rollBody.setActive(false);
		
		esm.createState(EntityStates.ROLL)
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.ROLL)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					// Physics
					Body oldBody = Mappers.body.get(entity).body;
					Body rollBody = Mappers.knight.get(entity).rollBody;
					
					oldBody.setActive(false);
					rollBody.setActive(true);
				
					rollBody.setTransform(oldBody.getPosition().add(0.0f, 0.005f), 0.0f);
					rollBody.setLinearVelocity(0.0f, 0.0f);
					
					Mappers.body.get(entity).set(rollBody);
					Mappers.knight.get(entity).body = oldBody;
					
					// **************************
					
					Mappers.ability.get(entity).lockAllBlocking();
					Mappers.facing.get(entity).locked = true;
					Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
					
					float rollSpeed = knightStats.get("roll_speed");
					EntityUtils.add(entity, ForceComponent.class).set(Mappers.facing.get(entity).facingRight ? rollSpeed : -rollSpeed, 0.0f);
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Body oldBody = Mappers.knight.get(entity).body;
					Body currBody = Mappers.body.get(entity).body;
					
					currBody.setActive(false);
					
					oldBody.setActive(true);
					oldBody.setLinearVelocity(0.0f, 0.0f);
					oldBody.setTransform(currBody.getPosition().x, currBody.getPosition().y, 0.0f);
					Mappers.body.get(entity).set(oldBody);
					
					Mappers.ability.get(entity).unlockAllBlocking();
					Mappers.facing.get(entity).locked = false;
					Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
					Mappers.knight.get(entity).rollElapsed = 0.0f;
				}
			});
		
		InputTransitionData rollData = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVEMENT, true).build();
		MultiTransition rollTransition = new MultiTransition(Transitions.INPUT, rollData).and(new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return Mappers.knight.get(entity).rollElapsed >= knightStats.get("roll_cooldown");
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Time: " + knightStats.get("roll_cooldown") + "s";
			}
		});
		
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE, TransitionTag.AIR_STATE), rollTransition, EntityStates.ROLL);
		esm.addTransition(EntityStates.ROLL, Transitions.TIME, new TimeTransitionData(0.4f), EntityStates.IDLING);

		Mappers.timer.get(knight).add("roll_handler", GameVars.UPS_INV, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.knight.get(entity).rollElapsed += GameVars.UPS_INV;
			}
		});
		
		// Movement tweaks
//		esm.getState(EntityStates.IDLING).addChangeListener(new StateChangeListener() {
//			@Override
//			public void onEnter(State prevState, Entity entity) {
//				if(prevState == EntityStates.JUMPING) {
//					Mappers.body.get(entity).body.setLinearVelocity(Mappers.body.get(entity).body.getLinearVelocity().x, 0);
//				}
//			}
//
//			@Override
//			public void onExit(State nextState, Entity entity) {
//			}
//		});
//		
//		
//		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
//		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
//		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
//
//		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
//		jumpData.triggers.add(new InputTrigger(Actions.JUMP, false));
//
//		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
//		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
//		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
//
//		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
//		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
//		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
//
////		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
////		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
//		
//		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
//		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
//		
//		InputTransitionData ladderInputData = new InputTransitionData(Type.ANY_ONE, true);
//		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_UP, false));
//		ladderInputData.triggers.add(new InputTrigger(Actions.MOVE_DOWN, false));
//		
//		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
//		
//		MultiTransition ladderTransition = new MultiTransition(Transitions.INPUT, ladderInputData)
//					.and(Transitions.COLLISION, ladderCollisionData);
//		
//		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
//		
//		esm.addTransition(TransitionTag.GROUND_STATE, Transitions.FALLING, EntityStates.FALLING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transitions.INPUT, runningData, EntityStates.RUNNING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transitions.INPUT, jumpData, EntityStates.JUMPING);
//		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING), Transitions.FALLING, EntityStates.FALLING);
//		esm.addTransition(esm.all(TransitionTag.AIR_STATE)/*.exclude(EntityStates.JUMPING)*/, new MultiTransition(Transitions.LANDED).and(Transitions.TIME, new TimeTransitionData(0.1f)), EntityStates.IDLING);
//		esm.addTransition(EntityStates.RUNNING, Transitions.INPUT, idleData, EntityStates.IDLING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transitions.INPUT, bothData, EntityStates.IDLING);
//		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
//		esm.addTransition(EntityStates.CLIMBING, Transitions.COLLISION, ladderFall, EntityStates.FALLING);
//		esm.addTransition(EntityStates.CLIMBING, Transitions.LANDED, EntityStates.IDLING);
		
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
					Mappers.timer.get(entity).add("delayed_knife_throw", GameVars.ANIM_FRAME * 1, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							AnimationStateMachine upperBodyASM = Mappers.asm.get(entity).get(EntityAnim.IDLE_ARMS);
							if(upperBodyASM != null && upperBodyASM.getCurrentState() == EntityAnim.THROW_ARMS) {
								ProjectileFactory.spawnThrowingKnife(entity, 5.0f, 5.0f, projectileSpeed, projectileDamage, 0.0f);
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
		rogueSM.addTransition(EntityStates.PROJECTILE_ATTACK, Transitions.TIME, new TimeTransitionData(0.4f), EntityStates.IDLING);
		
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
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.ROGUE_IDLE_LEGS));
		animMap.put(EntityAnim.IDLE_ARMS, assets.getAnimation(Asset.ROGUE_IDLE_ARMS));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.ROGUE_FALL_LEGS));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.ROGUE_JUMP_LEGS));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.ROGUE_RISE_LEGS));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.ROGUE_APEX_LEGS));
		animMap.put(EntityAnim.CLIMBING, assets.getAnimation(Asset.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.ROGUE_RUN_LEGS));
		animMap.put(EntityAnim.RUN_ARMS, assets.getAnimation(Asset.ROGUE_RUN_ARMS));
		animMap.put(EntityAnim.THROW_ARMS, assets.getAnimation(Asset.ROGUE_THROW_ARMS));
		animMap.put(EntityAnim.BACK_PEDAL, assets.getAnimation(Asset.ROGUE_BACK_PEDAL_LEGS));
		animMap.put(EntityAnim.BACK_PEDAL_ARMS, assets.getAnimation(Asset.ROGUE_BACK_PEDAL_ARMS));
		animMap.put(EntityAnim.APEX_ARMS, assets.getAnimation(Asset.ROGUE_APEX_ARMS));
		animMap.put(EntityAnim.FALL_ARMS, assets.getAnimation(Asset.ROGUE_FALL_ARMS));
		animMap.put(EntityAnim.RISE_ARMS, assets.getAnimation(Asset.ROGUE_RISE_ARMS));
		animMap.put(EntityAnim.JUMP_ARMS, assets.getAnimation(Asset.ROGUE_JUMP_ARMS));
		animMap.put(EntityAnim.DYNAMITE_ARMS, assets.getAnimation(Asset.ROGUE_DYNAMITE_ARMS));
		animMap.put(EntityAnim.SMOKE_BOMB_ARMS, assets.getAnimation(Asset.ROGUE_SMOKE_BOMB_ARMS));
		animMap.put(EntityAnim.BOOMERANG_ARMS, assets.getAnimation(Asset.ROGUE_BOOMERANG_ARMS));
		animMap.put(EntityAnim.HOMING_KNIVES_THROW, assets.getAnimation(Asset.ROGUE_HOMING_KNIVES_THROW));
		animMap.put(EntityAnim.DASH, assets.getAnimation(Asset.ROGUE_HOMING_KNIVES_THROW));
		animMap.put(EntityAnim.EXECUTE, assets.getAnimation(Asset.ROGUE_EXECUTE));
		animMap.put(EntityAnim.BOW_ATTACK, assets.getAnimation(Asset.ROGUE_BOW));
		animMap.put(EntityAnim.FLASH_POWDER_ARMS, assets.getAnimation(Asset.ROGUE_BOOMERANG_ARMS));
		
		Entity rogue = new EntityBuilder(ROGUE, FRIENDLY)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, rogueStats.get("health"), rogueStats.get("weight"))
			.build();
		
		// Player Related Components
		rogue.getComponent(ImmuneComponent.class).add(EffectType.STUN);
		rogue.add(engine.createComponent(MoneyComponent.class));
		rogue.add(engine.createComponent(PlayerComponent.class));
		rogue.add(engine.createComponent(BarrierComponent.class)
				.set(rogueStats.get("shield"), 
					 rogueStats.get("shield"), 
					 rogueStats.get("shield_rate"), 
					 rogueStats.get("shield_delay")));
		rogue.add(engine.createComponent(RogueComponent.class).set(rogueStats.get("switch_delay")));
		
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

		DynamiteAbility dynamiteAbility = new DynamiteAbility(
				rogueStats.get("dynamite_cooldown"),
				Actions.ABILITY_1,
				animMap.get(EntityAnim.DYNAMITE_ARMS), 
				rogueStats.get("dynamite_knockback"),
				rogueStats.get("dynamite_damage"),
				rogueStats.get("dynamite_explosion_radius"));
		dynamiteAbility.deactivate();
		
		HomingKnivesAbility homingKnivesAbility = new HomingKnivesAbility(
				rogueStats.get("homing_knives_cooldown"), 
				Actions.ABILITY_1,
				animMap.get(EntityAnim.HOMING_KNIVES_THROW), 
				(int)rogueStats.get("homing_knives_per_cluster"),
				rogueStats.get("homing_knives_damage"),
				rogueStats.get("homing_knives_range"),
				rogueStats.get("homing_knives_speed"));
		homingKnivesAbility.deactivate();
		
		VanishAbility vanishAbility = new VanishAbility(
				rogueStats.get("vanish_cooldown"),
				Actions.ABILITY_1,
				rogueStats.get("vanish_duration"));
		
		DashAbility dashAbility = new DashAbility(
				rogueStats.get("dash_cooldown"),
				Actions.ABILITY_1,
				rogueStats.get("dash_distance"),
				rogueStats.get("dash_speed"));
		dashAbility.deactivate();
		
		BoomerangAbility boomerangAbility = new BoomerangAbility(
				rogueStats.get("boomerang_cooldown"), 
				Actions.ABILITY_2,
				rogueStats.get("boomerang_speed"),
				rogueStats.get("boomerang_damage"),
				rogueStats.get("boomerang_max_duration"));
		
		ExecuteAbility executeAbility = new ExecuteAbility(
				rogueStats.get("execute_cooldown"), 
				Actions.ABILITY_3, 
				animMap.get(EntityAnim.EXECUTE));
		executeAbility.deactivate();
		
		BowAbility bowAbility = new BowAbility(
				rogueStats.get("bow_cooldown"), 
				Actions.ABILITY_3, 
				animMap.get(EntityAnim.BOW_ATTACK), 
				rogueStats.get("bow_damage"),
				rogueStats.get("bow_speed"));
		
		FlashPowderAbility flashPowderAbility = new FlashPowderAbility(
				rogueStats.get("flash_powder_cooldown"), 
				Actions.ABILITY_2, 
				animMap.get(EntityAnim.FLASH_POWDER_ARMS),
				rogueStats.get("flash_powder_stun_duration"));
		flashPowderAbility.deactivate();
		
		BalloonTrapAbility balloonAbility = new BalloonTrapAbility(
				rogueStats.get("balloon_cooldown"),
				Actions.ABILITY_3, 
				rogueStats.get("balloon_damage_per_pellet"),
				(int) rogueStats.get("balloon_num_pellets"), 
				rogueStats.get("balloon_pellet_speed"),
				(int) rogueStats.get("balloon_max_balloons"));
		balloonAbility.deactivate();
		
		rogue.add(engine.createComponent(AbilityComponent.class)
				.add(dynamiteAbility)
				.add(homingKnivesAbility)
				.add(vanishAbility)
				.add(dashAbility)
				.add(boomerangAbility)
				.add(executeAbility)
				.add(bowAbility)
				.add(flashPowderAbility)
				.add(balloonAbility));
		
		createRogueAttackMachine(rogue, rogueStats);
		
		
		
//		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Rogue ESM", engine, rogue)
//			.idle()
//			.run(rogueStats.get("ground_speed"))
//			.jump(rogueStats.get("jump_force"), rogueStats.get("jump_float_amount"), rogueStats.get("air_speed"), true, true)
//			.fall(rogueStats.get("air_speed"), true)
//			.climb(rogueStats.get("climb_speed"))
//			.build();
		
		EntityStateMachine esm = StateFactory.createBaseBipedal(rogue, rogueStats);
		
		esm.createState(EntityStates.DOUBLE_JUMP)
			.add(engine.createComponent(SpeedComponent.class).set(rogueStats.get("air_speed")))
			.add(engine.createComponent(DirectionComponent.class))
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(JumpComponent.class).set(rogueStats.get("double_jump_force"), rogueStats.get("double_jump_float_amount")))
			.addTag(TransitionTag.AIR_STATE)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					JumpComponent jumpComp = Mappers.jump.get(entity);
					InputComponent inputComp = Mappers.input.get(entity);						
					jumpComp.multiplier = inputComp.input.getValue(Actions.JUMP);
					Mappers.rogue.get(entity).canDoubleJump = false;
				}
				@Override
				public void onExit(State nextState, Entity entity) {
					if(nextState == EntityStates.IDLING) {
						Mappers.body.get(entity).body.setLinearVelocity(Mappers.body.get(entity).body.getLinearVelocity().x, 0);
					}
				}
			});
		
		Mappers.timer.get(rogue).add("double_jump", GameVars.UPS_INV, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				if(Mappers.collision.get(entity).onGround()) {
					Mappers.rogue.get(entity).canDoubleJump = true;
				}
			}
		});
		
		Transition doubleJumpTransition = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return Mappers.rogue.get(entity).canDoubleJump;
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Can Double Jump";
			}
		};
		
		// TODO Fix transition table so that when you add transition tags things update
		InputTransitionData jumpData = new InputTransitionData.Builder(Type.ALL, true).add(Actions.JUMP, true).build();
		esm.addTransition(TransitionTag.AIR_STATE, new MultiTransition(doubleJumpTransition).and(Transitions.INPUT, jumpData), EntityStates.DOUBLE_JUMP);
		esm.addTransition(esm.all(EntityStates.DOUBLE_JUMP).exclude(EntityStates.FALLING), Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(EntityStates.DOUBLE_JUMP, new MultiTransition(Transitions.LANDED).and(Transitions.TIME, new TimeTransitionData(0.1f)), EntityStates.IDLING);
		
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
						case DOUBLE_JUMP:
							return EntityAnim.JUMP_ARMS;
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
		upperBodyASM.createState(EntityAnim.JUMP_ARMS);
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
		upperBodyASM.createState(EntityAnim.DYNAMITE_ARMS);
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
		upperBodyASM.addTransition(EntityAnim.JUMP_ARMS, Transitions.ANIMATION_FINISHED, EntityAnim.RISE_ARMS);
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
		upperBodyASM.addTransition(upperBodyASM.one(
						EntityAnim.THROW_ARMS,
						EntityAnim.SMOKE_BOMB_ARMS,
						EntityAnim.BOOMERANG_ARMS,
						EntityAnim.FLASH_POWDER_ARMS), Transitions.ANIMATION_FINISHED, EntityAnim.INIT);
		
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
				case DOUBLE_JUMP:
					return EntityAnim.JUMP;
				case FALLING:
					return EntityAnim.JUMP_APEX;
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
		lowerBodyASM.addTransition(EntityAnim.JUMP, Transitions.ANIMATION_FINISHED, EntityAnim.RISE);
		lowerBodyASM.addTransition(EntityAnim.JUMP_APEX, Transitions.ANIMATION_FINISHED, EntityAnim.FALLING);
		
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
		
		EntityState doubleJumpState = esm.getState(EntityStates.DOUBLE_JUMP);
		doubleJumpState.removeAnimations();
		doubleJumpState.addSubstateMachine(lowerBodyASM);
		doubleJumpState.addSubstateMachine(upperBodyASM);
		
		esm.createState(EntityStates.HOMING_KNIVES)
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(DirectionComponent.class))
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.addAnimation(EntityAnim.HOMING_KNIVES_THROW);
		
		esm.createState(EntityStates.EXECUTE)
			.add(engine.createComponent(FrameMovementComponent.class).set("rogue/frames_execute"))
			.addAnimation(EntityAnim.EXECUTE);
		
		esm.createState(EntityStates.BOW_ATTACK)
			.add(engine.createComponent(FrameMovementComponent.class).set("rogue/frames_bow"))
			.addAnimation(EntityAnim.BOW_ATTACK);
		
		esm.createState(EntityStates.DASH)
			.addAnimation(EntityAnim.DASH);
		
//		InputTransitionData runningData = new InputTransitionData(Type.ONLY_ONE, true);
//		runningData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
//		runningData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
//
//		InputTransitionData jumpData = new InputTransitionData(Type.ALL, true);
//		jumpData.triggers.add(new InputTrigger(Actions.JUMP, true));
//
//		InputTransitionData idleData = new InputTransitionData(Type.ALL, false);
//		idleData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
//		idleData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
//
//		InputTransitionData bothData = new InputTransitionData(Type.ALL, true);
//		bothData.triggers.add(new InputTrigger(Actions.MOVE_LEFT));
//		bothData.triggers.add(new InputTrigger(Actions.MOVE_RIGHT));
//		
//		MultiTransition idleTransition = new MultiTransition(Transitions.INPUT, idleData).or(Transitions.INPUT, bothData);
//
//		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
//		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
//		
//		
//		InputTransitionData ladderInputData = new InputTransitionData.Builder(Type.ANY_ONE, true)
//					.add(Actions.MOVE_UP)
//					.add(Actions.MOVE_DOWN)
//					.build();
//		
//		CollisionTransitionData ladderCollisionData = new CollisionTransitionData(CollisionType.LADDER, true);
//		
//		MultiTransition ladderTransition = new MultiTransition(Transitions.INPUT, ladderInputData)
//			.and(Transitions.COLLISION, ladderCollisionData);
//		
//		CollisionTransitionData ladderFall = new CollisionTransitionData(CollisionType.LADDER, false);
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
		
//		esm.addTransition(TransitionTag.GROUND_STATE, Transitions.FALLING, EntityStates.FALLING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transitions.INPUT, runningData, EntityStates.RUNNING);
//		esm.addTransition(esm.one(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transitions.INPUT, jumpData, EntityStates.JUMPING);
//		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transitions.FALLING, EntityStates.FALLING);
//		esm.addTransition(esm.one(TransitionTag.AIR_STATE, EntityStates.WALL_SLIDING).exclude(EntityStates.JUMPING), Transitions.LANDED, EntityStates.IDLING);
//		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), idleTransition, EntityStates.IDLING);
//		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE), ladderTransition, EntityStates.CLIMBING);
//		esm.addTransition(EntityStates.CLIMBING, Transitions.COLLISION, ladderFall, EntityStates.FALLING);
//		esm.addTransition(EntityStates.CLIMBING, Transitions.LANDED, EntityStates.IDLING);
//		esm.addTransition(EntityStates.FALLING, wallSlideTransition, EntityStates.WALL_SLIDING);
//		esm.addTransition(EntityStates.WALL_SLIDING, offWall, EntityStates.FALLING);
//		esm.addTransition(EntityStates.WALL_SLIDING, Transitions.INPUT, jumpData, EntityStates.WALL_JUMP);
		
		esm.changeState(EntityStates.IDLING);
		
//		esm.addTransition(EntityStates.DASH, Transitions.COLLISION, onRightWallData, EntityStates.FALLING);
//		esm.addTransition(EntityStates.DASH, Transitions.COLLISION, onLeftWallData, EntityStates.FALLING);
//		System.out.print(esm.printTransitions(false));
		return rogue;
	}
	
	public static Entity createMonk(float x, float y){
		final EntityStats monkStats = EntityLoader.get(EntityIndex.MONK);
		
		// Animations
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.MONK_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.MONK_RUN));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.MONK_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.MONK_FALL));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.MONK_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.MONK_APEX));
		animMap.put(EntityAnim.SWING, assets.getAnimation(Asset.MONK_ATTACK_FRONT));
		animMap.put(EntityAnim.SWING_UP, assets.getAnimation(Asset.MONK_ATTACK_UP));
		animMap.put(EntityAnim.SWING_ANTICIPATION, assets.getAnimation(Asset.MONK_ATTACK_FRONT_ANTICIPATION));
		animMap.put(EntityAnim.SWING_UP_ANTICIPATION, assets.getAnimation(Asset.MONK_ATTACK_UP_ANTICIPATION));
		animMap.put(EntityAnim.WIND_BURST, assets.getAnimation(Asset.MONK_WIND_BURST));
		animMap.put(EntityAnim.INSTA_WALL, assets.getAnimation(Asset.MONK_STAFF_SLAM));
		animMap.put(EntityAnim.DASH, assets.getAnimation(Asset.MONK_DASH));
		
		Entity monk = new EntityBuilder(MONK, FRIENDLY)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, monkStats.get("health"), monkStats.get("weight"))
			.build();
		
		// Abilities
		PoisonDebuffAbility poisonAbility = new PoisonDebuffAbility(
				monkStats.get("poison_cooldown"), 
				Actions.ABILITY_1, 
				monkStats.get("poison_ability_duration"),
				monkStats.get("poison_dps"),
				monkStats.get("poison_decay_rate"),
				monkStats.get("poison_duration"));

		WindBurstAbility windBurstAbility = new WindBurstAbility(
				monkStats.get("wind_burst_cooldown"),
				Actions.ABILITY_2,
				assets.getAnimation(Asset.MONK_WIND_BURST),
				monkStats.get("wind_burst_damage"),
				monkStats.get("wind_burst_knockback"));
		
		InstaWallAbility instaWallAbility = new InstaWallAbility(
				monkStats.get("insta_wall_cooldown"), 
				Actions.ABILITY_3,
				animMap.get(EntityAnim.INSTA_WALL),
				monkStats.get("insta_wall_duration"), 
				(int)monkStats.get("insta_wall_max_height"));
		
		
		// Player Related Components4
		monk.getComponent(ImmuneComponent.class).add(EffectType.STUN);
		monk.add(engine.createComponent(MoneyComponent.class));
		monk.add(engine.createComponent(PlayerComponent.class));
		monk.add(engine.createComponent(BarrierComponent.class)
				.set(monkStats.get("shield"), 
					 monkStats.get("shield"), 
					 monkStats.get("shield_rate"), 
					 monkStats.get("shield_delay")));
		monk.add(engine.createComponent(MonkComponent.class));
		monk.add(engine.createComponent(AbilityComponent.class)
				.add(poisonAbility)
				.add(windBurstAbility)
				.add(instaWallAbility));
				
		EntityStateMachine esm = StateFactory.createBaseBipedal(monk, monkStats);
		
		esm.createState(EntityStates.SWING_ATTACK)
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SWING)
			.addAnimation(EntityAnim.SWING_UP)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Input input = Mappers.input.get(entity).input;
					final boolean swingUp = input.isPressed(Actions.MOVE_UP);
					
					Mappers.monk.get(entity).swingUp = swingUp;
					
					// Create the right swing
					SwingComponent swingComp = engine.createComponent(SwingComponent.class);
					float damage = monkStats.get("swing_damage");
					float knockback = monkStats.get("swing_knockback");
					
					if(swingUp) {
						swingComp.set(2.0f, 2.0f, 150f, 30f, GameVars.ANIM_FRAME * 1, damage, knockback);
					} else {
						swingComp.set(2.5f, 1.25f, 60f, -100f, GameVars.ANIM_FRAME * 1 + GameVars.UPS_INV, damage, knockback);
					}
					
					EffectDef activeEffect = Mappers.monk.get(entity).activeEffect;
					if(activeEffect != null) {
						swingComp.addEffect(activeEffect);
					}
					
					swingComp.shouldSwing = true;
					entity.add(swingComp);
					
					// Setup the right type of movement
					if(swingUp || !Mappers.collision.get(entity).onGround()) {
						entity.add(engine.createComponent(SpeedComponent.class).set(monkStats.get("air_speed")));
						entity.add(engine.createComponent(DirectionComponent.class));
						entity.add(engine.createComponent(GroundMovementComponent.class));
					} else {
						entity.add(engine.createComponent(FrameMovementComponent.class).set("monk/frames_swing_attack_front"));
					}
					
					// Lock facing
					Mappers.facing.get(entity).locked = entity.getComponent(FrameMovementComponent.class) != null;
				}
				
				@Override
				public void onExit(State nextState, Entity entity) {
					entity.remove(SwingComponent.class);
					entity.remove(GroundMovementComponent.class);
					entity.remove(SpeedComponent.class);
					entity.remove(DirectionComponent.class);
					entity.remove(FrameMovementComponent.class);
					
					Mappers.facing.get(entity).locked = false;
				}
			})
			.getASM().getState(EntityAnim.SWING).setChangeResolver(new StateChangeResolver() {
				@Override
				public State resolve(Entity entity, State oldState) {
					return Mappers.input.get(entity).input.isPressed(Actions.MOVE_UP) ? EntityAnim.SWING_UP : EntityAnim.SWING;
				}
			});
		
		esm.createState(EntityStates.SWING_ANTICIPATION)
			.addTag(TransitionTag.GROUND_STATE)
			.add(engine.createComponent(SpeedComponent.class).set(monkStats.get("air_speed")))
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(DirectionComponent.class))
			.addAnimation(EntityAnim.SWING_ANTICIPATION)
			.addAnimation(EntityAnim.SWING_UP_ANTICIPATION)
			.getASM().getState(EntityAnim.SWING_ANTICIPATION).setChangeResolver(new StateChangeResolver() {
				@Override
				public State resolve(Entity entity, State oldState) {
					return Mappers.monk.get(entity).swingUp ? EntityAnim.SWING_UP_ANTICIPATION : EntityAnim.SWING_ANTICIPATION;
				}
			});
		
		esm.createState(EntityStates.WIND_BURST)
			.add(engine.createComponent(FrameMovementComponent.class).set("monk/frames_monk_wind_burst"))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.WIND_BURST);
		
		esm.createState(EntityStates.INSTA_WALL)
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(GroundMovementComponent.class))
			.add(engine.createComponent(DirectionComponent.class))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.INSTA_WALL);
		
		esm.createState(EntityStates.DASH)
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.DASH)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					boolean facingRight = Mappers.facing.get(entity).facingRight;
					
					float dashSpeed = monkStats.get("dash_speed");
					EntityUtils.add(entity, ForceComponent.class).set(facingRight ? dashSpeed : -dashSpeed, 0.0f);
					Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
					
					Mappers.ability.get(entity).lockAllBlocking();
					Mappers.facing.get(entity).locked = true;
					Mappers.body.get(entity).body.setGravityScale(0.0f);
					
					Mappers.monk.get(entity).canDash = false;
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.ability.get(entity).unlockAllBlocking();
					Mappers.facing.get(entity).locked = false;
					Mappers.body.get(entity).body.setGravityScale(1.0f);
					Mappers.monk.get(entity).dashElapsed = 0.0f;
				}
				
			});
		
		Mappers.timer.get(monk).add("dash_handler", GameVars.UPS_INV, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.monk.get(entity).dashElapsed += GameVars.UPS_INV;
				if(Mappers.collision.get(entity).onGround()) {
					Mappers.monk.get(entity).canDash = true;
				}
			}
		});
		
		// Attacking
		InputTransitionData attackInput = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();
		
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE, TransitionTag.AIR_STATE), Transitions.INPUT, attackInput, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.SWING_ANTICIPATION);
		esm.addTransition(EntityStates.SWING_ANTICIPATION, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);

		// Dashing
		InputTransitionData dashData = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVEMENT, true).build();
		Transition canDash = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return Mappers.monk.get(entity).canDash && Mappers.monk.get(entity).dashElapsed >= monkStats.get("dash_cooldown");
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Can Dash";
			}
		};
		MultiTransition dashTransition = new MultiTransition(Transitions.INPUT, dashData).and(canDash);
		
		esm.addTransition(esm.one(TransitionTag.GROUND_STATE, TransitionTag.AIR_STATE), dashTransition, EntityStates.DASH);
		esm.addTransition(EntityStates.DASH, Transitions.TIME, new TimeTransitionData(monkStats.get("dash_duration")), EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		return monk;
	}
	
	// ---------------------------------------------
	// -                 ENEMIES                   -
	// ---------------------------------------------
	public static Entity createGoat(float x, float y) {
		// Stats
		EntityStats stats = EntityLoader.get(EntityIndex.GOAT);
		
		// Setup Animations
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.GOAT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.GOAT_WALK));
		animMap.put(EntityAnim.SWING, assets.getAnimation(Asset.GOAT_SWING_ATTACK));
		
		// Controller
		AIController controller = new AIController();
		
		// Setup Player
		Entity goat = new EntityBuilder(GOAT, ENEMY)
				.ai(controller)
				.animation(animMap)
				.mob(controller, stats.get("health"), stats.get("weight"))
				.physics("goat.json", x, y, true)
				.render(true)
				.build();
		goat.add(engine.createComponent(MoneyComponent.class).set((int)stats.get("money")));

		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Goat ESM", engine, goat)
				.idle()
				.run(stats.get("ground_speed"))
				.build();
		
		esm.createState(EntityStates.FALLING)
			.add(engine.createComponent(SpeedComponent.class).set(0.0f))
			.add(engine.createComponent(DirectionComponent.class))
			.add(engine.createComponent(GroundMovementComponent.class))
			.addAnimation(EntityAnim.IDLE)
			.addTag(TransitionTag.AIR_STATE);
		
		esm.createState(EntityStates.SWING_ATTACK)
			.add(engine.createComponent(FrameMovementComponent.class).set("goat/frames_overhead_swing", true))
			.add(engine.createComponent(SwingComponent.class).set(2.0f, 2.0f, 120, -60, GameVars.ANIM_FRAME * 5, stats.get("sword_damage"), stats.get("sword_knockback")))
			.addAnimation(EntityAnim.SWING)
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.swing.get(entity).shouldSwing = true;
					Mappers.facing.get(entity).locked = true;
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.facing.get(entity).locked = false;
				}
			});
		
		InputTransitionData idle1 = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVE_RIGHT).add(Actions.MOVE_LEFT).build();
		InputTransitionData idle2 = new InputTransitionData.Builder(Type.ALL, false).add(Actions.MOVE_RIGHT).add(Actions.MOVE_LEFT).build();
		InputTransitionData run = new InputTransitionData.Builder(Type.ONLY_ONE, true).add(Actions.MOVE_RIGHT).add(Actions.MOVE_LEFT).build();
		InputTransitionData attack = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();
		
		MultiTransition idleTransition = new MultiTransition(Transitions.INPUT, idle1).or(Transitions.INPUT, idle2);
		
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), idleTransition, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transitions.INPUT, run, EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, attack, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.FALLING, EntityStates.FALLING);
		esm.addTransition(EntityStates.FALLING, Transitions.LANDED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(goat);

		Selector<Entity> rootSelector = new Selector<Entity>();
		
		Sequence<Entity> pursueTarget = new Sequence<Entity>();
		pursueTarget.addChild(new TargetOnPlatformTask());
		pursueTarget.addChild(BTFactory.followOnPlatform());
		
		Sequence<Entity> attackSequence = new Sequence<Entity>();
		attackSequence.addChild(new InRangeTask(2.0f));
		attackSequence.addChild(new InLoSTask());
		attackSequence.addChild(BTFactory.attack(Actions.ATTACK));
		
		rootSelector.addChild(attackSequence);
		rootSelector.addChild(pursueTarget);
		rootSelector.addChild(BTFactory.patrol(1.25f));
		
		tree.addChild(rootSelector);
		goat.add(engine.createComponent(BTComponent.class).set(tree));
		
		return goat;
	}
	
	public static Entity createSpawner(float x, float y){
		EntityStats stats = EntityLoader.get(EntityIndex.SPAWNER);
		AIController controller = new AIController();
		
		Entity spawner = new EntityBuilder(SPAWNER, ENEMY)
				.physics("spawner.json", new BodyProperties.Builder().setSleepingAllowed(false).build(), x, y, false)
				.mob(controller, stats.get("health"), Float.MAX_VALUE)
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
	
	public static Entity createWings(Entity owner, float x, float y, float xOff, float yOff, Animation<TextureRegion> flapping){
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, flapping);
		Entity wings = new EntityBuilder(WINGS, Mappers.status.get(owner).status)
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

	public static Entity createBoar(float x, float y) {
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.BOAR_IDLE));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.BOAR_WALK));
		animMap.put(EntityAnim.CHARGE, assets.getAnimation(Asset.BOAR_CHARGE));
		
		AIController controller = new AIController();
		final EntityStats stats = EntityLoader.get(EntityIndex.BOAR);
		
		BodyBuilder bodyBuilder = new BodyBuilder()
				.pos(x, y)
				.type(BodyType.DynamicBody, CollisionBodyType.MOB)
				.addFixture()
					.fixtureType(FixtureType.BODY)
					.boxPixels(0, 0, 30, 20)
					.build()
				.addFixture()
					.fixtureType(FixtureType.FEET)
					.boxPixels(0, -11, 28, 4)
					.makeSensor()
					.build()
				.addFixture()
					.fixtureType(FixtureType.RIGHT_WALL)
					.boxPixels(15, 0, 2, 18)
					.makeSensor()
					.build()
				.addFixture()
					.fixtureType(FixtureType.LEFT_WALL)
					.boxPixels(-15, 0, 2, 18)
					.makeSensor()
					.build();
		
		Entity boar = new EntityBuilder(EntityType.BOAR, EntityStatus.ENEMY)
				.render(true)
				.animation(animMap)
				.physics(bodyBuilder, x, y, true)
				.mob(controller, stats.get("health"), stats.get("weight"))
				.ai(controller, new TargetComponent.DefaultTargetBehavior())
				.build();

		
		boar.add(engine.createComponent(MoneyComponent.class).set((int)stats.get("money")));
		boar.add(engine.createComponent(ImmuneComponent.class));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Boar ESM", engine, boar)
				.idle()
				.run(stats.get("ground_speed"))
				.build();
		
		final CollisionFilter chargeFilter = new CollisionFilter.Builder()
				.addBodyTypes(MOB)
				.addEntityTypes(FRIENDLY)
				.build();
		
		esm.createState(EntityStates.ATTACK_ANTICIPATION)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(DirectionComponent.class))
				.addAnimation(EntityAnim.CHARGE)
				.addTag(TransitionTag.STATIC_STATE);
		
		esm.createState(EntityStates.CHARGE)
				.add(engine.createComponent(DamageComponent.class).set(stats.get("damage")))
				.addTag(TransitionTag.STATIC_STATE)
				.addAnimation(EntityAnim.CHARGE)
				.addChangeListener(new StateChangeListener() {
					@Override
					public void onEnter(State prevState, Entity entity) {
						Mappers.facing.get(entity).locked = true;
//						Mappers.immune.get(entity).add(EffectType.KNOCKBACK);
						
						FixtureInfo info = Mappers.collisionListener.get(entity).collisionData.getFixtureInfo(FixtureType.BODY);
						info.addBehavior(chargeFilter, new DamageOnCollideBehavior(new KnockBackDef(entity, stats.get("knockback_distance"), stats.get("knockback_angle"))));
						
						final float chargeSpeed = stats.get("charge_speed");
						Mappers.timer.get(entity).add("charge", GameVars.UPS_INV, true, new TimeListener() {
							@Override
							public void onTime(Entity entity) {
								BodyComponent bodyComp = Mappers.body.get(entity);
								bodyComp.body.setLinearVelocity(Mappers.facing.get(entity).facingRight ? chargeSpeed : -chargeSpeed, bodyComp.body.getLinearVelocity().y);
							}
						});
					}

					@Override
					public void onExit(State nextState, Entity entity) {
						Mappers.facing.get(entity).locked = false;
//						Mappers.immune.get(entity).remove(EffectType.KNOCKBACK);
						Mappers.timer.get(entity).remove("charge");
						
						FixtureInfo info = Mappers.collisionListener.get(entity).collisionData.getFixtureInfo(FixtureType.BODY);
						info.removeBehaviors(chargeFilter);
					}
				});
		
		esm.createState(EntityStates.ATTACK_COOLDOWN)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(DirectionComponent.class))
				.addAnimation(EntityAnim.IDLE)
				.addTag(TransitionTag.STATIC_STATE);
		
		InputTransitionData attack = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build();

		CollisionTransitionData rightWallData = new CollisionTransitionData(CollisionType.RIGHT_WALL, true);
		CollisionTransitionData leftWallData = new CollisionTransitionData(CollisionType.LEFT_WALL, true);
		
		TimeTransitionData chargeTime = new TimeTransitionData(stats.get("charge_time"));
		MultiTransition chargeTransition = new MultiTransition(Transitions.TIME, chargeTime).or(Transitions.COLLISION, rightWallData).or(Transitions.COLLISION, leftWallData);
		
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), InputFactory.idle(), EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transitions.INPUT, InputFactory.run(), EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, attack, EntityStates.ATTACK_ANTICIPATION);
		esm.addTransition(EntityStates.ATTACK_ANTICIPATION, Transitions.TIME, new TimeTransitionData(stats.get("charge_anticipation")), EntityStates.CHARGE);
		esm.addTransition(EntityStates.CHARGE, chargeTransition, EntityStates.ATTACK_COOLDOWN);
		esm.addTransition(EntityStates.ATTACK_COOLDOWN, Transitions.TIME, new TimeTransitionData(stats.get("charge_cooldown")), EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(boar);
		
		Selector<Entity> rootSelector = new Selector<Entity>();
		
		Sequence<Entity> onPlatCanAttack = new Sequence<Entity>();
		onPlatCanAttack.addChild(new TargetOnPlatformTask());
		onPlatCanAttack.addChild(new InRangeTask(stats.get("attack_range")));
		
		Selector<Entity> attackRangeSelector = new Selector<Entity>();
		attackRangeSelector.addChild(onPlatCanAttack);
		attackRangeSelector.addChild(new InRangeTask(stats.get("close_attack_range")));
		
		Sequence<Entity> attackSequence = new Sequence<Entity>();
		attackSequence.addChild(attackRangeSelector);
		attackSequence.addChild(new InLoSTask());
		attackSequence.addChild(BTFactory.attack(Actions.ATTACK));
		
		Sequence<Entity> pursueTarget = new Sequence<Entity>();
		pursueTarget.addChild(new TargetOnPlatformTask());
		pursueTarget.addChild(BTFactory.followOnPlatform());
		
		rootSelector.addChild(attackSequence);
		rootSelector.addChild(pursueTarget);
		rootSelector.addChild(BTFactory.patrol(1.0f));
		
		tree.addChild(rootSelector);
		boar.add(engine.createComponent(BTComponent.class).set(tree));
		
		return boar;
	}
	
	public static Entity createGunGremlin(float x, float y) {
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.GUN_GREMLIN_IDLE));
		animMap.put(EntityAnim.WALK, assets.getAnimation(Asset.GUN_GREMLIN_WALK));
		animMap.put(EntityAnim.RUN, assets.getAnimation(Asset.GUN_GREMLIN_RUN));
		animMap.put(EntityAnim.JUMP, assets.getAnimation(Asset.GUN_GREMLIN_JUMP));
		animMap.put(EntityAnim.RISE, assets.getAnimation(Asset.GUN_GREMLIN_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.GUN_GREMLIN_APEX));
		animMap.put(EntityAnim.FALLING, assets.getAnimation(Asset.GUN_GREMLIN_FALL));
		animMap.put(EntityAnim.ATTACK, assets.getAnimation(Asset.GUN_GREMLIN_SHOOT));
		
		AIController controller = new AIController();
		final EntityStats stats = EntityLoader.get(EntityIndex.GUN_GREMLIN);
		NavMesh mesh = NavMesh.get(EntityIndex.GUN_GREMLIN);
		PathFinder pathFinder = new PathFinder(mesh);
		
		Rectangle hitbox = EntityIndex.GUN_GREMLIN.getHitBox();
		BodyBuilder bodyBuilder = new BodyBuilder()
				.pos(x, y)
				.type(BodyType.DynamicBody, CollisionBodyType.MOB)
				.addFixture()
					.fixtureType(FixtureType.BODY)
					.boxPixels(0, 0, (int)hitbox.width, (int)hitbox.height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.FEET)
					.boxPixels(0, -(int)(hitbox.height * 0.5f) - 1, (int)hitbox.width - 1, 4)
					.makeSensor()
					.build();
		
		Entity gremlin = new EntityBuilder(EntityType.GUN_GREMLIN, ENEMY)
				.physics(bodyBuilder, x, y, true)
				.ai(controller)
				.mob(controller, stats.get("health"), stats.get("weight"))
				.animation(animMap)
				.render(true)
				.build();

		gremlin.add(engine.createComponent(MoneyComponent.class).set((int)stats.get("money")));
		gremlin.add(engine.createComponent(PathComponent.class).set(pathFinder));
		gremlin.add(engine.createComponent(PropertyComponent.class));
		
		Mappers.property.get(gremlin).setProperty("walking", true);
		
		EntityStateMachine esm = StateFactory.createBaseBipedal(gremlin, stats);
		
		esm.getState(EntityStates.RUNNING)
			.addAnimation(EntityAnim.WALK);
		
		Mappers.timer.get(gremlin).add("run_handler", GameVars.UPS_INV, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				boolean walking = Mappers.property.get(entity).getBoolean("walking");
				AnimationStateMachine asm = Mappers.asm.get(entity).get(EntityAnim.RUN);
				if(asm != null) {
					if(asm.getCurrentState() == EntityAnim.WALK && !walking) {
						asm.changeState(EntityAnim.RUN);
					} else if(asm.getCurrentState() == EntityAnim.RUN && walking) {
						asm.changeState(EntityAnim.WALK);
					}
				}
				
				EntityStateMachine esm = Mappers.esm.get(entity).first();
				if(esm.getCurrentState() == EntityStates.RUNNING) {
					Mappers.speed.get(entity).set(walking ? stats.get("walk_speed") : stats.get("ground_speed"));
				}
			}
		});
		
		esm.createState(EntityStates.PROJECTILE_ATTACK)
				.addAnimation(EntityAnim.ATTACK)
				.addTag(TransitionTag.STATIC_STATE)
				.add(engine.createComponent(FrameMovementComponent.class).set("gun_gremlin/frames_gremlin_shoot", true))
				.addChangeListener(new StateChangeListener() {
					@Override
					public void onEnter(State prevState, Entity entity) {
						Mappers.facing.get(entity).locked = true;
						Mappers.timer.get(entity).add("shot_delay", GameVars.ANIM_FRAME * 6, false, new TimeListener() {
							@Override
							public void onTime(Entity entity) {
								EntityStateMachine esm = Mappers.esm.get(entity).first();
								if(esm.getCurrentState() == EntityStates.PROJECTILE_ATTACK) {
									// Create attack
									int numBullets = (int)stats.get("projectile_amount");
									float damage = stats.get("projectile_damage");
									float range = stats.get("projectile_range");
									float speed = stats.get("projectile_speed");
									float spread = stats.get("projectile_spread");
									
									Vector2 pos = PhysicsUtils.getPos(entity);
									float xOff = 0.0f;
									float yOff = 0.0f;
									
									float a1;
									float a2;
									float spreadFrac = spread / numBullets;
									for(int i = 0; i < numBullets; i++) {
										a1 = -i * spreadFrac + spread * 0.5f;
										a2 = -(i + 1) * spreadFrac + spread * 0.5f;
										
										float angle = MathUtils.random(a2, a1);
										
										if(!Mappers.facing.get(entity).facingRight) {
											angle = 180 - angle;
											xOff = -xOff;
										}
										
										Entity projectile = createShotgunBullet(pos.x + xOff, pos.y + yOff, speed, angle, damage, range, null, Mappers.status.get(entity).status);
										EntityManager.addEntity(projectile);
									}
								}
							}
						});
					}

					@Override
					public void onExit(State nextState, Entity entity) {
						Mappers.facing.get(entity).locked = false;
					}
				});
				
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, InputFactory.attack(), EntityStates.PROJECTILE_ATTACK);
		esm.addTransition(EntityStates.PROJECTILE_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		LeafTask<Entity> setWalking = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Mappers.property.get(getObject()).setProperty("walking", true);
				return Status.SUCCEEDED;
			}
			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return task;
			}
		};
		
		LeafTask<Entity> setRunning = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Mappers.property.get(getObject()).setProperty("walking", false);
				return Status.SUCCEEDED;
			}
			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return task;
			}
		};
		
		LeafTask<Entity> cancelPath = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Mappers.path.get(getObject()).pathFinder.reset();
				return Status.SUCCEEDED;
			}

			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return task;
			}
		};
		
		BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(gremlin);
		
		Selector<Entity> rootSelector = new Selector<Entity>();
		
		Sequence<Entity> patrolSequence = new Sequence<Entity>();
		patrolSequence.addChild(setWalking);
		patrolSequence.addChild(cancelPath);
		patrolSequence.addChild(BTFactory.patrol(1.0f));
		
		Selector<Entity> rangeSelector = new Selector<Entity>();
		
		Sequence<Entity> immediateRange = new Sequence<Entity>();
		immediateRange.addChild(new InRangeTask(stats.get("chase_range")));
		immediateRange.addChild(new InLoSTask());
		
		Sequence<Entity> platformRange = new Sequence<Entity>();
		platformRange.addChild(new TargetOnPlatformTask());
		platformRange.addChild(new InRangeTask(stats.get("platform_range")));
		platformRange.addChild(new InLoSTask());
		
		rangeSelector.addChild(immediateRange);
		rangeSelector.addChild(platformRange);
		
		Sequence<Entity> pursueTarget = new Sequence<Entity>();
		pursueTarget.addChild(rangeSelector);
		pursueTarget.addChild(new AlwaysSucceed<Entity>(new CalculatePathTask(true)));
		pursueTarget.addChild(new FollowPathTask());
		
		Selector<Entity> chaseSelector = new Selector<Entity>();
		chaseSelector.addChild(pursueTarget);
		chaseSelector.addChild(new FollowPathTask());
		
		Sequence<Entity> chaseSequence = new Sequence<Entity>();
		chaseSequence.addChild(setRunning);
		chaseSequence.addChild(chaseSelector);
		
		Sequence<Entity> attackSequence = new Sequence<Entity>();
		attackSequence.addChild(new InRangeTask(stats.get("attack_range")));
		attackSequence.addChild(new OnGroundTask());
		attackSequence.addChild(new OnTileTask(0.5f));
		attackSequence.addChild(BTFactory.attack(Actions.ATTACK));
		
		rootSelector.addChild(attackSequence);
		rootSelector.addChild(chaseSequence);
		rootSelector.addChild(patrolSequence);
		
		tree.addChild(rootSelector);
		gremlin.add(engine.createComponent(BTComponent.class).set(tree));
		
		return gremlin;
	}
	
	public static Entity createRocky(float x, float y) {
		AIController controller = new AIController();
		final EntityStats stats = EntityLoader.get(EntityIndex.ROCKY);
		
		// PERFORMANCE Scale animations on creation is not very efficient
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE,              RenderUtils.scaleAnimation(assets.getAnimation(Asset.ROCKY_IDLE),  2.0f));
		animMap.put(EntityAnim.RUN,               RenderUtils.scaleAnimation(assets.getAnimation(Asset.ROCKY_WALK),  2.0f));
		animMap.put(EntityAnim.SWING, 			  RenderUtils.scaleAnimation(assets.getAnimation(Asset.ROCKY_SWING), 2.0f));
		animMap.put(EntityAnim.PROJECTILE_ATTACK, RenderUtils.scaleAnimation(assets.getAnimation(Asset.ROCKY_THROW), 2.0f));
		
		Rectangle hitbox = EntityIndex.ROCKY.getHitBox();
		BodyBuilder builder = new BodyBuilder()
				.fixedRotation(true)
				.type(BodyType.DynamicBody, CollisionBodyType.MOB)
				.pos(x, y)
				.addFixture()
					.fixtureType(FixtureType.BODY)
					.boxPixels(0, 0, (int)hitbox.width, (int)hitbox.height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.FEET)
					.boxPixels(0, -(int)(hitbox.height * 0.5f), (int)(hitbox.width - 1), 4)
					.makeSensor()
					.build();
		
		Entity rocky = new EntityBuilder(EntityType.ROCKY, EntityStatus.ENEMY)
				.ai(controller)
				.animation(animMap)
				.render(true)
				.physics(builder, x, y, true)
				.mob(controller, stats.get("health"), stats.get("weight"), (int) stats.get("money"))
				.build();
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Rocky ESM", engine, rocky)
				.idle()
				.run(stats.get("ground_speed"))
				.build();
		
		SwingComponent swingComp = engine.createComponent(SwingComponent.class)
				.set(4.0f, 3.0f, 60, -120, GameVars.ANIM_FRAME * 7, stats.get("swing_damage"), stats.get("swing_knockback"));
		
		esm.createState(EntityStates.SWING_ATTACK)
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SWING)
			.add(engine.createComponent(NoMovementComponent.class))
			.add(swingComp)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.swing.get(entity).shouldSwing = true;
					Mappers.facing.get(entity).locked = true;
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.facing.get(entity).locked = false;
				}
			});
		
		esm.createState(EntityStates.PROJECTILE_ATTACK)
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.PROJECTILE_ATTACK)
			.add(engine.createComponent(NoMovementComponent.class))
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.timer.get(entity).add("throw_delay", 8 * GameVars.ANIM_FRAME, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							// If rocky is no longer throwing, dont throw the projectile
							if(Mappers.esm.get(entity).first().getCurrentState() != EntityStates.PROJECTILE_ATTACK) return;
						
							float damage = stats.get("rock_damage");
							float angle = stats.get("rock_angle");
							
							Entity target = Mappers.target.get(entity).target;
							if(!EntityUtils.isValid(target)) return;
							
							Vector2 to = PhysicsUtils.getPos(target);
							Vector2 from = PhysicsUtils.getPos(entity);
							
							float y = to.y - from.y;
							float x = to.x - from.x;
							
							if(x < 0) angle = 180 - angle;
							
							float cos = MathUtils.cosDeg(angle);
							float sin = MathUtils.sinDeg(angle);
							float tan = sin / cos;
							
							float g = GameVars.GRAVITY;
							float speed = (float)Math.sqrt((g * x*x)/(2*cos*cos*(y - x*tan)));
							
							float xOff = 0;
							float yOff = 28;
							
							ProjectileData data = ProjectileFactory.initProjectile(entity, xOff, yOff, angle);
							Entity rock = createRock(data.x, data.y, angle, damage, speed, EntityStatus.ENEMY);
							EntityManager.addEntity(rock);
						}
					});
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					
				}
			});
		
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), InputFactory.idle(), EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transitions.INPUT, InputFactory.run(), EntityStates.RUNNING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, InputFactory.attack(Actions.ATTACK), EntityStates.SWING_ATTACK);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, InputFactory.attack(Actions.ATTACK2), EntityStates.PROJECTILE_ATTACK);
		esm.addTransition(esm.one(EntityStates.SWING_ATTACK, EntityStates.PROJECTILE_ATTACK), Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(rocky);
	
		Selector<Entity> rootSelector = new Selector<Entity>();
		
		Sequence<Entity> pursueTarget = new Sequence<Entity>();
		pursueTarget.addChild(new TargetOnPlatformTask());
		pursueTarget.addChild(BTFactory.followOnPlatform());
		
		Sequence<Entity> throwSequence = new Sequence<Entity>();
		throwSequence.addChild(new InRangeTask(stats.get("throw_range")));
		throwSequence.addChild(new InLoSTask());
		throwSequence.addChild(BTFactory.attack(Actions.ATTACK2));
		
		Sequence<Entity> meleeSequence = new Sequence<Entity>();
		meleeSequence.addChild(new InRangeTask(stats.get("swing_range")));
		meleeSequence.addChild(new InLoSTask());
		meleeSequence.addChild(BTFactory.attack(Actions.ATTACK));
		
		rootSelector.addChild(meleeSequence);
		rootSelector.addChild(throwSequence);
		rootSelector.addChild(pursueTarget);
		rootSelector.addChild(BTFactory.patrol(1.25f));
		
		tree.addChild(rootSelector);
		rocky.add(engine.createComponent(BTComponent.class).set(tree));
		
		return rocky;
	}
	
	public static Entity createGruntGremlin(float x, float y) {
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE,      assets.getAnimation(Asset.GRUNT_GREMLIN_IDLE));
		animMap.put(EntityAnim.WALK,      assets.getAnimation(Asset.GRUNT_GREMLIN_WALK));
		animMap.put(EntityAnim.RUN,       assets.getAnimation(Asset.GRUNT_GREMLIN_RUN));
		animMap.put(EntityAnim.JUMP,      assets.getAnimation(Asset.GRUNT_GREMLIN_JUMP));
		animMap.put(EntityAnim.RISE,      assets.getAnimation(Asset.GRUNT_GREMLIN_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getAnimation(Asset.GRUNT_GREMLIN_APEX));
		animMap.put(EntityAnim.FALLING,   assets.getAnimation(Asset.GRUNT_GREMLIN_FALL));
		animMap.put(EntityAnim.ATTACK,    assets.getAnimation(Asset.GRUNT_GREMLIN_SWING));
		animMap.put(EntityAnim.TRIP,      assets.getAnimation(Asset.GRUNT_GREMLIN_TRIP));
		
		AIController controller = new AIController();
		final EntityStats stats = EntityLoader.get(EntityIndex.GRUNT_GREMLIN);
		NavMesh mesh = NavMesh.get(EntityIndex.GRUNT_GREMLIN);
		PathFinder pathFinder = new PathFinder(mesh);
		
		Rectangle hitbox = EntityIndex.GRUNT_GREMLIN.getHitBox();
		BodyBuilder bodyBuilder = new BodyBuilder()
				.pos(x, y)
				.type(BodyType.DynamicBody, CollisionBodyType.MOB)
				.addFixture()
					.fixtureType(FixtureType.BODY)
					.boxPixels(0, 0, (int)hitbox.width, (int)hitbox.height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.FEET)
					.boxPixels(0, -(int)(hitbox.height * 0.5f) - 1, (int)hitbox.width - 1, 4)
					.makeSensor()
					.build();
		
		Entity gremlin = new EntityBuilder(EntityType.GRUNT_GREMLIN, ENEMY)
				.physics(bodyBuilder, x, y, true)
				.ai(controller)
				.mob(controller, stats.get("health"), stats.get("weight"), (int) stats.get("money"))
				.animation(animMap)
				.render(true)
				.build();

		gremlin.add(engine.createComponent(PathComponent.class).set(pathFinder));
		gremlin.add(engine.createComponent(PropertyComponent.class));
		
		Mappers.property.get(gremlin).setProperty("walking", true);
		Mappers.property.get(gremlin).setProperty("should_trip", false);
		
		EntityStateMachine esm = StateFactory.createBaseBipedal(gremlin, stats);
		
		esm.getState(EntityStates.RUNNING)
			.addAnimation(EntityAnim.WALK);
		
		Mappers.timer.get(gremlin).add("run_handler", GameVars.UPS_INV, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				boolean walking = Mappers.property.get(entity).getBoolean("walking");
				AnimationStateMachine asm = Mappers.asm.get(entity).get(EntityAnim.RUN);
				if(asm != null) {
					if(asm.getCurrentState() == EntityAnim.WALK && !walking) {
						asm.changeState(EntityAnim.RUN);
					} else if(asm.getCurrentState() == EntityAnim.RUN && walking) {
						asm.changeState(EntityAnim.WALK);
					}
				}
				
				EntityStateMachine esm = Mappers.esm.get(entity).first();
				if(esm.getCurrentState() == EntityStates.RUNNING) {
					Mappers.speed.get(entity).set(walking ? stats.get("walk_speed") : stats.get("ground_speed"));
				}
			}
		});
		
		SwingComponent swingComp = engine.createComponent(SwingComponent.class)
				.set(2.0f, 1.0f, 90, -90, GameVars.ANIM_FRAME * 3, stats.get("swing_damage"), stats.get("swing_knockback"));
		
		esm.createState(EntityStates.SWING_ATTACK)
				.addAnimation(EntityAnim.ATTACK)
				.addTag(TransitionTag.STATIC_STATE)
				.add(engine.createComponent(NoMovementComponent.class))
				.add(swingComp)
				.addChangeListener(new StateChangeListener() {
					@Override
					public void onEnter(State prevState, Entity entity) {
						Mappers.facing.get(entity).locked = true;
						Mappers.swing.get(entity).shouldSwing = true;
					}

					@Override
					public void onExit(State nextState, Entity entity) {
						Mappers.facing.get(entity).locked = false;
					}
				});
				
		esm.createState(EntityStates.TRIP)
			.add(engine.createComponent(NoMovementComponent.class))
			.addAnimation(EntityAnim.TRIP)
			.addTag(TransitionTag.STATIC_STATE)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					AudioLocator.getAudio().playSound(Sounds.TRIP, Vector2.Zero);
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.property.get(entity).setProperty("should_trip", false);
				}
			});
		
		Mappers.death.get(gremlin).add(new DeathBehavior() {
			@Override
			public void onDeath(Entity entity) {
				AudioLocator.getAudio().playSound(Sounds.DYING, PhysicsUtils.getPos(entity));
				entity.add(engine.createComponent(RemoveComponent.class));
			}
		});
		
		Transition tripTransition = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return Mappers.property.get(entity).getBoolean("should_trip");
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Should Trip";
			}
		};
		
		Mappers.timer.get(gremlin).add("trip_handler", GameVars.UPS_INV, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				// Can't trip if not running
				if(Mappers.esm.get(entity).first().getCurrentState() != EntityStates.RUNNING) return;
				
				AnimationStateMachine asm = Mappers.asm.get(entity).get(EntityAnim.RUN);
				if(asm == null || asm.getCurrentAnimation() != EntityAnim.RUN) return;
				
				// Check to see if gremlin should trip
				if(MathUtils.random() < GameVars.UPS_INV / stats.get("seconds_per_trip")) {
					Mappers.property.get(entity).setProperty("should_trip", true);
				}
			}
		});
		
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, InputFactory.attack(), EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), tripTransition, EntityStates.TRIP);
		esm.addTransition(EntityStates.TRIP, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		LeafTask<Entity> setWalking = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Mappers.property.get(getObject()).setProperty("walking", true);
				return Status.SUCCEEDED;
			}
			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return task;
			}
		};
		
		LeafTask<Entity> setRunning = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Mappers.property.get(getObject()).setProperty("walking", false);
				return Status.SUCCEEDED;
			}
			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return task;
			}
		};
		
		LeafTask<Entity> cancelPath = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Mappers.path.get(getObject()).pathFinder.reset();
				return Status.SUCCEEDED;
			}

			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return task;
			}
		};
		
		BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(gremlin);
		
		Selector<Entity> rootSelector = new Selector<Entity>();
		
		Sequence<Entity> patrolSequence = new Sequence<Entity>();
		patrolSequence.addChild(setWalking);
		patrolSequence.addChild(cancelPath);
		patrolSequence.addChild(BTFactory.patrol(1.0f));
		
		Sequence<Entity> immediateRange = new Sequence<Entity>();
		immediateRange.addChild(new InRangeTask(stats.get("chase_range")));
		immediateRange.addChild(new InLoSTask());

		Sequence<Entity> pursueTarget = new Sequence<Entity>();
		pursueTarget.addChild(immediateRange);
		pursueTarget.addChild(new AlwaysSucceed<Entity>(new CalculatePathTask(true)));
		pursueTarget.addChild(new FollowPathTask());
		
		Selector<Entity> chaseSelector = new Selector<Entity>();
		chaseSelector.addChild(pursueTarget);
		chaseSelector.addChild(new FollowPathTask());
		
		Sequence<Entity> chaseSequence = new Sequence<Entity>();
		chaseSequence.addChild(setRunning);
		chaseSequence.addChild(chaseSelector);
		
		Sequence<Entity> attackSequence = new Sequence<Entity>();
		attackSequence.addChild(new InRangeTask(stats.get("attack_range")));
		attackSequence.addChild(new OnGroundTask());
		attackSequence.addChild(new OnTileTask(0.5f));
		attackSequence.addChild(BTFactory.attack(Actions.ATTACK));
		
		rootSelector.addChild(attackSequence);
		rootSelector.addChild(chaseSequence);
		rootSelector.addChild(patrolSequence);
		
		tree.addChild(rootSelector);
		gremlin.add(engine.createComponent(BTComponent.class).set(tree));
		
		return gremlin;
	}
	
	public static Entity createProjectileGremlin(float x, float y) {
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE,   assets.getAnimation(Asset.PROJECTILE_GREMLIN_IDLE));
		animMap.put(EntityAnim.RUN,    assets.getAnimation(Asset.PROJECTILE_GREMLIN_WALK));
		animMap.put(EntityAnim.ATTACK, assets.getAnimation(Asset.PROJECTILE_GREMLIN_SHOOT));
		
		AIController controller = new AIController();
		final EntityStats stats = EntityLoader.get(EntityIndex.PROJECTILE_GREMLIN);
		
		Rectangle hitbox = EntityIndex.PROJECTILE_GREMLIN.getHitBox();
		BodyBuilder bodyBuilder = new BodyBuilder()
				.pos(x, y)
				.type(BodyType.DynamicBody, CollisionBodyType.MOB)
				.addFixture()
					.fixtureType(FixtureType.BODY)
					.boxPixels(0, 0, (int)hitbox.width, (int)hitbox.height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.FEET)
					.boxPixels(0, -(int)(hitbox.height * 0.5f) - 1, (int)hitbox.width - 1, 4)
					.makeSensor()
					.build();
		
		Entity gremlin = new EntityBuilder(EntityType.PROJECTILE_GREMLIN, ENEMY)
				.physics(bodyBuilder, x, y, true)
				.ai(controller)
				.mob(controller, stats.get("health"), stats.get("weight"), (int) stats.get("money"))
				.animation(animMap)
				.render(true)
				.build();

		EntityStateMachine esm = StateFactory.createBaseBipedalNoJump(gremlin, stats);
		
		esm.createState(EntityStates.PROJECTILE_ATTACK)
				.addAnimation(EntityAnim.ATTACK)
				.addTag(TransitionTag.STATIC_STATE)
				.add(engine.createComponent(NoMovementComponent.class))
				.addChangeListener(new StateChangeListener() {
					@Override
					public void onEnter(State prevState, Entity entity) {
						Mappers.facing.get(entity).locked = true;
						Mappers.timer.get(entity).add("shot_delay", GameVars.ANIM_FRAME * 8, false, new TimeListener() {
							@Override
							public void onTime(Entity entity) {
								EntityStateMachine esm = Mappers.esm.get(entity).first();
								if(esm.getCurrentState() == EntityStates.PROJECTILE_ATTACK) {
									// Create attack
									TargetComponent targetComp = Mappers.target.get(entity);
									if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return;
									
									Vector2 toPos   = PhysicsUtils.getPos(targetComp.target);
									Vector2 fromPos = PhysicsUtils.getPos(entity);
									Vector2 diff = toPos.sub(fromPos);
									
									float angle = MathUtils.radDeg * MathUtils.atan2(diff.y, diff.x);
									float xOff = 0;
									float yOff = 5;
									
									ProjectileData data = ProjectileFactory.initProjectile(entity, xOff, yOff, angle);
									Entity proj = createGremlinProjectile(data.x, data.y, angle, stats.get("projectile_damage"), stats.get("projectile_speed"), Mappers.status.get(entity).status);
									EntityManager.addEntity(proj);
								}
							}
						});
					}

					@Override
					public void onExit(State nextState, Entity entity) {
						Mappers.facing.get(entity).locked = false;
					}
				});
				
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, InputFactory.attack(), EntityStates.PROJECTILE_ATTACK);
		esm.addTransition(EntityStates.PROJECTILE_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(gremlin);
		
		Selector<Entity> rootSelector = new Selector<Entity>();
		
		Sequence<Entity> patrolSequence = new Sequence<Entity>();
		patrolSequence.addChild(BTFactory.patrol(1.0f));
		
		Sequence<Entity> attackSequence = new Sequence<Entity>();
		attackSequence.addChild(new InRangeTask(stats.get("attack_range")));
		attackSequence.addChild(new InLoSTask());
		attackSequence.addChild(new OnGroundTask());
		attackSequence.addChild(BTFactory.attack(Actions.ATTACK));
		
		rootSelector.addChild(attackSequence);
		rootSelector.addChild(patrolSequence);
		
		tree.addChild(rootSelector);
		gremlin.add(engine.createComponent(BTComponent.class).set(tree));
		
		return gremlin;
	}
	
	public static Entity createBird(float x, float y) {
		AIController controller = new AIController();
		final EntityStats stats = EntityLoader.get(EntityIndex.BIRD);
		
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.FLYING, assets.getAnimation(Asset.BIRD_FLY));
		animMap.put(EntityAnim.DIVE,   assets.getAnimation(Asset.BIRD_SWOOP));
		animMap.put(EntityAnim.SLAM,   assets.getAnimation(Asset.BIRD_CRASH));
		
		Rectangle hitbox = EntityIndex.BIRD.getHitBox();
		float width = hitbox.width;
		float height = hitbox.height;
		
		int sensorWidth = 2;
		
		BodyBuilder builder = new BodyBuilder()
				.gravScale(0.0f)
				.pos(x, y)
				.type(BodyType.DynamicBody, CollisionBodyType.MOB)
				.addFixture()
					.fixtureType(FixtureType.BODY)
					.boxPixels(0, 0, (int)width, (int)height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.RIGHT_WALL)
					.makeSensor()
					.boxPixels((int)(width * 0.5f + 1), 0, sensorWidth, (int)height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.LEFT_WALL)
					.makeSensor()
					.boxPixels(-(int)(width * 0.5f + 1), 0, sensorWidth, (int)height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.CEILING_SENSOR)
					.makeSensor()
					.boxPixels(0, (int)(height * 0.5f + 1), (int)width, sensorWidth)
					.build()
				.addFixture()
					.fixtureType(FixtureType.FEET)
					.makeSensor()
					.boxPixels(0, -(int)(height * 0.5f + 1), (int)width, sensorWidth)
					.build();
		
		Entity bird = new EntityBuilder(EntityType.BIRD, EntityStatus.ENEMY)
				.ai(controller)
				.mob(controller, stats.get("health"), stats.get("weight"), (int)stats.get("money"))
				.render(true)
				.animation(animMap)
				.physics(builder, x, y, true)
				.build();
		bird.add(engine.createComponent(PropertyComponent.class));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Bird ESM", engine, bird)
				.build();
		
		Movement fly = new Movement() {
			@Override
			public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
				Vector2 vel = new Vector2();
				float speed = Mappers.speed.get(entity).maxSpeed;
				
				AIController controller = Mappers.aiController.get(entity).controller;
				float right = controller.getValue(Actions.MOVE_RIGHT);
				float left  = controller.getValue(Actions.MOVE_LEFT);
				float up 	= controller.getValue(Actions.MOVE_UP);
				float down  = controller.getValue(Actions.MOVE_DOWN);
				
				vel.x = speed * (right - left);
				vel.y = speed * (up - down);
				
				Movement bob = Mappers.controlledMovement.get(entity).getMovement(1);
				return vel.add(bob.getVelocity(entity, elapsed, delta));
			}
		};
		
		Movement bob = new Movement() {
			@Override
			public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
				// y = A sin (2*pi*f * t)
				// dy = A * [cos (2*pi*f *t) * 2*pi*f
				
				float amp = 0.25f;
				float pi2 = MathUtils.PI2;
				float freq = 1 / (GameVars.ANIM_FRAME * 7);
				
				return new Vector2(0, amp * pi2 * freq * MathUtils.sin(pi2 * freq * elapsed));
			}
		};
		
		esm.createState(EntityStates.FLYING)
			.add(engine.createComponent(SpeedComponent.class).set(stats.get("air_speed")))
			.add(engine.createComponent(ControlledMovementComponent.class).addAll(fly, bob))
			.addAnimation(EntityAnim.FLYING)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					// TEMPORARY Flip direction
					Mappers.facing.get(entity).facingRight = !Mappers.facing.get(entity).facingRight;
				}
				@Override
				public void onExit(State nextState, Entity entity) {
					
				}
			});
		
		final CollisionFilter damageOnCollideFilter = new CollisionFilter.Builder()
				.addBodyTypes(MOB)
				.addEntityTypes(FRIENDLY)
				.build();
		
		Movement dive = new Movement() {
			@Override
			public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
				boolean right = Mappers.facing.get(entity).facingRight;
				float angle = stats.get("dive_angle");
				float cos = MathUtils.cosDeg(angle);
				float sin = MathUtils.sinDeg(angle);
				float speed = Mappers.speed.get(entity).maxSpeed;
				
				return new Vector2(right ? speed * cos : -speed * cos, -speed * sin);
			}
		};

		Movement recover = new Movement() {
			@Override
			public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
//				boolean right = Mappers.facing.get(entity).facingRight;
//				
//				float deriv = 2 * elapsed; // 2t b/c func is t^2
//				float angle = (float) Math.toDegrees(Math.atan(deriv));
//				
//				angle = right ? angle : 180 - angle;
//				float speed = Mappers.speed.get(entity).maxSpeed;
//				float cos = MathUtils.cosDeg(angle);
//				float sin = MathUtils.sinDeg(angle); 
//				
//				return new Vector2(speed * cos, speed * sin);
				
				boolean right = Mappers.facing.get(entity).facingRight;
				float angle = stats.get("dive_angle");
				float cos = MathUtils.cosDeg(angle);
				float sin = MathUtils.sinDeg(angle);
				float speed = Mappers.speed.get(entity).maxSpeed;
				
				return new Vector2(right ? speed * cos : -speed * cos, speed * sin);
			}
		};
		
		esm.createState(EntityStates.DIVING)
			.add(engine.createComponent(SpeedComponent.class).set(stats.get("dive_speed")))
			.add(engine.createComponent(ControlledMovementComponent.class).set(dive))
			.add(engine.createComponent(DamageComponent.class).set(stats.get("dive_damage")))
			.addAnimation(EntityAnim.DIVE)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.facing.get(entity).locked = true;
					Mappers.immune.get(entity).add(EffectType.KNOCKBACK);
					
					CollisionData data = Mappers.collisionListener.get(entity).collisionData;
					FixtureInfo info = data.getFixtureInfo(FixtureType.BODY);
					info.addBehavior(damageOnCollideFilter, new DamageOnCollideBehavior());
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.facing.get(entity).locked = false;
					Mappers.immune.get(entity).remove(EffectType.KNOCKBACK);
					
					CollisionData data = Mappers.collisionListener.get(entity).collisionData;
					FixtureInfo info = data.getFixtureInfo(FixtureType.BODY);
					info.removeBehaviors(damageOnCollideFilter);
				}
			});
		
		esm.createState(EntityStates.RECOVER)
			.add(engine.createComponent(ControlledMovementComponent.class).set(recover))
			.add(engine.createComponent(SpeedComponent.class).set(stats.get("recover_speed")))
			.addAnimation(EntityAnim.FLYING)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.facing.get(entity).locked = true;
					Mappers.immune.get(entity).add(EffectType.KNOCKBACK);
				}
				
				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.facing.get(entity).locked = false;
					Mappers.immune.get(entity).remove(EffectType.KNOCKBACK);
				}
			});
		
		esm.createState(EntityStates.SLAM)
			.add(engine.createComponent(NoMovementComponent.class))
			.addAnimation(EntityAnim.SLAM);
		
		CollisionTransitionData leftWall  = new CollisionTransitionData(CollisionType.LEFT_WALL, true);
		CollisionTransitionData rightWall = new CollisionTransitionData(CollisionType.RIGHT_WALL, true);
		CollisionTransitionData ground    = new CollisionTransitionData(CollisionType.GROUND, true);
		CollisionTransitionData ceiling   = new CollisionTransitionData(CollisionType.CEILING, true);

		MultiTransition crashTransition = new MultiTransition(
					Transitions.COLLISION, leftWall)
				.or(Transitions.COLLISION, rightWall)
				.or(Transitions.COLLISION, ground)
				.or(Transitions.COLLISION, ceiling
		);
		
		// sin = opp / hyp
		// hyp = opp / sin
		final float diveHeight = stats.get("dive_height");
		final float diveAngle  = stats.get("dive_angle");
		final float diveSpeed  = stats.get("dive_speed");
		float hyp = diveHeight / MathUtils.sinDeg(diveAngle);
		float diveTime = hyp / diveSpeed;
		
		float recoverSpeed = stats.get("recover_speed");
		float recoverTime = hyp / recoverSpeed;
		
		TimeTransitionData diveTimeData = new TimeTransitionData(diveTime);
		TimeTransitionData recoverTimeData = new TimeTransitionData(recoverTime);
		
		esm.addTransition(EntityStates.FLYING, Transitions.INPUT, InputFactory.attack(), EntityStates.DIVING);
		esm.addTransition(EntityStates.DIVING, crashTransition, EntityStates.SLAM);
		esm.addTransition(EntityStates.DIVING, Transitions.TIME, diveTimeData, EntityStates.RECOVER);
		esm.addTransition(EntityStates.RECOVER, Transitions.TIME, recoverTimeData, EntityStates.FLYING);
		esm.addTransition(EntityStates.SLAM, Transitions.TIME, new TimeTransitionData(stats.get("crash_cooldown")), EntityStates.FLYING);

		LeafTask<Entity> calcFlyAngle = new LeafTask<Entity>() {
			@Override
			public Status execute() {
				Entity entity = getObject();
				
				TargetComponent targetComp = Mappers.target.get(entity);
				if(targetComp == null || !EntityUtils.isTargetable(targetComp.target)) return Status.FAILED;
				
				Entity target = targetComp.target;

				Vector2 targetPos = PhysicsUtils.getPos(target).add(new Vector2(0, 0.5f));
				Vector2 myPos = PhysicsUtils.getPos(entity);
				
				float ang = myPos.x > targetPos.x ? diveAngle : 180 - diveAngle;
				Vector2 offset = new Vector2(diveHeight * (MathUtils.cosDeg(ang) / MathUtils.sinDeg(ang)), diveHeight);
				Vector2 toPos = new Vector2(offset.x + targetPos.x, offset.y + targetPos.y);
				
				// Check toPos for tile collision
				Level level = Mappers.level.get(entity).level;
				
				boolean success = true;
				if(!level.isValidPoint(toPos.x, toPos.y, Mappers.body.get(entity).getAABB())) {
					ang = 180 - ang;
					offset = new Vector2(diveHeight * (MathUtils.cosDeg(ang) / MathUtils.sinDeg(ang)), diveHeight);
					toPos = new Vector2(offset.x + targetPos.x, offset.y + targetPos.y);
					success = level.isValidPoint(toPos.x, toPos.y, Mappers.body.get(entity).getAABB());
				}
				
				Mappers.property.get(entity).setProperty("fly_angle", MathUtils.radDeg * MathUtils.atan2(toPos.y - myPos.y, toPos.x - myPos.x));
				
				return success ? Status.SUCCEEDED : Status.FAILED;
			}

			@Override
			protected Task<Entity> copyTo(Task<Entity> task) {
				return task;
			}
		};
		
		RangeTest rangeTest = new InRangeTask.RangeTest() {
			@Override
			public boolean inRange(Entity me, Entity other) {
				// Check two circles
				float bigRadius = diveHeight / MathUtils.sinDeg(diveAngle);
				float smallRadius = 1.0f;
				
				float ang1 = diveAngle;
				float ang2 = 180 - diveAngle;
				float cos1 = MathUtils.cosDeg(ang1);
				float cos2 = MathUtils.cosDeg(ang2);
				float sin = MathUtils.sinDeg(ang1); // sin is the same for both
				
				Vector2 targetPos = PhysicsUtils.getPos(other).add(new Vector2(0, 0.5f));
				Vector2 myPos = PhysicsUtils.getPos(me);
				
				// First centroid
				Vector2 centroid1 = new Vector2(targetPos.x + bigRadius * cos1, targetPos.y + bigRadius * sin);
				Vector2 centroid2 = new Vector2(targetPos.x + bigRadius * cos2, targetPos.y + bigRadius * sin);
				
				if(DebugVars.RANGES_ON) {
					DebugRender.setColor(Color.PURPLE);
					DebugRender.setType(ShapeType.Line);
					DebugRender.circle(centroid1.x, centroid1.y, smallRadius);
					DebugRender.circle(centroid2.x, centroid2.y, smallRadius);
				}
				
				return inCircle(myPos, centroid1, smallRadius) || inCircle(myPos, centroid2, smallRadius);
			}
			
			private boolean inCircle(Vector2 pos, Vector2 center, float radius) {
				float dx = pos.x - center.x;
				float dy = pos.y - center.y;
				
				return (dx * dx) + (dy * dy) <= radius * radius;
			}
		};
		
		BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(bird);

		Selector<Entity> selector = new Selector<Entity>();
		
		Sequence<Entity> attackSequence = new Sequence<Entity>();
		attackSequence.addChild(new InLoSTask());
		attackSequence.addChild(new InRangeTask(rangeTest));
		attackSequence.addChild(BTFactory.turnWhenTargetBehind());
		attackSequence.addChild(new Wait<Entity>(1.0f));
		attackSequence.addChild(BTFactory.attack(Actions.ATTACK));
		
		Sequence<Entity> chaseSequence = new Sequence<Entity>();
		chaseSequence.addChild(new InLoSTask());
		chaseSequence.addChild(calcFlyAngle);
		chaseSequence.addChild(new ReleaseControlsTask());
		chaseSequence.addChild(new FlyTask(true));
		
		Sequence<Entity> doNothing = new Sequence<Entity>();
		doNothing.addChild(new ReleaseControlsTask());

		selector.addChild(attackSequence);
		selector.addChild(chaseSequence);
		selector.addChild(doNothing);
		
		tree.addChild(selector);
		
		bird.add(engine.createComponent(BTComponent.class).set(tree));
		
		esm.changeState(EntityStates.FLYING);
		
		return bird;
	}
	
	public static Entity createDrillGremiln(float x, float y) {
		AIController controller = new AIController();
		final EntityStats stats = EntityLoader.get(EntityIndex.DRILL_GREMLIN);
		
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.TUNNEL, 			   assets.getAnimation(Asset.DRILL_GREMLIN_TUNNEL));
		animMap.put(EntityAnim.SURFACE_PLUNGE,     assets.getAnimation(Asset.DRILL_GREMLIN_SURFACE_PLUNGE));
		animMap.put(EntityAnim.UNDERGROUND_PLUNGE, assets.getAnimation(Asset.DRILL_GREMLIN_UNDERGROUND_PLUNGE));
		animMap.put(EntityAnim.SURFACE, 		   assets.getAnimation(Asset.DRILL_GREMLIN_SURFACE));
		animMap.put(EntityAnim.RUN, 			   assets.getAnimation(Asset.DRILL_GREMLIN_WALK));
		animMap.put(EntityAnim.IDLE, 			   assets.getAnimation(Asset.DRILL_GREMLIN_IDLE));
		animMap.put(EntityAnim.ATTACK, 			   assets.getAnimation(Asset.DRILL_GREMLIN_ATTACK));
		
		final Rectangle hitbox = EntityIndex.DRILL_GREMLIN.getHitBox();
		BodyBuilder builder = new BodyBuilder()
				.pos(x, y)
				.type(BodyType.DynamicBody, CollisionBodyType.MOB)
				.addFixture()
					.fixtureType(FixtureType.BODY)
					.boxPixels(0, 0, (int) hitbox.width, (int) hitbox.height)
					.build()
				.addFixture()
					.fixtureType(FixtureType.FEET)
					.boxPixels(0, -(int)(hitbox.height * 0.5f), (int)hitbox.width - 1, 2)
					.makeSensor()
					.build();
				
		Entity gremlin = new EntityBuilder(EntityType.DRILL_GREMLIN, EntityStatus.ENEMY)
				.ai(controller)
				.animation(animMap)
				.mob(controller, stats.get("health"), stats.get("weight"), (int) stats.get("money"))
				.physics(builder, x, y, true)
				.render(true)
				.build();
		gremlin.add(engine.createComponent(PropertyComponent.class));
		
		EntityStateMachine esm = StateFactory.createBaseBipedalNoJump(gremlin, stats);
		
		final CollisionFilter tileFilter = new CollisionFilter.Builder()
				.addBodyTypes(CollisionBodyType.TILE)
				.allEntityTypes()
				.build();
		
		final CollisionFilter mobFilter = new CollisionFilter.Builder()
				.addBodyTypes(CollisionBodyType.MOB)
				.addEntityTypes(EntityStatus.FRIENDLY)
				.build();
		
		// Diving Calculations
		final float percentAbove = 0.75f;
		
		esm.createState(EntityStates.DIVING)
			.add(engine.createComponent(FrameMovementComponent.class))
			.addTag(TransitionTag.STATIC_STATE)
			.addAnimation(EntityAnim.SURFACE_PLUNGE)
			.addAnimation(EntityAnim.UNDERGROUND_PLUNGE)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					boolean fromGround = prevState == EntityStates.BASE_ATTACK;
					Mappers.property.get(entity).setProperty("from_ground", fromGround);
					Mappers.immune.get(entity).add(EffectType.KNOCKBACK);
					Mappers.frameMovement.get(entity).set(fromGround ? "drill_gremlin/frames_underground_plunge" : "drill_gremlin/frames_surface_plunge", false);
					
					CollisionData data = Mappers.collisionListener.get(entity).collisionData;
					FixtureInfo info = data.getFixtureInfo(FixtureType.BODY);
					info.removeAllBehaviors();
					info.addBehavior(tileFilter, new SensorBehavior());
					info.addBehavior(mobFilter, new SensorBehavior());
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.immune.get(entity).remove(EffectType.KNOCKBACK);
				}
			});
		
		esm.getState(EntityStates.DIVING)
			.getASM()
			.getState(EntityAnim.SURFACE_PLUNGE)
			.setChangeResolver(new StateChangeResolver() {
				@Override
				public State resolve(Entity entity, State oldState) {
					return Mappers.property.get(entity).getBoolean("from_ground") ? EntityAnim.UNDERGROUND_PLUNGE : EntityAnim.SURFACE_PLUNGE;
				}
			});
		
		final float aboveGround = hitbox.height * percentAbove * GameVars.PPM_INV;
		
		esm.createState(EntityStates.BASE_ATTACK)
			.addTag(TransitionTag.STATIC_STATE)
			.add(engine.createComponent(NoMovementComponent.class))
			.add(engine.createComponent(DamageComponent.class).set(stats.get("attack_damage")))
			.addAnimation(EntityAnim.SURFACE)
			.addAnimation(EntityAnim.ATTACK)
			.addAnimTransition(EntityAnim.SURFACE, Transitions.ANIMATION_FINISHED, EntityAnim.ATTACK)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					BodyComponent bodyComp = Mappers.body.get(entity);
					
					Vector2 pos = PhysicsUtils.getPos(entity);
					bodyComp.body.setTransform(pos.x, pos.y + aboveGround, 0.0f);
					
					Mappers.immune.get(entity).add(EffectType.KNOCKBACK);
					
					CollisionData data = Mappers.collisionListener.get(entity).collisionData;
					FixtureInfo info = data.getFixtureInfo(FixtureType.BODY);
					info.addBehavior(mobFilter, new DamageOnCollideBehavior(new KnockBackDef(entity, stats.get("attack_knockback"), stats.get("attack_knockback_angle")))); // TODO Add knockback
				}
				
				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.immune.get(entity).remove(EffectType.KNOCKBACK);
					
					CollisionData data = Mappers.collisionListener.get(entity).collisionData;
					FixtureInfo info = data.getFixtureInfo(FixtureType.BODY);
					info.removeBehaviors(mobFilter); 
				}
			});
		
		Movement tunnel = new Movement() {
			@Override
			public Vector2 getVelocity(Entity entity, float elapsed, float delta) {
				float speed = Mappers.speed.get(entity).maxSpeed;
				AIController controller = Mappers.aiController.get(entity).controller;
				float velX = speed * (controller.getValue(Actions.MOVE_RIGHT) - controller.getValue(Actions.MOVE_LEFT));
				
				return new Vector2(velX, 0.0f);
			}
		};
		////////////////////////////////////////////////////////////////////////////
		//    BEHAVIOR TREES
		////////////////////////////////////////////////////////////////////////////

		
		// Tunneling Behavior Tree
		final BehaviorTree<Entity> tunnelTree = new BehaviorTree<Entity>();
		tunnelTree.setObject(gremlin);
		
		Selector<Entity> tunnelSelector = new Selector<Entity>();
		
		Sequence<Entity> follow = new Sequence<Entity>();
		follow.addChild(new InStateTask(EntityStates.TUNNEL, SMType.ESM));
		follow.addChild(BTFactory.followOnPlatform(0.5f));
		
		tunnelSelector.addChild(follow);
		tunnelTree.addChild(tunnelSelector);
		
		////////////////////////////////////////////////////////////////////////////
		
		// Main Behavior Tree
		final BehaviorTree<Entity> tree = new BehaviorTree<Entity>();
		tree.setObject(gremlin);
		
		Selector<Entity> selector = new Selector<Entity>();

		Sequence<Entity> diveSequence = new Sequence<Entity>();
		diveSequence.addChild(new OnGroundTask());
		diveSequence.addChild(new TargetOnPlatformTask());
		diveSequence.addChild(new ReleaseControlsTask());
		diveSequence.addChild(new Wait<Entity>(0.1f));
		diveSequence.addChild(new AttackTask(Actions.ATTACK)); // Diving is attack 1
		
		selector.addChild(diveSequence);
		selector.addChild(BTFactory.patrol(1.0f));
		
		tree.addChild(selector);
		gremlin.add(engine.createComponent(BTComponent.class).set(tree));
		
		////////////////////////////////////////////////////////////////////////////
		
		esm.createState(EntityStates.TUNNEL)
			.addTag(TransitionTag.STATIC_STATE)
			.add(engine.createComponent(SpeedComponent.class).set(stats.get("tunnel_speed")))
			.add(engine.createComponent(ControlledMovementComponent.class).set(tunnel))
			.addAnimation(EntityAnim.TUNNEL)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.bt.get(entity).set(tunnelTree);
					Mappers.inviciblity.get(entity).add(InvincibilityType.ALL);
					Mappers.immune.get(entity).add(EffectType.KNOCKBACK);
					
					// Adjustment to account for errors
					BodyComponent bodyComp = Mappers.body.get(entity);
					Rectangle aabb = bodyComp.getAABB();
					Vector2 pos = PhysicsUtils.getPos(entity);
					
					int row = Maths.toGridCoord(pos.y - aabb.height * 0.5f - 0.05f);
					float wanted = row + 1 - aabb.height * 0.5f - 0.05f;
					
					bodyComp.body.setTransform(pos.x, wanted, 0.0f);

					EntityManager.addDelayedAction(new DelayedAction(entity) {
						@Override
						public void onAction() {
							Mappers.body.get(getEntity()).body.setGravityScale(0.0f);
						}
					});
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.bt.get(entity).set(tree);
					Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
					Mappers.immune.get(entity).remove(EffectType.KNOCKBACK);
				}
			});
		
		TimeTransitionData tunnelData = new TimeTransitionData(stats.get("tunnel_time"));
		TimeTransitionData attackData = new TimeTransitionData(stats.get("attack_time"));
		
		esm.addTransition(esm.all(TransitionTag.GROUND_STATE), Transitions.INPUT, InputFactory.attack(Actions.ATTACK), EntityStates.DIVING);
		esm.addTransition(EntityStates.DIVING, Transitions.ANIMATION_FINISHED, EntityStates.TUNNEL);
		esm.addTransition(EntityStates.TUNNEL, Transitions.TIME, tunnelData, EntityStates.BASE_ATTACK);
		esm.addTransition(EntityStates.BASE_ATTACK, Transitions.TIME, attackData, EntityStates.DIVING);
		
		// Idling / Running -> Diving
		// Diving -> Tunneling (Animation Finished)
		// Tunneling -> Attack
		// Attack -> Tunneling
		
		esm.changeState(EntityStates.IDLING);
		
		return gremlin;
	}
	
	// ---------------------------------------------
	// -                   DROPS                   -
	// -------- 	-------------------------------------
	public static Entity createCoin(float x, float y, float fx, float fy, int amount){
		Animation<TextureRegion> animation = null;
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
		Entity coin = createDrop(x, y, fx, fy, "coin.json", animation, assets.getAnimation(Asset.COIN_EXPLOSION), DropType.COIN, COIN);
		coin.add(engine.createComponent(MoneyComponent.class).set(amount));
		return coin;
	}
	
	private static Entity createDrop(float x, float y, float fx, float fy, String physicsBody, Animation<TextureRegion> dropIdle, Animation<TextureRegion> dropDisappear, DropType type, EntityType entityType){
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.DROP_IDLE, dropIdle);
		animMap.put(EntityAnim.DROP_DISAPPEAR, dropDisappear);
		
		Entity drop = new EntityBuilder(entityType, NEUTRAL)
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
	public static Entity createBullet(float speed, float angle, float x, float y, float damage, boolean isArc, EntityStatus status) {
		return new ProjectileBuilder(BULLET, status, x, y, speed, angle)
				.addDamage(damage)
				.makeArced(isArc)
				.build();
	}
	
	public static Entity createThrowingKnife(float x, float y, float speed, float angle, float damage, EntityStatus status){
		Entity knife = new ProjectileBuilder(THROWING_KNIFE, status, x, y, speed, angle)
				.addDamage(damage)
				.render(true)
				.animate(assets.getAnimation(Asset.ROGUE_THROWING_KNIFE))
				.build();
		return knife;
	}
	
	public static Entity createShotgunBullet(float x, float y, float speed, float angle, float damage, float range, TextureRegion frame, EntityStatus status) {
		return new ProjectileBuilder(BULLET, status, x, y, speed, angle)
				.addDamage(damage)
				.addTimedDeath(range / speed)
				.render(frame, false)
				.build();
	}
	
	public static Entity createHomingKnife(Vector2 fromPos, Vector2 toPos, float time, float damage, EntityStatus status){
		Entity knife = new ProjectileBuilder(HOMING_KNIFE, status, fromPos.x, fromPos.y, Vector2.dst(fromPos.x, fromPos.y, toPos.x, toPos.y) / time, MathUtils.radiansToDegrees * MathUtils.atan2(toPos.y - fromPos.y, toPos.x - fromPos.x))
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

		filter = new CollisionFilter.Builder()
				.addBodyTypes(MOB)
				.allEntityTypes()
				.build();
		
		info.addBehavior(filter, new SensorBehavior());

		data.setFixtureInfo(FixtureType.BULLET, info);
		return knife;
	}
	
	public static Entity createBoomerang(Entity parent, float x, float y, float speed, float turnSpeed, float angle, float damage, EntityStatus status){
		Entity boomerang = new ProjectileBuilder(BOOMERANG, status, "boomerang.json", x, y, speed, angle)
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
				.addEntityTypes(status.getOpposite())
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
	
	public static Entity createArrow(float x, float y, float speed, float angle, float damage, EntityStatus status) {
		Entity arrow = new ProjectileBuilder(ARROW, status, "arrow.json", x, y, speed, angle)
			.addDamage(damage)
			.render(true)
			.animate(assets.getAnimation(Asset.ROGUEG_ARROW_PROJECTILE))
			.build();
		
		CollisionData data = Mappers.collisionListener.get(arrow).collisionData;
		FixtureInfo info = new FixtureInfo();
		
		CollisionFilter filter = new CollisionFilter.Builder()
				.addBodyTypes(MOB)
				.allEntityTypes()
				.removeEntityType(status)
				.build();
		
		info.addBehavior(filter, new DamageOnCollideBehavior());
		
		filter = new CollisionFilter.Builder()
				.addBodyTypes(TILE)
				.allEntityTypes()
				.build();
		
		info.addBehavior(filter, new DeathOnCollideBehavior());
		data.setFixtureInfo(FixtureType.BULLET, info);
		return arrow;
	}
	
	public static Entity createBalloonPellet(float x, float y, float speed, float angle, float damage, EntityStatus status) {
		Entity pellet = new ProjectileBuilder(BALLOON_PELLET, status, x, y, speed, angle)
				.render(false)
				.animate(assets.getAnimation(Asset.ROGUE_BALLOON_PROJECTILE))
				.addDamage(damage)
				.addTimedDeath(4.0f / speed)
				.makeAutomaticRotatable()
				.build();
		
		return pellet;
	}
	
	private static Entity createExplosiveProjectile(float speed, float angle, float x, float y, float damage, boolean isArc, EntityStatus status, EntityType type, String physicsBody, float radius, float damageDropOffRate, float knockback, Animation<TextureRegion> init, Animation<TextureRegion> fly, Animation<TextureRegion> death){
		// CLEANUP Generic explosive projectile uses all mana bomb stuff
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.PROJECTILE_INIT, init);
		animMap.put(EntityAnim.PROJECTILE_FLY, fly);
		animMap.put(EntityAnim.PROJECTILE_DEATH, death);
		
		Entity explosive = new ProjectileBuilder(type, status, physicsBody, x, y, speed, angle)
				.render(true)
				.makeArced(isArc)
				.makeExplosive(radius, speed, damage, damageDropOffRate, knockback)
				.animate(animMap.get(EntityAnim.PROJECTILE_INIT), animMap.get(EntityAnim.PROJECTILE_FLY), animMap.get(EntityAnim.PROJECTILE_DEATH))
				.addStateMachine()
				.addDamage(damage)
				.build();
		return explosive;
	}
	
	public static Entity createManaBomb(float x, float y, float angle, float damage, float knockback, EntityStatus status){
		return createExplosiveProjectile(10.0f, angle, x, y, damage, true, status, MANA_BOMB, "mana_bomb.json", 5.0f, 0.0f, knockback,
				null, 
				new Animation<TextureRegion>(0.1f, assets.getAnimation(Asset.MANA_BOMB_EXPLOSION).getKeyFrames()[0]), 
				assets.getAnimation(Asset.MANA_BOMB_EXPLOSION));
	}
	
	public static Entity createDynamiteProjectile(float x, float y, float angle, float speed, float damage, float knockback, float radius, EntityStatus status){
		Entity dynamite = new ProjectileBuilder(DYNAMITE, status, x, y, speed, angle)
				.render(false)
				.animate(null, assets.getAnimation(Asset.ROGUE_DYNAMITE_PROJECTILE), assets.getAnimation(Asset.SMOKE_BOMB))
				.addStateMachine()
				.makeArced()
				.build();
		
		final boolean facingRight = angle < 90;
		Mappers.timer.get(dynamite).add("rotation", 0.0f, true, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.rotation.get(entity).rotation += facingRight ? -15.0f : 15.0f;
			}
		});
		
		Mappers.death.get(dynamite).add(new DeathBehavior() {
			@Override
			public void onDeath(Entity entity) {
				Mappers.rotation.get(entity).rotation = 0.0f;
				Mappers.timer.get(entity).remove("rotation");
			}
		});
		
		CollisionData data = Mappers.collisionListener.get(dynamite).collisionData;
		FixtureInfo info = new FixtureInfo();
		
		CollisionFilter filter = new CollisionFilter.Builder()
				.addBodyTypes(TILE)
				.allEntityTypes()
				.build();
		
		info.addBehaviors(filter, 
				new SpawnExplosionBehavior(radius, damage, knockback),
				new DeathOnCollideBehavior());
		
		filter = new CollisionFilter.Builder()
				.allBodyTypes()
				.removeBodyType(TILE)
				.allEntityTypes()
				.build();
		
		info.addBehavior(filter, new SensorBehavior());
		data.setFixtureInfo(FixtureType.BULLET, info);

		return dynamite;
	}
	
	public static Entity createExplosiveParticle(Entity parent, float speed, float angle, float x, float y){
		CombustibleComponent combustibleComp = Mappers.combustible.get(parent);
		Entity particle = new ProjectileBuilder(EXPLOSIVE_PARTICLE, Mappers.status.get(parent).status, "explosive_particle.json", x, y, speed, angle)
				.addTimedDeath(combustibleComp.radius / combustibleComp.speed)
				.build();
		particle.add(engine.createComponent(ParentComponent.class).set(parent));
		
		return particle;
	}
	
	public static Entity createWindParticle(Entity group, EntityStatus status, float x, float y, float distance, float speed, float angle, float damage, float knockback) {
		Entity particle = new ProjectileBuilder(EntityType.WIND_PARTICLE, status, "wind_particle.json", x, y, speed, angle)
				.addTimedDeath(distance / speed)
				.build();
		particle.add(engine.createComponent(ParentComponent.class).set(group));
		particle.add(engine.createComponent(PropertyComponent.class));
		
		PropertyComponent properties = Mappers.property.get(particle);
		properties.setProperty("damage", damage);
		properties.setProperty("knockback", knockback);
		
		Mappers.children.get(group).add(particle);
		return particle;
	}
	
	public static Entity createRock(float x, float y, float angle, float damage, float speed, EntityStatus status){
		BodyBuilder builder = new BodyBuilder()
				.pos(x, y)
				.type(BodyType.DynamicBody, CollisionBodyType.PROJECTILE)
				.fixedRotation(true)
				.addFixture()
					.fixtureType(FixtureType.BULLET)
					.circle(0, 0, 0.5f)
					.build();
		
		// PERFORMANCE HACK Creating a new texture and scaling 2x every time this method is called
		return new ProjectileBuilder(EntityType.ROCK, status, builder, x, y, speed, angle)
				.render(RenderUtils.scaleRegion(assets.getRegion(Asset.ROCKY_PROJECTILE), 2.0f), true)
				.giveAngularSpeed(300.0f)
				.addDamage(damage)
				.makeArced()
				.build();
	}
	
	public static Entity createGremlinProjectile(float x, float y, float angle, float damage, float speed, EntityStatus status) {
		BodyBuilder builder = new BodyBuilder()
				.bullet(true)
				.type(BodyType.DynamicBody, CollisionBodyType.PROJECTILE)
				.pos(x, y)
				.gravScale(0.0f)
				.addFixture()
					.fixtureType(FixtureType.BULLET)
					.circle(0, 0, 0.2f)
					.build();
		
		return new ProjectileBuilder(EntityType.GREMLIN_PROJECTILE, status, builder, x, y, speed, angle)
				.addDamage(damage)
				.render(assets.getRegion(Asset.PROJECTILE_GREMLIN_PROJECTILE), true)
				.build();
	}
	
	private static class ProjectileBuilder {
		
		private Entity projectile;
		
		public ProjectileBuilder(EntityType type, EntityStatus status, float x, float y, float speed, float angle){
			this(type, status, "bullet.json", x, y, speed, angle);
		}
		
		public ProjectileBuilder(EntityType type, EntityStatus status, BodyBuilder builder, float x, float y, float speed, float angle){
			projectile = new EntityBuilder(type, status)
					.physics(builder, x, y, false)
					.build();
			projectile.add(engine.createComponent(ProjectileComponent.class).set(x, y, speed, angle, false));
			projectile.add(engine.createComponent(StatusComponent.class).set(status).setCollideWith(status.getOpposite()));
			projectile.add(engine.createComponent(ForceComponent.class).set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle)));
		}
		
		public ProjectileBuilder(EntityType type, EntityStatus status, String physics, float x, float y, float speed, float angle){
			projectile = new EntityBuilder(type, status)
					.physics(physics, new BodyProperties.Builder().setGravityScale(0.0f).build(), x, y, false)
					.build();
			projectile.add(engine.createComponent(ProjectileComponent.class).set(x, y, speed, angle, false));
			projectile.add(engine.createComponent(StatusComponent.class).set(status).setCollideWith(status.getOpposite()));
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
					.removeEntityType(Mappers.status.get(projectile).status)
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
		
		public ProjectileBuilder animate(Animation<TextureRegion> fly){
			ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
			animMap.put(EntityAnim.PROJECTILE_FLY, fly);
			
			fly.setFrameDuration(GameVars.ANIM_FRAME * 0.5f);
			
			projectile.add(engine.createComponent(StateComponent.class).set(EntityAnim.PROJECTILE_FLY));
			projectile = new EntityBuilder(projectile).animation(animMap).build();
			return this;
		}
		
		public ProjectileBuilder animate(Animation<TextureRegion> init, Animation<TextureRegion> fly, Animation<TextureRegion> death){
			if(fly == null) throw new IllegalArgumentException("Flying animation can't be null.");
			ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
			if(init != null) animMap.put(EntityAnim.PROJECTILE_INIT, init);
			if(death != null) animMap.put(EntityAnim.PROJECTILE_DEATH, death);
			
			animMap.put(EntityAnim.PROJECTILE_FLY, fly);
			fly.setFrameDuration(GameVars.ANIM_FRAME * 0.5f);
			
			projectile = new EntityBuilder(projectile).animation(animMap).build();
			return this;
		}
		
		public ProjectileBuilder addStateMachine(){
			if(Mappers.animation.get(projectile) == null) throw new RuntimeException("Make sure you give the projectile animation capabilities before adding a state machine.");
			EntityStateMachine esm = new StateFactory.EntityStateBuilder(Mappers.entity.get(projectile) + " ESM", engine, projectile).build();
			
			ArrayMap<State, Animation<TextureRegion>> animMap = Mappers.animation.get(projectile).animations;
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
		
		public ProjectileBuilder makeAutomaticRotatable(){
			Mappers.rotation.get(projectile).automatic = true;
			return this;
		}
		
		public ProjectileBuilder giveAngularSpeed(float angularVel) {
			Mappers.rotation.get(projectile).angularVel = angularVel;
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
	// -                   MISC                    -
	// ---------------------------------------------
	public static Entity createBalloonTrap(float x, float y, int numPellets, float damagePerPellet, float speed, EntityStatus status){
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.INFLATE, assets.getAnimation(Asset.ROGUE_BALLOON_INFLATE));
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.ROGUE_BALLOON_IDLE));
		animMap.put(EntityAnim.POP, assets.getAnimation(Asset.ROGUE_BALLOON_POP));
		
		Entity balloon = new EntityBuilder(BALLOON_TRAP, NEUTRAL)
				.render(false)
				.animation(animMap)
				.physics("balloon.json", new BodyProperties.Builder().setGravityScale(0.0f).build(), x, y, false)
				.build();
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Balloon Trap ESM", engine, balloon).build();
		esm.createState(EntityStates.INIT)
			.addAnimation(EntityAnim.INFLATE);
		esm.createState(EntityStates.IDLING)
			.addAnimation(EntityAnim.IDLE)
			.add(engine.createComponent(BobComponent.class).set(0.25f, 0.25f));
		esm.createState(EntityStates.DYING)
			.addAnimation(EntityAnim.POP)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onExit(State nextState, Entity entity) {
					entity.add(new RemoveComponent());
				}
				
				@Override
				public void onEnter(State prevState, Entity entity) {
				}
			});
		
		Mappers.death.get(balloon).add(new DeathBehavior() {
			@Override
			public void onDeath(Entity entity) {
				Mappers.esm.get(entity).first().changeState(EntityStates.DYING);
				Mappers.body.get(entity).body.setActive(false);
			}
		});
		
		esm.addTransition(EntityStates.INIT, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		esm.addTransition(EntityStates.DYING, Transitions.ANIMATION_FINISHED, EntityStates.INIT);
		
		esm.changeState(EntityStates.INIT);
		
		CollisionData data = Mappers.collisionListener.get(balloon).collisionData;
		FixtureInfo info = new FixtureInfo();
		
		CollisionFilter filter = new CollisionFilter.Builder()
				.addBodyTypes(MOB)
				.allEntityTypes()
				.removeEntityType(status)
				.build();
		
		info.addBehaviors(filter, 
				new BalloonTrapBehavior(status, numPellets, damagePerPellet, speed),
				new DeathOnCollideBehavior());
		
		filter = new CollisionFilter.Builder()
				.addBodyTypes(PROJECTILE)
				.allEntityTypes()
				.build();
		
		info.addBehaviors(filter, 
				new BalloonTrapBehavior(status, numPellets, damagePerPellet, speed),
				new DeathOnCollideBehavior());
		
		data.setFixtureInfo(FixtureType.BODY, info);
		return balloon;
	}
	
	/**
	 * Represents a parent entity that ties together a group of entities and allows them to communicate through a global property map.
	 * @return
	 */
	public static Entity createGroup() {
		Entity group = new EntityBuilder(EntityType.GROUP, EntityStatus.NEUTRAL).build();
		group.add(engine.createComponent(ChildrenComponent.class));
		group.add(engine.createComponent(PropertyComponent.class));
		return group;
	}
	
	public static Entity createInstaWall(float x, int startRow, int endRow) {
		// Width and height in meters
		float width = 1;
		float height = (endRow - startRow + 1);
		
		float y = startRow + height * 0.5f;
		
		Entity wall = new EntityBuilder(EntityType.INSTA_WALL, EntityStatus.NEUTRAL)
				.render(false)
				.build();
		
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		bdef.fixedRotation = true;
		bdef.position.set(x, y);
		
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width * 0.5f, height * 0.5f);
		fdef.shape = shape;
		fdef.friction = 0.0f;
		
		Body body = PhysicsUtils.createPhysics(world, wall, bdef, fdef, CollisionBodyType.TILE, FixtureType.GROUND);
		
		wall.add(engine.createComponent(PositionComponent.class).set(x, y));
		wall.add(engine.createComponent(VelocityComponent.class));
		wall.add(engine.createComponent(BodyComponent.class).set(body));
	
		TextureRegion tile = assets.getRegion(Asset.MONK_INSTA_WALL_TILE);
		
		FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, (int)(width * GameVars.PPM), (int)(height * GameVars.PPM), false);
		SpriteBatch batch = new SpriteBatch();
		OrthographicCamera cam = new OrthographicCamera();
		cam.setToOrtho(false, fbo.getWidth(), fbo.getHeight());
		
		fbo.begin();
		
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		for(int row = startRow; row <= endRow; row++) {
			float yy = (row - startRow) * GameVars.PPM;
			
			batch.draw(tile, 0, yy);
		}
		batch.end();
		
		fbo.end();
		
		Texture texture = fbo.getColorBufferTexture();
		TextureRegion region = new TextureRegion(texture);
		region.flip(false, true);
		
		Mappers.texture.get(wall).set(region);
		
		return wall;
	}
	
	// ---------------------------------------------
	// -                EXPLOSIVES                 -
	// ---------------------------------------------
	public static Entity createExplosion(float x, float y, float radius, float damage, float knockback, EntityStatus status){
		final float SPEED = 20.0f;
		
		Entity explosion = new EntityBuilder(EXPLOSION, status).build();
		explosion.add(engine.createComponent(PositionComponent.class).set(x, y));
		EntityUtils.add(explosion, CombustibleComponent.class).set(radius, SPEED, damage, 5.0f, knockback).shouldExplode = true; // HACK speed and drop off is hardcoded
		explosion.add(engine.createComponent(StatusComponent.class).set(status).setCollideWith(status.getOpposite()));
		
		// Setup timed death after explosive particles are dead
		Mappers.timer.get(explosion).add("death", radius / SPEED, false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.death.get(entity).triggerDeath();
			}
		});
		
		return explosion;
	}
	
	public static Entity createSmoke(float x, float y, Animation<TextureRegion> smokeAnimation, boolean facing){
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, smokeAnimation);
		
		Entity smoke = new EntityBuilder(SMOKE, NEUTRAL)
				.render(facing)
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
	
	public static Entity createWind(float x, float y, Animation<TextureRegion> windAnimation, boolean facing, String frameData) {
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, windAnimation);
		
		Entity wind = new EntityBuilder(EntityType.WIND, NEUTRAL)
				.render(facing)
				.animation(animMap)
				.build();
		wind.add(engine.createComponent(StateComponent.class).set(EntityAnim.IDLE));
		wind.add(engine.createComponent(PositionComponent.class).set(x, y));
		wind.add(engine.createComponent(VelocityComponent.class));
		wind.add(engine.createComponent(FrameMovementComponent.class).set(frameData));
		
		Mappers.timer.get(wind).add("death", windAnimation.getAnimationDuration(), false, new TimeListener() {
			@Override
			public void onTime(Entity entity) {
				Mappers.death.get(entity).triggerDeath();
			}
		});
		return wind;	
	}
	
	public static Entity createFlashPowder(float x, float y, boolean facingRight) {
		Entity powder = createSmoke(x, y, assets.getAnimation(Asset.FLASH_POWDER_EXPLOSION), true);
		Mappers.facing.get(powder).facingRight = facingRight;
		return powder;
	}
	
	// ----------------------------------------------
	// -                DAMAGE TEXT                 -
	// ----------------------------------------------
	public static Entity createDamageText(String text, Color color, BitmapFont font, float x, float y, float speed){
		Entity entity = new EntityBuilder(DAMAGE_TEXT, NEUTRAL).build();
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
	public static Entity createParticle(Animation<TextureRegion> animation, float x, float y){
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.JUMP, animation);
		
		Entity entity = new EntityBuilder(PARTICLE, NEUTRAL)
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
	
	public static Entity createPoisonParticles(float x, float y) {
		ArrayMap<State, Animation<TextureRegion>> animMap = new ArrayMap<State, Animation<TextureRegion>>();
		animMap.put(EntityAnim.IDLE, assets.getAnimation(Asset.POISON_PARTICLES));
		
		Entity entity = new EntityBuilder(PARTICLE, NEUTRAL)
				.render(null, false, null, -1)
				.animation(animMap)
				.build();
		entity.add(engine.createComponent(StateComponent.class).set(EntityAnim.IDLE));
		entity.add(engine.createComponent(PositionComponent.class).set(x, y));
		
		return entity;
	}
	
	// ---------------------------------------------
	// -                   CAMERA                  -
	// ---------------------------------------------
	public static Entity createCamera(OrthographicCamera worldCamera){
		Entity camera = new EntityBuilder(CAMERA, NEUTRAL).build();
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
		Entity tile = new EntityBuilder(BASE_TILE, NEUTRAL).build();
		tile.add(engine.createComponent(BodyComponent.class).set(body));
		return tile;
	}
	
	// ---------------------------------------------
	// -               LEVEL TRIGGER               -
	// --------------------------------------------- 
	public static Entity createLevelTrigger(float x, float y, String trigger) {
		Entity levelTrigger = new EntityFactory.EntityBuilder(LEVEL_TRIGGER, NEUTRAL)
				.physics("level_trigger.json", x, y, false)
				.build();
		levelTrigger.add(engine.createComponent(LevelSwitchComponent.class).set(trigger));
		return levelTrigger;
	}
	
	
	public static class EntityBuilder{
		private Entity entity;
		
		/**
		 * Setups base entity with Engine, World, Level, Entity, Timer, Death, and Type components.
		 * @param name
		 * @param status
		 */
		public EntityBuilder(EntityType type, EntityStatus status){
			entity = engine.createEntity();
			EntityUtils.setTargetable(entity, true);
			entity.add(engine.createComponent(EntityComponent.class).set(type, ID++));
			entity.add(engine.createComponent(EngineComponent.class).set(engine));
			entity.add(engine.createComponent(WorldComponent.class).set(world));
			entity.add(engine.createComponent(LevelComponent.class).set(level, entity));
			entity.add(engine.createComponent(TimerComponent.class));
			entity.add(engine.createComponent(DeathComponent.class));		
			entity.add(engine.createComponent(StatusComponent.class).set(status).setCollideWith(status.getOpposite()));
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
		public EntityBuilder animation(ArrayMap<State, Animation<TextureRegion>> animMap){
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
		 * Adds Body, Position, Velocity and optional Collision components.
		 * 
		 * @param builder
		 * @param x
		 * @param y
		 * @param collideable
		 * @return
		 */
		public EntityBuilder physics(BodyBuilder builder, float x, float y, boolean collideable) {
			builder.setEntity(entity);
			entity.add(engine.createComponent(BodyComponent.class).set(PhysicsUtils.createPhysicsBody(world, builder)));
			entity.add(engine.createComponent(PositionComponent.class).set(x, y));
			entity.add(engine.createComponent(VelocityComponent.class));
			if(collideable) entity.add(engine.createComponent(CollisionComponent.class));
			return this;
		}
		
		/**
		 * Adds Input, Health, Weight, Money, Effect and Invincibility components.
		 * 
		 * @param input
		 * @param health
		 * @param weight
		 * @param money
		 * @return
		 */ 
		public EntityBuilder mob(Input input, float health, float weight, int money){
			entity.add(engine.createComponent(InputComponent.class).set(input));
			entity.add(engine.createComponent(HealthComponent.class).set(health, health));
			entity.add(engine.createComponent(WeightComponent.class).set(weight));
			entity.add(engine.createComponent(MoneyComponent.class).set(money));
			entity.add(engine.createComponent(InvincibilityComponent.class));
			entity.add(engine.createComponent(EffectComponent.class));
			entity.add(engine.createComponent(ImmuneComponent.class));
			return this;
		}
		
		public EntityBuilder mob(Input input, float health, float weight){
			return mob(input, health, weight, 0);
		}
		
		/**
		 * Adds AIController and Target components (target behavior defaults to DefaultTargetBehavior).
		 * @param controller
		 * @return
		 */
		public EntityBuilder ai(AIController controller) {
			return ai(controller, null);
		}
		
		/**
		 * Adds AIController and Target components.
		 * @param controller
		 * @return
		 */
		public EntityBuilder ai(AIController controller, TargetBehavior targetBehavior) {
			entity.add(engine.createComponent(AIControllerComponent.class).set(controller));
			entity.add(engine.createComponent(TargetComponent.class).set(targetBehavior == null ? new TargetComponent.DefaultTargetBehavior() : targetBehavior));
			return this;
		}
		
		public Entity build(){
			return entity;
		}
	}
}