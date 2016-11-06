package com.fullspectrum.entity;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.assets.Assets;
import com.fullspectrum.component.AIControllerComponent;
import com.fullspectrum.component.AIStateMachineComponent;
import com.fullspectrum.component.AnimationComponent;
import com.fullspectrum.component.AttackComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.FSMComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.OffsetComponent;
import com.fullspectrum.component.ParentComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.SwordComponent;
import com.fullspectrum.component.SwordStatsComponent;
import com.fullspectrum.component.TargetComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TypeComponent;
import com.fullspectrum.component.TypeComponent.EntityType;
import com.fullspectrum.component.VelocityComponent;
import com.fullspectrum.component.WanderingComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.fsm.AIState;
import com.fullspectrum.fsm.AIStateMachine;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.StateChangeListener;
import com.fullspectrum.fsm.transition.InputTransitionData;
import com.fullspectrum.fsm.transition.InputTransitionData.Type;
import com.fullspectrum.fsm.transition.InputTrigger;
import com.fullspectrum.fsm.transition.RandomTransitionData;
import com.fullspectrum.fsm.transition.RangeTransitionData;
import com.fullspectrum.fsm.transition.Transition;
import com.fullspectrum.fsm.transition.TransitionTag;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Input;
import com.fullspectrum.level.Level;
import com.fullspectrum.utils.PhysicsUtils;

public class EntityFactory {

	private static Assets assets = Assets.getInstance();
	
	private EntityFactory(){}
	
	public static Entity createPlayer(Engine engine, Level level, Input input, World world, float x, float y) {
		// Setup Player
		Entity player = engine.createEntity();
		player.add(engine.createComponent(EngineComponent.class).set(engine));
		player.add(engine.createComponent(LevelComponent.class).set(level));
		player.add(engine.createComponent(CollisionComponent.class));
		player.add(engine.createComponent(PositionComponent.class).set(x, y));
		player.add(engine.createComponent(VelocityComponent.class));
		player.add(engine.createComponent(RenderComponent.class));
		player.add(engine.createComponent(TextureComponent.class).set(assets.getSpriteAnimation(Assets.KNIGHT_IDLE).getKeyFrame(0)));
		player.add(engine.createComponent(InputComponent.class).set(input));
		player.add(engine.createComponent(FacingComponent.class));
		player.add(engine.createComponent(MoneyComponent.class));
		player.add(engine.createComponent(BodyComponent.class));
		player.add(engine.createComponent(TypeComponent.class).set(EntityType.FRIENDLY));
		player.add(engine.createComponent(HealthComponent.class).set(1000, 1000));
		player.add(engine.createComponent(WorldComponent.class).set(world));
		player.add(engine.createComponent(AnimationComponent.class)
			.addAnimation(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE))
			.addAnimation(EntityAnim.RUNNING, assets.getSpriteAnimation(Assets.KNIGHT_WALK))
			.addAnimation(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.KNIGHT_JUMP))
			.addAnimation(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.KNIGHT_FALL))
			.addAnimation(EntityAnim.RANDOM_IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE))
			.addAnimation(EntityAnim.RISE, assets.getSpriteAnimation(Assets.KNIGHT_RISE))
			.addAnimation(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.KNIGHT_APEX))
			.addAnimation(EntityAnim.OVERHEAD_ATTACK, assets.getSpriteAnimation(Assets.KNIGHT_ATTACK_OVERHEAD)));

		EntityStateMachine fsm = new EntityStateMachine(player, "body/player.json");
		fsm.setDebugName("Entity State Machine");
		fsm.createState(EntityStates.RUNNING)
				.add(engine.createComponent(SpeedComponent.class).set(8.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.RUNNING)
				.addTag(TransitionTag.GROUND_STATE);

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;

		fsm.createState(EntityStates.IDLING)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.IDLE)
				.addAnimation(EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.IDLE, Transition.RANDOM, rtd, EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.RANDOM_IDLE, Transition.ANIMATION_FINISHED, EntityAnim.IDLE)
				.addTag(TransitionTag.GROUND_STATE);

		fsm.createState(EntityStates.FALLING)
				.add(engine.createComponent(SpeedComponent.class).set(8.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.JUMP_APEX)
				.addAnimation(EntityAnim.FALLING)
				.addAnimTransition(EntityAnim.JUMP_APEX, Transition.ANIMATION_FINISHED, EntityAnim.FALLING)
				.addTag(TransitionTag.AIR_STATE);

		fsm.createState(EntityStates.DIVING)
				.add(engine.createComponent(SpeedComponent.class).set(8.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(JumpComponent.class).set(-10.0f))
				.addAnimation(EntityAnim.FALLING)
				.addTag(TransitionTag.AIR_STATE);

		fsm.createState(EntityStates.JUMPING)
				.add(engine.createComponent(SpeedComponent.class).set(8.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(JumpComponent.class).set(20f))
				.addAnimation(EntityAnim.JUMP)
				.addAnimation(EntityAnim.RISE)
				.addAnimTransition(EntityAnim.JUMP, Transition.ANIMATION_FINISHED, EntityAnim.RISE)
				.addTag(TransitionTag.AIR_STATE);
		
		Entity sword = createSword(engine, world, player, x, y, 25);
//		engine.addEntity(sword);
		
		fsm.createState(EntityStates.ATTACK)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(SwordComponent.class).set(sword))
				.add(engine.createComponent(SwingComponent.class).set(150, 210, 0.6f))
				.addAnimation(EntityAnim.OVERHEAD_ATTACK)
				.addTag(TransitionTag.GROUND_STATE)
				.addTag(TransitionTag.STATIC_STATE)
				.addChangeListener(new StateChangeListener(){
					@Override
					public void onEnter(Entity entity) {
						SwingComponent swingComp = Mappers.swing.get(entity);
						swingComp.time = 0;
						
						SwordComponent swordComp = Mappers.sword.get(entity);
						SwordStatsComponent swordStats = Mappers.swordStats.get(swordComp.sword);
						swordStats.hitEntities.clear();
						
						EngineComponent engineComp = Mappers.engine.get(entity);
						engineComp.engine.addEntity(swordComp.sword);
						
//						Mappers.body.get(swordComp.sword).body.setActive(true);
					}

					@Override
					public void onExit(Entity entity) {
						SwordComponent swordComp = Mappers.sword.get(entity);
						
						EngineComponent engineComp = Mappers.engine.get(entity);
						engineComp.engine.removeEntity(swordComp.sword);
						
						Mappers.body.get(swordComp.sword).body.setActive(false);
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

		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
		
		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));

		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		fsm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.ATTACK), Transition.INPUT, attackData, EntityStates.ATTACK);
		fsm.addTransition(EntityStates.ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
		
//		System.out.print(fsm.printTransitions());

//		fsm.disableState(EntityStates.DIVING);
		fsm.changeState(EntityStates.IDLING);

		player.add(engine.createComponent(FSMComponent.class).set(fsm));
		return player;
	}
	
	public static Entity createAIPlayer(Engine engine, Level level, AIController controller/*, PathFinder pathFinder*/, Entity toFollow, World world, float x, float y) {
		// Setup Player
		Entity player = engine.createEntity();
		player.add(engine.createComponent(EngineComponent.class).set(engine));
		player.add(engine.createComponent(LevelComponent.class).set(level));
		player.add(engine.createComponent(CollisionComponent.class));
		player.add(engine.createComponent(PositionComponent.class).set(x, y));
		player.add(engine.createComponent(VelocityComponent.class));
		player.add(engine.createComponent(RenderComponent.class));
		player.add(engine.createComponent(TextureComponent.class).set(assets.getSpriteAnimation(Assets.KNIGHT_IDLE).getKeyFrame(0)));
		player.add(engine.createComponent(InputComponent.class).set(controller));
		player.add(engine.createComponent(FacingComponent.class));
		player.add(engine.createComponent(BodyComponent.class));
		player.add(engine.createComponent(TypeComponent.class).set(EntityType.ENEMY));
		player.add(engine.createComponent(WorldComponent.class).set(world));
		player.add(engine.createComponent(HealthComponent.class).set(100, 100));
		player.add(engine.createComponent(AnimationComponent.class)
				.addAnimation(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE))
				.addAnimation(EntityAnim.RUNNING, assets.getSpriteAnimation(Assets.KNIGHT_WALK))
				.addAnimation(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.KNIGHT_JUMP))
				.addAnimation(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.KNIGHT_FALL))
				.addAnimation(EntityAnim.RANDOM_IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE))
				.addAnimation(EntityAnim.RISE, assets.getSpriteAnimation(Assets.KNIGHT_RISE))
				.addAnimation(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.KNIGHT_APEX))
				.addAnimation(EntityAnim.OVERHEAD_ATTACK, assets.getSpriteAnimation(Assets.KNIGHT_ATTACK_OVERHEAD)));
		player.add(engine.createComponent(AIControllerComponent.class).set(controller));
		player.add(engine.createComponent(TargetComponent.class).set(toFollow));

		EntityStateMachine fsm = new EntityStateMachine(player, "body/player.json");
		fsm.setDebugName("Entity State Machine");
		fsm.createState(EntityStates.RUNNING)
				.add(engine.createComponent(SpeedComponent.class).set(5.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.RUNNING)
				.addTag(TransitionTag.GROUND_STATE);

		RandomTransitionData rtd = new RandomTransitionData();
		rtd.waitTime = 4.0f;
		rtd.probability = 1.0f;

		fsm.createState(EntityStates.IDLING)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.IDLE)
				.addAnimation(EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.IDLE, Transition.RANDOM, rtd, EntityAnim.RANDOM_IDLE)
				.addAnimTransition(EntityAnim.RANDOM_IDLE, Transition.ANIMATION_FINISHED, EntityAnim.IDLE)
				.addTag(TransitionTag.GROUND_STATE);

		fsm.createState(EntityStates.FALLING)
				.add(engine.createComponent(SpeedComponent.class).set(5.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.addAnimation(EntityAnim.JUMP_APEX)
				.addAnimation(EntityAnim.FALLING)
				.addAnimTransition(EntityAnim.JUMP_APEX, Transition.ANIMATION_FINISHED, EntityAnim.FALLING)
				.addTag(TransitionTag.AIR_STATE);

		fsm.createState(EntityStates.DIVING)
				.add(engine.createComponent(SpeedComponent.class).set(5.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(JumpComponent.class).set(-10.0f))
				.addAnimation(EntityAnim.OVERHEAD_ATTACK)
				.addTag(TransitionTag.AIR_STATE);

		fsm.createState(EntityStates.JUMPING)
				.add(engine.createComponent(SpeedComponent.class).set(5.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(JumpComponent.class).set(17.5f))
				.addAnimation(EntityAnim.JUMP)
				.addAnimation(EntityAnim.RISE)
				.addAnimTransition(EntityAnim.JUMP, Transition.ANIMATION_FINISHED, EntityAnim.RISE)
				.addTag(TransitionTag.AIR_STATE);
		
		Entity sword = createSword(engine, world, player, x, y, 25);
//		engine.addEntity(sword);
		
		fsm.createState(EntityStates.ATTACK)
				.add(engine.createComponent(SpeedComponent.class).set(0.0f))
				.add(engine.createComponent(DirectionComponent.class))
				.add(engine.createComponent(GroundMovementComponent.class))
				.add(engine.createComponent(SwordComponent.class).set(sword))
				.add(engine.createComponent(SwingComponent.class).set(150, 210, 0.6f))
				.addAnimation(EntityAnim.OVERHEAD_ATTACK)
				.addTag(TransitionTag.GROUND_STATE)
				.addTag(TransitionTag.STATIC_STATE)
				.addChangeListener(new StateChangeListener(){
					@Override
					public void onEnter(Entity entity) {
						SwingComponent swingComp = Mappers.swing.get(entity);
						swingComp.time = 0;
						
						SwordComponent swordComp = Mappers.sword.get(entity);
						SwordStatsComponent swordStats = Mappers.swordStats.get(swordComp.sword);
						swordStats.hitEntities.clear();
						
						EngineComponent engineComp = Mappers.engine.get(entity);
						engineComp.engine.addEntity(swordComp.sword);
						
//						Mappers.body.get(swordComp.sword).body.setActive(true);
					}

					@Override
					public void onExit(Entity entity) {
						SwordComponent swordComp = Mappers.sword.get(entity);
						
						EngineComponent engineComp = Mappers.engine.get(entity);
						engineComp.engine.removeEntity(swordComp.sword);
						
						Mappers.body.get(swordComp.sword).body.setActive(false);
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

		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
		
		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));

		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING, TransitionTag.STATIC_STATE), Transition.INPUT, runningData, EntityStates.RUNNING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(TransitionTag.STATIC_STATE), Transition.INPUT, jumpData, EntityStates.JUMPING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.FALLING, EntityStates.FALLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
		fsm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING, TransitionTag.STATIC_STATE), Transition.INPUT, bothData, EntityStates.IDLING);
		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);
		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.ATTACK), Transition.INPUT, attackData, EntityStates.ATTACK);
		fsm.addTransition(EntityStates.ATTACK, Transition.ANIMATION_FINISHED, EntityStates.IDLING);
//		System.out.print(fsm.printTransitions());
		
//		fsm.disableState(EntityStates.DIVING);
		fsm.changeState(EntityStates.IDLING);
		
		player.add(engine.createComponent(FSMComponent.class).set(fsm));
		
		AIStateMachine aism = new AIStateMachine(player);
		aism.createState(AIState.WANDERING)
			.add(engine.createComponent(WanderingComponent.class).set(20, 1.5f));
		
		aism.createState(AIState.FOLLOWING)
			.add(engine.createComponent(FollowComponent.class).set(toFollow));
		
		aism.createState(AIState.ATTACKING)
			.add(engine.createComponent(TargetComponent.class).set(toFollow))
			.add(engine.createComponent(AttackComponent.class));
		
		RangeTransitionData wanderingToFollow = new RangeTransitionData();
		wanderingToFollow.target = toFollow;
		wanderingToFollow.distance = 15.0f;
		wanderingToFollow.inRange = true;
		wanderingToFollow.rayTrace = true;
		
		RangeTransitionData followToWandering = new RangeTransitionData();
		followToWandering.target = toFollow;
		followToWandering.distance = 15.0f;
		followToWandering.inRange = false;
		followToWandering.rayTrace = false;
		
		RangeTransitionData toAttack = new RangeTransitionData();
		toAttack.target = toFollow;
		toAttack.distance = 1.5f;
		toAttack.inRange = true;
		toAttack.rayTrace = true;
		
		RangeTransitionData fromAttack = new RangeTransitionData();
		fromAttack.target = toFollow;
		fromAttack.distance = 2.5f;
		fromAttack.inRange = false;
		fromAttack.rayTrace = false;
		
		aism.addTransition(AIState.WANDERING, Transition.RANGE, wanderingToFollow, AIState.FOLLOWING);
		aism.addTransition(AIState.FOLLOWING, Transition.RANGE, followToWandering, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.FOLLOWING, AIState.ATTACKING), Transition.INVALID_ENTITY, toFollow, AIState.WANDERING);
		aism.addTransition(aism.one(AIState.WANDERING, AIState.FOLLOWING), Transition.RANGE, toAttack, AIState.ATTACKING);
		aism.addTransition(AIState.ATTACKING, Transition.RANGE, fromAttack, AIState.FOLLOWING);
		
		aism.changeState(AIState.WANDERING);
//		aism.disableState(AIState.FOLLOWING);
		
//		System.out.println(aism.printTransitions());
		
		player.add(engine.createComponent(AIStateMachineComponent.class).set(aism));
		return player;
	}
	
	public static Entity createSword(Engine engine, World world, Entity owner, float x, float y, int damage){
		Entity sword = engine.createEntity();
		
		sword.add(engine.createComponent(EngineComponent.class).set(engine));
		sword.add(engine.createComponent(BodyComponent.class)
				.set(PhysicsUtils.createPhysicsBody(Gdx.files.internal("body/sword.json"), world, new Vector2(x, y), sword, true)));
		sword.add(engine.createComponent(ParentComponent.class).set(owner));
		sword.add(engine.createComponent(OffsetComponent.class).set(16.0f * PPM_INV, 0.0f * PPM_INV, true));
		sword.add(engine.createComponent(SwordStatsComponent.class).set(damage));
		sword.getComponent(BodyComponent.class).body.setActive(false);
		
		return sword;
	}
	
	public static Entity createCoin(Engine engine, World world, float x, float y, int amount){
		Entity coin = engine.createEntity();
		
		Animation animation = null;
		if(amount <= 10){
			animation = assets.getSpriteAnimation(Assets.silverCoin);
		}else if(amount <= 30){
			animation = assets.getSpriteAnimation(Assets.goldCoin);
		}
		else{
			animation = assets.getSpriteAnimation(Assets.blueCoin);
		}
		
		coin.add(engine.createComponent(EngineComponent.class).set(engine));
		coin.add(engine.createComponent(BodyComponent.class));
		coin.add(engine.createComponent(RenderComponent.class));
		coin.add(engine.createComponent(PositionComponent.class).set(x, y));
		coin.add(engine.createComponent(VelocityComponent.class));
		coin.add(engine.createComponent(TypeComponent.class).set(EntityType.NEUTRAL));
		coin.add(engine.createComponent(WorldComponent.class).set(world));
		coin.add(engine.createComponent(MoneyComponent.class).set(amount));
		coin.add(engine.createComponent(TextureComponent.class).set(animation.getKeyFrame(0)));
		coin.add(engine.createComponent(AnimationComponent.class)
				.addAnimation(EntityAnim.COIN_ROTATE, animation));
		
		EntityStateMachine fsm = new EntityStateMachine(coin, "body/coin.json");
		fsm.createState(EntityStates.IDLING)
			.addAnimation(EntityAnim.COIN_ROTATE);
			
		fsm.changeState(EntityStates.IDLING);
		coin.add(engine.createComponent(FSMComponent.class).set(fsm));
		return coin;
	}
	
//	public static Entity createGoblin(PooledEngine engine, Level level, AIController controller, World world, Entity toFollow, float x, float y) {
//		// Setup Player
//		Entity goblin = engine.createEntity();
	//goblin.add(engine.createComponent(EngineComponent.class).set(engine));
//		goblin.add(new LevelComponent(level));
//		goblin.add(new CollisionComponent());
//		goblin.add(new PositionComponent(x, y));
//		goblin.add(new VelocityComponent());
//		goblin.add(new RenderComponent());
//		goblin.add(new TextureComponent(GoblinAssets.animations.get(EntityAnim.IDLE).getKeyFrame(0)));
//		goblin.add(new InputComponent(controller));
//		goblin.add(new AIControllerComponent(controller));
//		goblin.add(new FacingComponent());
//		goblin.add(new BodyComponent());
//		goblin.add(new WorldComponent(world));
//		goblin.add(new AnimationComponent()
//			.addAnimation(EntityAnim.IDLE, GoblinAssets.animations.get(EntityAnim.IDLE))
//			.addAnimation(EntityAnim.RUNNING, GoblinAssets.animations.get(EntityAnim.RUNNING)));
//
//		EntityStateMachine fsm = new EntityStateMachine(goblin, "body/goblin.json");
//		EntityState runningState = fsm.createState(EntityStates.RUNNING)
//				.add(new SpeedComponent(8.0f))
//				.add(new DirectionComponent())
//				.add(new GroundMovementComponent())
//				.addAnimation(EntityAnim.RUNNING);
//		runningState.addTag(TransitionTag.GROUND_STATE);
//
//		EntityState idleState = fsm.createState(EntityStates.IDLING)
//				.add(new SpeedComponent(0.0f))
//				.add(new DirectionComponent())
//				.add(new GroundMovementComponent())
//				.addAnimation(EntityAnim.IDLE);
//		idleState.addTag(TransitionTag.GROUND_STATE);
//
//		EntityState fallingState = fsm.createState(EntityStates.FALLING)
//				.add(new SpeedComponent(8.0f))
//				.add(new DirectionComponent())
//				.add(new GroundMovementComponent())
//				.addAnimation(EntityAnim.IDLE);
//		fallingState.addTag(TransitionTag.AIR_STATE);
////
////		EntityState divingState = fsm.createState(EntityStates.DIVING)
////				.add(new SpeedComponent(5.0f))
////				.add(new DirectionComponent())
////				.add(new GroundMovementComponent())
////				.add(new JumpComponent(-20.0f))
////				.addAnimation(EntityAnim.FALLING);
////		divingState.addTag(TransitionTag.AIR_STATE);
////
//		EntityState jumpingState = fsm.createState(EntityStates.JUMPING)
//				.add(new SpeedComponent(8.0f))
//				.add(new DirectionComponent())
//				.add(new GroundMovementComponent())
//				.add(new JumpComponent(20.0f))
//				.addAnimation(EntityAnim.IDLE);
//		jumpingState.addTag(TransitionTag.AIR_STATE);
//
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
//		InputTransitionData diveData = new InputTransitionData(Type.ALL, true);
//		diveData.triggers.add(new InputTrigger(Actions.MOVE_DOWN));
//
//		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.FALLING, EntityStates.FALLING);
//		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.RUNNING), Transition.INPUT, runningData, EntityStates.RUNNING);
//		fsm.addTransition(TransitionTag.GROUND_STATE, Transition.INPUT, jumpData, EntityStates.JUMPING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING), Transition.FALLING, EntityStates.FALLING);
//		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transition.LANDED, EntityStates.IDLING);
//		fsm.addTransition(EntityStates.RUNNING, Transition.INPUT, idleData, EntityStates.IDLING);
//		fsm.addTransition(fsm.all(TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), Transition.INPUT, bothData, EntityStates.IDLING);
////		fsm.addTransition(fsm.all(TransitionTag.AIR_STATE).exclude(EntityStates.FALLING, EntityStates.DIVING), Transition.INPUT, diveData, EntityStates.DIVING);
//
////		System.out.print(fsm.printTransitions());
//
//		fsm.changeState(EntityStates.IDLING);
//
//		goblin.add(new FSMComponent(fsm));
//		return goblin;
//	}

	
	
}
