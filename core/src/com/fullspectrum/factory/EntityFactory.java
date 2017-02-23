package com.fullspectrum.factory;

import static com.fullspectrum.game.GameVars.PPM_INV;

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
import com.fullspectrum.ability.AntiMagneticAbility;
import com.fullspectrum.ability.ManaBombAbility;
import com.fullspectrum.ai.AIBehavior;
import com.fullspectrum.ai.AIController;
import com.fullspectrum.ai.PathFinder;
import com.fullspectrum.assets.Assets;
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
import com.fullspectrum.component.BulletStatsComponent;
import com.fullspectrum.component.CameraComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.CombustibleComponent;
import com.fullspectrum.component.DamageComponent;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.DeathComponent.DeathBehavior;
import com.fullspectrum.component.DeathComponent.DefaultDeathBehavior;
import com.fullspectrum.component.DirectionComponent;
import com.fullspectrum.component.DirectionComponent.Direction;
import com.fullspectrum.component.DropComponent;
import com.fullspectrum.component.DropMovementComponent;
import com.fullspectrum.component.ESMComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.EntityComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.FlowFieldComponent;
import com.fullspectrum.component.FlowFollowComponent;
import com.fullspectrum.component.FlyingComponent;
import com.fullspectrum.component.FollowComponent;
import com.fullspectrum.component.ForceComponent;
import com.fullspectrum.component.GroundMovementComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.InvincibilityComponent;
import com.fullspectrum.component.JumpComponent;
import com.fullspectrum.component.KnightComponent;
import com.fullspectrum.component.InvincibilityComponent.InvincibilityType;
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
import com.fullspectrum.component.RogueComponent;
import com.fullspectrum.component.SpawnComponent;
import com.fullspectrum.component.SpawnerPoolComponent;
import com.fullspectrum.component.SpeedComponent;
import com.fullspectrum.component.StateComponent;
import com.fullspectrum.component.SwingComponent;
import com.fullspectrum.component.SwordComponent;
import com.fullspectrum.component.SwordStatsComponent;
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
import com.fullspectrum.entity.CoinType;
import com.fullspectrum.entity.DropType;
import com.fullspectrum.entity.EntityAnim;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.entity.EntityLoader;
import com.fullspectrum.entity.EntityManager;
import com.fullspectrum.entity.EntityStates;
import com.fullspectrum.entity.EntityStats;
import com.fullspectrum.fsm.AIState;
import com.fullspectrum.fsm.AIStateMachine;
import com.fullspectrum.fsm.AnimationStateMachine;
import com.fullspectrum.fsm.EntityStateMachine;
import com.fullspectrum.fsm.MultiTransition;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateChangeListener;
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
import com.fullspectrum.physics.BodyProperties;
import com.fullspectrum.utils.PhysicsUtils;

public class EntityFactory {

	private static Assets assets = Assets.getInstance();
	
	private EntityFactory(){}
	
//	public static Entity createPlayer(Engine engine, World world, Level level, Input input, float x, float y) {
//		// Stats
//		EntityStats playerStats = EntityLoader.get(EntityIndex.PLAYER);
//		EntityStats knightStats = EntityLoader.get(EntityIndex.KNIGHT);
//		EntityStats rogueStats = EntityLoader.get(EntityIndex.ROGUE);
//		EntityStats mageStats = EntityLoader.get(EntityIndex.MAGE);
//		
//		// Setup Animations
//		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
//		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
//		animMap.put(EntityAnim.RUNNING, assets.getSpriteAnimation(Assets.KNIGHT_RUN));
//		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.KNIGHT_JUMP));
//		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.KNIGHT_FALL));
//		animMap.put(EntityAnim.RANDOM_IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
//		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.KNIGHT_RISE));
//		animMap.put(EntityAnim.LAND, assets.getSpriteAnimation(Assets.KNIGHT_LAND));
//		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.KNIGHT_APEX));
//		animMap.put(EntityAnim.CLIMBING, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
//		animMap.put(EntityAnim.SWING, assets.getSpriteAnimation(Assets.SHADOW_PUNCH));
//		animMap.put(EntityAnim.WALL_SLIDING, assets.getSpriteAnimation(Assets.SHADOW_IDLE));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_IDLE_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_ANTICIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_ANTICIPATION));
//		animMap.put(EntityAnim.SWING_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_SWING));
//		animMap.put(EntityAnim.SWING_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_SWING));
//		animMap.put(EntityAnim.SWING_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_SWING));
//		animMap.put(EntityAnim.SWING_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_SWING));
//
//		Entity player = new EntityBuilder("player", engine, world, level)
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
//					.add(AbilityType.MANA_BOMB, assets.getSpriteAnimation(Assets.blueCoin).getKeyFrame(0.0f), mageStats.get("mana_bomb_cooldown")))
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
//	}
	
	public static Entity createKnight(Engine engine, World world, Level level, float x, float y){
		final EntityStats knightStats = EntityLoader.get(EntityIndex.KNIGHT);
		
		// Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getSpriteAnimation(Assets.KNIGHT_RUN));
		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.KNIGHT_FALL));
		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.KNIGHT_RISE));
		animMap.put(EntityAnim.LAND, assets.getSpriteAnimation(Assets.KNIGHT_LAND));
		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_ANTICIPATION));
		animMap.put(EntityAnim.SWING_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.SWING_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_SWING));
		animMap.put(EntityAnim.SWING_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_SWING));
		animMap.put(EntityAnim.SWING_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_SWING));
		
		Entity knight = new EntityBuilder("knight", engine, world, level)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, EntityType.FRIENDLY, knightStats.get("health"))
			.build();
		
		// Player Related Components
		knight.add(engine.createComponent(MoneyComponent.class));
		knight.add(engine.createComponent(PlayerComponent.class));
		knight.add(engine.createComponent(BarrierComponent.class)
				.set(knightStats.get("shield"), 
					 knightStats.get("shield"), 
					 knightStats.get("shield_rate"), 
					 knightStats.get("shield_delay")));
		knight.add(engine.createComponent(AbilityComponent.class).add(
				new AntiMagneticAbility(knightStats.get("anti_magnetic_cooldown"), 
						new InputTransitionData.Builder(Type.ALL, true).add(Actions.BLOCK).build(),
						1.0f, 
						knightStats.get("anti_magnetic_duration"))));
		knight.add(engine.createComponent(InvincibilityComponent.class));
		
		Entity sword = createSword(engine, world, level, knight, x, y, (int)knightStats.get("sword_damage"));
		
		KnightComponent knightComp = engine.createComponent(KnightComponent.class);
		
		// Setup swings
		SwingComponent swing1 = engine.createComponent(SwingComponent.class).set(1.75f, 1.0f, 120.0f, -180.0f, 0.0f);
		SwingComponent swing2 = engine.createComponent(SwingComponent.class).set(1.75f, 0.75f, 150.0f, -180.0f, 0.0f);
		SwingComponent swing3 = engine.createComponent(SwingComponent.class).set(1.75f, 1.25f, 120.0f, -120.0f, 0.0f);
		SwingComponent swing4 = engine.createComponent(SwingComponent.class).set(1.75f, 1.0f, 135.0f, -120.0f, 0.0f);
		
		// Setup attacks
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_1, EntityAnim.SWING_ANTICIPATION_1, EntityAnim.SWING_1, swing1);
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_2, EntityAnim.SWING_ANTICIPATION_2, EntityAnim.SWING_2, swing2);
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_3, EntityAnim.SWING_ANTICIPATION_3, EntityAnim.SWING_3, swing3);
		knightComp.addAttack(EntityAnim.SWING_IDLE_ANTIPATION_4, EntityAnim.SWING_ANTICIPATION_4, EntityAnim.SWING_4, swing4);
		
		knight.add(knightComp);
		knight.add(engine.createComponent(SwordComponent.class).set(sword));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Knight ESM", engine, knight)
			.idle()
			.run(knightStats.get("ground_speed"))
			.jump(knightStats.get("jump_force"), knightStats.get("air_speed"), true, true)
			.fall(knightStats.get("air_speed"), true)
			.climb(5.0f)
			.knockBack(EntityStates.IDLING)
			.build();
		
//		esm.getState(EntityStates.RUNNING)
//			.addChangeListener(new StateChangeListener() {
//				@Override
//				public void onEnter(State prevState, Entity entity) {
//					Mappers.timer.get(entity).add("run_particle_timer", 0.5f, true, new TimeListener() {
//						@Override
//						public void onTime(Entity entity) {
//							ParticleFactory.spawnRunParticle(entity);
//						}
//					});
//					Mappers.timer.get(entity).add("change_direction", GameVars.UPS_INV, true, new TimeListener() {
//						@Override
//						public void onTime(Entity entity) {
//							Input input = Mappers.input.get(entity).input;
//							boolean facingRight = Mappers.facing.get(entity).facingRight;
//							
//							if(input.isPressed(Actions.MOVE_RIGHT) && !facingRight || input.isJustPressed(Actions.MOVE_LEFT) && facingRight){
//								ParticleFactory.spawnRunParticle(entity);
//							}
//						}
//					});
//				}
//				@Override
//				public void onExit(State nextState, Entity entity) {
//					Mappers.timer.get(entity).remove("run_particle_timer");
//				}
//			});
		
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
		// INCOMPLETE Limit chaining
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
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					// Clean up some things if you are interrupted
					if(nextState != EntityStates.SWING_ATTACK){
						KnightComponent knightComp = Mappers.knight.get(entity);
						knightComp.first = true;
					}
				}
				
			});
		
		// FIXME What happens if you get put into a knockback state mid swing?
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
					
					float duration = 0.1f;
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
					Mappers.timer.get(entity).add("lunge_distance", 0.1f + GameVars.UPS_INV, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							Mappers.body.get(entity).body.setLinearVelocity(0.0f, 0.0f);
						}
					});
					
					// Add swing
					// CLEANUP Copy swing b/c when removed if the pooled engine is implemented the data will get reset
					SwingComponent currSwing = knightComp.getCurrentAttack().getSwingComp();
					Engine engine = Mappers.engine.get(entity).engine;
					entity.add(engine.createComponent(SwingComponent.class).set(currSwing.rx, currSwing.ry, currSwing.startAngle, currSwing.endAngle, currSwing.delay + GameVars.UPS_INV + 0.1f));
					Mappers.sword.get(entity).shouldSwing = true;
				
					// CLEANUP GROSS!!! 
					Mappers.timer.get(entity).add("anim_timer", 0.0f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							ESMComponent esmComp = Mappers.esm.get(entity);
							AnimationStateMachine asm = esmComp.first().getCurrentStateObject().getASM();
							asm.changeState(Mappers.knight.get(entity).getCurrentAttack().getSwingAnimation());
						}
					});
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					Mappers.body.get(entity).body.setBullet(false);
					Mappers.body.get(entity).body.setGravityScale(0.2f);
					entity.remove(SwingComponent.class);
					
					// Need to do more clean up if the chain attack is interrupted
					if(nextState != EntityStates.SWING_ANTICIPATION){
						KnightComponent knightComp = Mappers.knight.get(entity);
						knightComp.first = true;
					}
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
						Mappers.body.get(entity).body.setGravityScale(1.0f);
						Mappers.knight.get(entity).first = true;
						Mappers.inviciblity.get(entity).remove(InvincibilityType.ALL);
					}
				}
			});
		
		// SWINGING TRANSITIONS
		// ******************************************
		
		// INCOMPLETE Can only chain after successfully hitting an enemy first
		InputTransitionData attackPress = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK).build();
		MultiTransition chainAttack = new MultiTransition(Transitions.INPUT, attackPress)
				.and(new Transition() {
					// For combo attack, enemy needs to be in range 
					@Override
					public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
						LevelComponent levelComp = Mappers.level.get(entity);
						LevelHelper helper = levelComp.levelHelper;
						
						Array<Entity> entities = helper.getEntities(new EntityGrabber() {
							@Override
							public boolean validEntity(Entity me, Entity other) {
								if(Mappers.type.get(other).same(Mappers.type.get(me))) return false;
								if(Mappers.heatlh.get(other).health <= 0) return false;
								
								Body myBody = Mappers.body.get(me).body;
								Body otherBody = Mappers.body.get(other).body;
								
								// Can only chain to enemies in front of you
								FacingComponent facingComp = Mappers.facing.get(me);
								
								float myX = myBody.getPosition().x;
								float myY = myBody.getPosition().y;
								float otherX = otherBody.getPosition().x;
								float otherY = otherBody.getPosition().y;

								float minX = 0.5f;
								float maxX = 7.0f;
								float yRange = 1.5f;
								
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
						KnightComponent knightComp = Mappers.knight.get(entity);
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
		
		esm.addTransition(esm.one(TransitionTag.AIR_STATE, TransitionTag.GROUND_STATE).exclude(EntityStates.IDLING), Transitions.INPUT, attackPress, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.IDLING, Transitions.INPUT, attackPress, EntityStates.IDLE_TO_SWING);
		esm.addTransition(EntityStates.IDLE_TO_SWING, Transitions.ANIMATION_FINISHED, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ATTACK, Transitions.ANIMATION_FINISHED, EntityStates.SWING_ANTICIPATION);
		esm.addTransition(EntityStates.SWING_ANTICIPATION, chainAttack, EntityStates.SWING_ATTACK);
		esm.addTransition(EntityStates.SWING_ANTICIPATION, Transitions.TIME, new TimeTransitionData(0.3f), EntityStates.IDLING);
		
		// ******************************************
		
		esm.createState(EntityStates.LANDING)
			.addAnimation(EntityAnim.LAND);
				
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
		esm.addTransition(esm.all(TransitionTag.AIR_STATE).exclude(EntityStates.JUMPING), Transitions.LANDED, EntityStates.LANDING);
		esm.addTransition(EntityStates.LANDING, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
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
		rogueSM.createState(EntityStates.CLEAN_UP)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.facing.get(entity).locked = false;
				}

				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		rogueSM.createState(EntityStates.PROJECTILE_ATTACK)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.rogue.get(entity).doThrowingAnim = true;
					Mappers.facing.get(entity).locked = true;
					Mappers.timer.get(entity).add("delayed_throw", 0.2f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							ProjectileFactory.spawnThrowingKnife(entity, 5.0f, 0.0f, projectileSpeed, projectileDamage, 0.0f);
						}
					});
				}

				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		
		InputTransitionData attacking = new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK).build();
		InputTransitionData notAttacking = new InputTransitionData.Builder(Type.ALL, false).add(Actions.ATTACK).build();
		
		rogueSM.addTransition(rogueSM.one(EntityStates.CLEAN_UP, EntityStates.IDLING), Transitions.INPUT, attacking, EntityStates.PROJECTILE_ATTACK);
		rogueSM.addTransition(EntityStates.PROJECTILE_ATTACK, Transitions.TIME, new TimeTransitionData(1.0f), EntityStates.IDLING);
		rogueSM.addTransition(rogueSM.one(EntityStates.PROJECTILE_ATTACK, EntityStates.IDLING), Transitions.INPUT, notAttacking, EntityStates.CLEAN_UP);
		
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
	
	public static Entity createRogue(Engine engine, World world, Level level, float x, float y){
		final EntityStats rogueStats = EntityLoader.get(EntityIndex.ROGUE);
		
		// Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.KNIGHT_FALL));
		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.KNIGHT_RISE));
		animMap.put(EntityAnim.LAND, assets.getSpriteAnimation(Assets.KNIGHT_LAND));
		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.WALL_SLIDING, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_ANTICIPATION));
		animMap.put(EntityAnim.SWING_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.SWING_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_SWING));
		animMap.put(EntityAnim.SWING_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_SWING));
		animMap.put(EntityAnim.SWING_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_SWING));
		animMap.put(EntityAnim.RUN, assets.getSpriteAnimation(Assets.ROGUE_RUN));
		animMap.put(EntityAnim.RUN_ARMS, assets.getSpriteAnimation(Assets.ROGUE_RUN_ARMS));
		animMap.put(EntityAnim.RUN_THROW, assets.getSpriteAnimation(Assets.ROGUE_RUN_THROW));
		animMap.put(EntityAnim.BACK_PEDAL, assets.getSpriteAnimation(Assets.ROGUE_BACK_PEDAL));
		animMap.put(EntityAnim.BACK_PEDAL_ARMS, assets.getSpriteAnimation(Assets.ROGUE_BACK_PEDAL_ARMS));
		animMap.put(EntityAnim.BACK_PEDAL_THROW, assets.getSpriteAnimation(Assets.ROGUE_BACK_PEDAL_THROW));
		
		Entity rogue = new EntityBuilder("rogue", engine, world, level)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, EntityType.FRIENDLY, rogueStats.get("health"))
			.build();
		
		// Player Related Components
		rogue.add(engine.createComponent(MoneyComponent.class));
		rogue.add(engine.createComponent(PlayerComponent.class));
		rogue.add(engine.createComponent(BarrierComponent.class)
				.set(rogueStats.get("shield"), 
					 rogueStats.get("shield"), 
					 rogueStats.get("shield_rate"), 
					 rogueStats.get("shield_delay")));
		rogue.add(engine.createComponent(AbilityComponent.class));
//		rogue.add(engine.createComponent(TintComponent.class).set(Color.RED));
		rogue.add(engine.createComponent(RogueComponent.class));
		rogue.add(engine.createComponent(InvincibilityComponent.class));
		
		createRogueAttackMachine(rogue, rogueStats);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Rogue ESM", engine, rogue)
			.idle()
			.run(rogueStats.get("ground_speed"))
			.jump(rogueStats.get("jump_force"), rogueStats.get("air_speed"), true, true)
			.fall(rogueStats.get("air_speed"), true)
			.climb(rogueStats.get("climb_speed"))
			.wallSlide()
			.knockBack(EntityStates.IDLING)
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
		
		// RUNNING TO BACK PEDALING AND VICE VERSA TRANSITIONS
		AnimationStateMachine runningASM = esm.getState(EntityStates.RUNNING).getASM();
		runningASM.createState(EntityAnim.BACK_PEDAL);
		
		runningASM.addTransition(EntityAnim.RUN, backpedalingTransition, EntityAnim.BACK_PEDAL);
		runningASM.addTransition(EntityAnim.BACK_PEDAL, notBackpedalingTransition, EntityAnim.RUN);
		
		runningASM.setDebugName("Running ASM");
		System.out.println(runningASM.printTransitions());
		
		// ***************************
		// * Upper Body Throwing ASM *
		// ***************************
		
		AnimationStateMachine throwingASM = new AnimationStateMachine(rogue, new StateObjectCreator());
		throwingASM.createState(EntityAnim.RUN_ARMS)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					ASMComponent asmComp = Mappers.asm.get(entity);
					final AnimationStateMachine throwingASM = asmComp.get(EntityAnim.RUN_ARMS);
					final AnimationStateMachine runningASM = asmComp.get(EntityAnim.RUN);

					// CLEANUP More 0 second timers....
					Mappers.timer.get(entity).add("delayed_time_set", 0.0f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							throwingASM.setTime(runningASM.getAnimationTime());							
						}
					});
				}
				@Override
				public void onExit(State nextState, Entity entity) {
					
				}
			});
		throwingASM.createState(EntityAnim.BACK_PEDAL_ARMS)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					ASMComponent asmComp = Mappers.asm.get(entity);
					final AnimationStateMachine throwingASM = asmComp.get(EntityAnim.RUN_ARMS);
					final AnimationStateMachine runningASM = asmComp.get(EntityAnim.RUN);

					// CLEANUP More 0 second timers....
					Mappers.timer.get(entity).add("delayed_time_set", 0.0f, false, new TimeListener() {
						@Override
						public void onTime(Entity entity) {
							throwingASM.setTime(runningASM.getAnimationTime());							
						}
					});
				}
				@Override
				public void onExit(State nextState, Entity entity) {
					
				}
			});
		throwingASM.createState(EntityAnim.RUN_THROW)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
//					AnimationStateMachine asm = Mappers.asm.get(entity).get(EntityAnim.RUN_THROW);
//					System.out.println("Time After Changing: " + asm.getAnimationTime());
					Mappers.rogue.get(entity).doThrowingAnim = false;
				}
				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		throwingASM.createState(EntityAnim.BACK_PEDAL_THROW)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					Mappers.rogue.get(entity).doThrowingAnim = false;
				}
				@Override
				public void onExit(State nextState, Entity entity) {
				}
			});
		
		// Throwing transition
		Transition throwTransition = new Transition() {
			@Override
			public boolean shouldTransition(Entity entity, TransitionObject obj, float deltaTime) {
				return Mappers.rogue.get(entity).doThrowingAnim;
			}
			
			@Override
			public boolean allowMultiple() {
				return false;
			}
			
			@Override
			public String toString() {
				return "Throw";
			}
		};
		
		throwingASM.addTransition(EntityAnim.RUN_THROW, Transitions.ANIMATION_FINISHED, EntityAnim.RUN_ARMS);
		throwingASM.addTransition(EntityAnim.BACK_PEDAL_THROW, Transitions.ANIMATION_FINISHED, EntityAnim.BACK_PEDAL_ARMS);
		throwingASM.addTransition(EntityAnim.RUN_ARMS, throwTransition, EntityAnim.RUN_THROW);
		throwingASM.addTransition(EntityAnim.BACK_PEDAL_ARMS, throwTransition, EntityAnim.BACK_PEDAL_THROW);
		throwingASM.addTransition(EntityAnim.RUN_ARMS, backpedalingTransition, EntityAnim.BACK_PEDAL_ARMS);
		throwingASM.addTransition(EntityAnim.BACK_PEDAL_ARMS, notBackpedalingTransition, EntityAnim.RUN_ARMS);
		
		throwingASM.setDebugName("Throwing ASM");
		System.out.println(throwingASM.printTransitions());
		
		// Add the new animation state machine as a substate machine of the running entity state (should handle initial state change)
		esm.getState(EntityStates.RUNNING).addSubstateMachine(throwingASM);
		
		// ********************************************
		
//		esm.createState(EntityStates.WALL_JUMP)
//			.add(engine.createComponent(SpeedComponent.class).set(rogueStats.get("air_speed")))
//			.addAnimation(EntityAnim.JUMP)
//			.addAnimation(EntityAnim.RISE)
//			.addAnimTransition(EntityAnim.JUMP, Transitions.ANIMATION_FINISHED, EntityAnim.RISE)
//			.addTag(TransitionTag.AIR_STATE)
//			.addChangeListener(new StateChangeListener(){
//				@Override
//				public void onEnter(State prevState, Entity entity) {
//					EngineComponent engineComp = Mappers.engine.get(entity);
//					CollisionComponent collisionComp = Mappers.collision.get(entity);
//					FacingComponent facingComp = Mappers.facing.get(entity);
//					
//					// Get the jump force and remove the component
//					float fx = Mappers.speed.get(entity).maxSpeed;
//					float fy = 15.0f;
//					if(collisionComp.onRightWall()) fx = -fx;
//					facingComp.facingRight = Math.signum(fx) < 0 ? false : true;
//					entity.add(engineComp.engine.createComponent(ForceComponent.class).set(fx, fy));
//				}
//
//				@Override
//				public void onExit(State nextState, Entity entity) {
//				}
//			});
		
		esm.createState(EntityStates.DASH)
			.addAnimation(EntityAnim.SWING)
			.addChangeListener(new StateChangeListener(){
				@Override
				public void onEnter(State prevState, Entity entity) {
					BodyComponent bodyComp = Mappers.body.get(entity);
					FacingComponent facingComp = Mappers.facing.get(entity);
					ForceComponent forceComp = Mappers.engine.get(entity).engine.createComponent(ForceComponent.class);
					
					Body body = bodyComp.body;
					body.setLinearVelocity(body.getLinearVelocity().x, 0);
					body.setGravityScale(0.0f);
					
					// CLEANUP Dash speed should be in config file
					forceComp.set(facingComp.facingRight ? 30f : -30f, 0);
					entity.add(forceComp);
				}

				@Override
				public void onExit(State nextState, Entity entity) {
					BodyComponent bodyComp = Mappers.body.get(entity);
					Body body = bodyComp.body;
					
					body.setLinearVelocity(0.0f, 0.0f);
					body.setGravityScale(1.0f);
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
		
		MultiTransition idleTransition = new MultiTransition(Transitions.INPUT, idleData).or(Transitions.INPUT, bothData);

		InputTransitionData attackData = new InputTransitionData(Type.ALL, true);
		attackData.triggers.add(new InputTrigger(Actions.ATTACK, true));
		
		InputTransitionData dashData = new InputTransitionData.Builder(Type.ALL, true).add(Actions.MOVEMENT, true).build();
		TimeTransitionData dashTime = new TimeTransitionData(0.1f);
		
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
		esm.addTransition(EntityStates.JUMPING, Transitions.INPUT, dashData, EntityStates.DASH);
		esm.addTransition(EntityStates.DASH, Transitions.TIME, dashTime, EntityStates.FALLING);
		
		esm.changeState(EntityStates.IDLING);
		
//		esm.addTransition(EntityStates.DASH, Transitions.COLLISION, onRightWallData, EntityStates.FALLING);
//		esm.addTransition(EntityStates.DASH, Transitions.COLLISION, onLeftWallData, EntityStates.FALLING);
//		System.out.print(esm.printTransitions(false));
		return rogue;
	}
	
	public static Entity createMage(Engine engine, World world, Level level, float x, float y){
//		Entity sword = createSword(engine, world, level, player, x, y, 100);
		
		final EntityStats mageStats = EntityLoader.get(EntityIndex.MAGE);
		
		// Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.RUN, assets.getSpriteAnimation(Assets.KNIGHT_RUN));
		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.KNIGHT_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.KNIGHT_FALL));
		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.KNIGHT_RISE));
		animMap.put(EntityAnim.LAND, assets.getSpriteAnimation(Assets.KNIGHT_LAND));
		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.KNIGHT_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.WALL_SLIDING, assets.getSpriteAnimation(Assets.KNIGHT_IDLE));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_IDLE_ANTIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_IDLE_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_ANTICIPATION));
		animMap.put(EntityAnim.SWING_ANTICIPATION_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_ANTICIPATION));
		animMap.put(EntityAnim.SWING_1, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN1_SWING));
		animMap.put(EntityAnim.SWING_2, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN2_SWING));
		animMap.put(EntityAnim.SWING_3, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN3_SWING));
		animMap.put(EntityAnim.SWING_4, assets.getSpriteAnimation(Assets.KNIGHT_CHAIN4_SWING));
		
		Entity mage = new EntityBuilder("mage", engine, world, level)
			.animation(animMap)
			.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
			.physics("player.json", x, y, true)
			.mob(null, EntityType.FRIENDLY, mageStats.get("health"))
			.build();
		
		// Player Related Components
		mage.add(engine.createComponent(MoneyComponent.class));
		mage.add(engine.createComponent(PlayerComponent.class));
		mage.add(engine.createComponent(BarrierComponent.class)
				.set(mageStats.get("shield"), 
					 mageStats.get("shield"), 
					 mageStats.get("shield_rate"), 
					 mageStats.get("shield_delay")));
		mage.add(engine.createComponent(AbilityComponent.class)
			.add(new ManaBombAbility(mageStats.get("mana_bomb_cooldown"),
				 new InputTransitionData.Builder(Type.ALL, true).add(Actions.ATTACK, true).build())));
		mage.add(engine.createComponent(TintComponent.class).set(Color.PURPLE));
		mage.add(engine.createComponent(InvincibilityComponent.class));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Mage ESM", engine, mage)
			.idle()
			.run(mageStats.get("ground_speed"))
			.jump(mageStats.get("jump_force"), mageStats.get("air_speed"), true, true)
			.fall(mageStats.get("air_speed"), true)
			.climb(mageStats.get("climb_speed"))
			.knockBack(EntityStates.IDLING)
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
					ProjectileFactory.spawnExplosiveProjectile(entity, 0.0f, 5.0f, mageStats.get("mana_bomb_speed"), mageStats.get("mana_bomb_damage"), 45f, 5.0f, 5.0f);
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
	
	public static Entity createAIPlayer(Engine engine, World world, Level level, float x, float y) {
		// Stats
		EntityStats stats = EntityLoader.get(EntityIndex.AI_PLAYER);
		NavMesh mesh = NavMesh.get(EntityIndex.AI_PLAYER);
		PathFinder pathFinder = new PathFinder(mesh);
		
		// Setup Animations
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.AI_PLAYER_IDLE));
		animMap.put(EntityAnim.RUN, assets.getSpriteAnimation(Assets.AI_PLAYER_WALK));
		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.AI_PLAYER_JUMP));
		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.AI_PLAYER_FALL));
		animMap.put(EntityAnim.RANDOM_IDLE, assets.getSpriteAnimation(Assets.AI_PLAYER_IDLE));
		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.AI_PLAYER_RISE));
		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.AI_PLAYER_APEX));
		animMap.put(EntityAnim.CLIMBING, assets.getSpriteAnimation(Assets.AI_PLAYER_IDLE));
		animMap.put(EntityAnim.SWING, assets.getSpriteAnimation(Assets.AI_PLAYER_ATTACK_OVERHEAD));
		
		// Controller
		AIController controller = new AIController();
		
		// Setup Player
		Entity player = new EntityBuilder(EntityIndex.AI_PLAYER.getName(), engine, world, level)
				.animation(animMap)
				.mob(controller, EntityType.ENEMY, stats.get("health"))
				.physics("player.json", x, y, true)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0), true)
				.build();
		float baseMoney = stats.get("money");
		int money = (int)MathUtils.random(baseMoney - 0.1f * baseMoney, baseMoney + 0.1f * baseMoney);
		player.add(engine.createComponent(MoneyComponent.class).set(money));
		player.add(engine.createComponent(AIControllerComponent.class).set(controller));
		player.add(engine.createComponent(TargetComponent.class));
		player.add(engine.createComponent(PathComponent.class).set(pathFinder));

		Entity sword = createSword(engine, world, level, player, x, y, (int)stats.get("sword_damage"));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("AI Player ESM", engine, player)
			.idle()
			.run(stats.get("ground_speed"))
			.fall(stats.get("air_speed"), true)
			.jump(stats.get("jump_force"), stats.get("air_speed"), true, true)
			.climb(stats.get("climb_speed"))
			.swingAttack(sword, 2.5f, 1.0f, 150f, -90f, 0.4f)
			.knockBack(EntityStates.IDLING)
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
	
	public static Entity createSpitter(Engine engine, World world, Level level, float x, float y){
		// Stats
		final EntityStats stats = EntityLoader.get(EntityIndex.SPITTER);
		
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.spitterIdle));
		animMap.put(EntityAnim.DYING, assets.getSpriteAnimation(Assets.spitterDeath));
		animMap.put(EntityAnim.ATTACK, assets.getSpriteAnimation(Assets.spitterAttack));
		AIController controller = new AIController();
		Entity entity = new EntityBuilder(EntityIndex.SPITTER.getName(), engine, world, level)
				.animation(animMap)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
				.physics("spitter.json", new BodyProperties.Builder().setGravityScale(0.0f).build(), x, y, false)
				.mob(controller, EntityType.ENEMY, stats.get("health"))
				.build();
		
		float baseMoney = stats.get("money");
		int money = (int)MathUtils.random(baseMoney - 0.1f * baseMoney, baseMoney + 0.1f * baseMoney);
		entity.add(engine.createComponent(AIControllerComponent.class).set(controller));
		entity.add(engine.createComponent(TargetComponent.class).set(new TargetComponent.DefaultTargetBehavior(15.0f * 15.0f)));
		entity.add(engine.createComponent(MoneyComponent.class).set(money));
		entity.add(engine.createComponent(BobComponent.class).set(2.0f, 16.0f * GameVars.PPM_INV)); // 0.5f loop (2 cycles in one second), 16 pixel height
		entity.getComponent(DeathComponent.class).set(new DeathBehavior(){
			@Override
			public void onDeath(Entity entity) {
				Mappers.body.get(entity).body.setActive(false);
				Mappers.wing.get(entity).wings.add(new RemoveComponent());
				Mappers.esm.get(entity).first().changeState(EntityStates.DYING);
			}
		});
		
		Entity wings = createWings(engine, world, level, entity, x, y, -0.8f, 0.5f, assets.getSpriteAnimation(Assets.spitterWings));
		entity.add(engine.createComponent(WingComponent.class).set(wings));
		EntityManager.addEntity(wings);
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Spitter ESM", engine, entity)
			.knockBack(EntityStates.FLYING)
			.build();
		
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
	
	public static Entity createSlime(Engine engine, World world, Level level, float x, float y){
		// Stats
		final EntityStats stats = EntityLoader.get(EntityIndex.SLIME);
		
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.slimeIdle));
		animMap.put(EntityAnim.JUMP, assets.getSpriteAnimation(Assets.slimeJump));
		animMap.put(EntityAnim.RISE, assets.getSpriteAnimation(Assets.slimeRise));
		animMap.put(EntityAnim.JUMP_APEX, assets.getSpriteAnimation(Assets.slimeApex));
		animMap.put(EntityAnim.FALLING, assets.getSpriteAnimation(Assets.slimeFall));
		animMap.put(EntityAnim.LAND, assets.getSpriteAnimation(Assets.slimeLand));
		
		AIController controller = new AIController();
		
		Entity slime = new EntityBuilder(EntityIndex.SLIME.getName(), engine, world, level)
				.mob(controller, EntityType.ENEMY, stats.get("health"))
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
				.knockBack(EntityStates.IDLING)
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
	
	public static Entity createSpawner(Engine engine, World world, Level level, float x, float y){
		EntityStats stats = EntityLoader.get(EntityIndex.SPAWNER);
		AIController controller = new AIController();
		
		Entity spawner = new EntityBuilder(EntityIndex.SPAWNER.getName(), engine, world, level)
				.physics("spawner.json", new BodyProperties.Builder().setSleepingAllowed(false).build(), x, y, false)
				.mob(controller, EntityType.ENEMY, stats.get("health"))
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
	
	public static Entity createWings(Engine engine, World world, Level level, Entity owner, float x, float y, float xOff, float yOff, Animation flapping){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, flapping);
		Entity wings = new EntityBuilder("wings of " + Mappers.entity.get(owner).name, engine, world, level)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
				.animation(animMap)
				.build();
		wings.add(engine.createComponent(PositionComponent.class).set(x, y));
		wings.add(engine.createComponent(StateComponent.class).set(EntityAnim.IDLE));
		wings.add(engine.createComponent(ParentComponent.class).set(owner));
		wings.add(engine.createComponent(OffsetComponent.class).set(xOff, yOff, true));
		return wings;
	}
	
	public static Entity createSword(Engine engine, World world, Level level, Entity owner, float x, float y, int damage){
		Entity sword = new EntityBuilder("sword of " + Mappers.entity.get(owner).name, engine, world, level)
				.physics("sword.json", 
						new BodyProperties.Builder().setGravityScale(0.0f).setActive(false).build(),
						x, y, false)
				.build();
		sword.add(engine.createComponent(ParentComponent.class).set(owner));
		sword.add(engine.createComponent(OffsetComponent.class).set(16.0f * PPM_INV, 0.0f * PPM_INV, true));
		sword.add(engine.createComponent(SwordStatsComponent.class).set(damage));
		
		return sword;
	}
	
	public static Entity createCoin(Engine engine, World world, Level level, float x, float y, float fx, float fy, int amount){
		Animation animation = null;
		CoinType coinType = CoinType.getCoin(amount);
		switch(coinType){
		case BLUE:
			animation = assets.getSpriteAnimation(Assets.blueCoin);
			break;
		case GOLD:
			animation = assets.getSpriteAnimation(Assets.goldCoin);
			break;
		case SILVER:
			animation = assets.getSpriteAnimation(Assets.silverCoin);
			break;
		}
		Entity coin = createDrop(engine, world, level, x, y, fx, fy, "coin.json", animation, assets.getSpriteAnimation(Assets.disappearCoin), DropType.COIN);
		coin.add(engine.createComponent(MoneyComponent.class).set(amount));
		return coin;
	}
	
	private static Entity createDrop(Engine engine, World world, Level level, float x, float y, float fx, float fy, String physicsBody, Animation dropIdle, Animation dropDisappear, DropType type){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.DROP_IDLE, dropIdle);
		animMap.put(EntityAnim.DROP_DISAPPEAR, dropDisappear);
		
		Entity drop = new EntityBuilder(type.name().toLowerCase(), engine, world, level)
				.animation(animMap)
				.physics(physicsBody, x, y, false)
				.render(dropIdle.getKeyFrame(0), false)
				.build();
		
		drop.add(engine.createComponent(ForceComponent.class).set(fx, fy));
		drop.add(engine.createComponent(DropComponent.class).set(type));
		drop.add(engine.createComponent(TypeComponent.class).set(EntityType.NEUTRAL).setCollideWith(EntityType.FRIENDLY));

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
	
	public static Entity createProjectile(Engine engine, World world, Level level, String physicsBody, float speed, float angle, float x, float y, boolean isArc, EntityType type){
		// CLEANUP Better naming for projectiles
		Entity projectile = new EntityBuilder(physicsBody.substring(0, physicsBody.indexOf('.')), engine, world, level)
				.physics(physicsBody, new BodyProperties.Builder().setGravityScale(isArc ? 1.0f : 0.0f).build(), x, y, false)
				.build();
		projectile.add(engine.createComponent(TypeComponent.class).set(type).setCollideWith(type.getOpposite()));
		projectile.add(engine.createComponent(ForceComponent.class).set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle)));
		projectile.add(engine.createComponent(ProjectileComponent.class).set(x, y, speed, angle, isArc));
		
		return projectile;
	}
	
	public static Entity createBullet(Engine engine, World world, Level level, float speed, float angle, float x, float y, float damage, boolean isArc, EntityType type){
		Entity bullet = createProjectile(engine, world, level, "bullet.json", speed, angle, x, y, isArc, type);
		bullet.add(engine.createComponent(BulletStatsComponent.class).set(damage));
		return bullet;
	}
	
	// CLEANUP Should be an option to create bullets with animations
	public static Entity createThrowingKnife(Engine engine, World world, Level level, float speed, float angle, float x, float y, float damage, EntityType type){
		Entity knife = createBullet(engine, world, level, speed, angle, x, y, damage, false, type);
		
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.ATTACK, assets.getSpriteAnimation(Assets.ROGUE_PROJECTILE));
		
		knife = new EntityBuilder(knife)
			.render(animMap.get(EntityAnim.ATTACK).getKeyFrame(0.0f), true)
			.animation(animMap)
			.build();
		
		knife.add(engine.createComponent(StateComponent.class).set(EntityAnim.ATTACK));
		
		return knife;
	}
	
	public static Entity createSpitProjectile(Engine engine, World world, Level level, float speed, float angle, float x, float y, float damage, float airTime, EntityType type){
		Entity spit = createBullet(engine, world, level, speed, angle, x, y, damage, false, type);
		
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, assets.getSpriteAnimation(Assets.spitInit));
		animMap.put(EntityAnim.FLYING, assets.getSpriteAnimation(Assets.spitFly));
		animMap.put(EntityAnim.DYING, assets.getSpriteAnimation(Assets.spitSplash));
		
		spit = new EntityBuilder(spit)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), true)
				.animation(animMap)
				.build();
		
		spit.getComponent(TimerComponent.class).add("spit_life", airTime, false, new TimeListener(){
			@Override
			public void onTime(Entity entity) {
				DeathComponent deathComp = Mappers.death.get(entity);
				deathComp.triggerDeath();
			}
		});

		spit.getComponent(DeathComponent.class).set(new DeathBehavior() {
			@Override
			public void onDeath(Entity entity) {
				Mappers.body.get(entity).body.setActive(false);
				Mappers.esm.get(entity).first().changeState(EntityStates.DYING);
			}
		});
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Spit ESM", engine, spit).build();
		
		esm.createState(EntityStates.IDLING)
			.addAnimation(EntityAnim.IDLE);
		
		esm.createState(EntityStates.FLYING)
			.addAnimation(EntityAnim.FLYING);
		
		esm.createState(EntityStates.DYING)
			.addAnimation(EntityAnim.DYING)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
				}
				@Override
				public void onExit(State nextState, Entity entity) {
					entity.add(new RemoveComponent());
				}
			});
		
		esm.addTransition(EntityStates.IDLING, Transitions.ANIMATION_FINISHED, EntityStates.FLYING);
		esm.addTransition(EntityStates.DYING, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		return spit;
	}
	
	public static Entity createExplosiveProjectile(Engine engine, World world, Level level, float speed, float angle, float x, float y, float damage, boolean isArc, EntityType type, float radius, float damageDropOffRate){
		Entity explosive = createProjectile(engine, world, level, "explosive.json", speed, angle, x, y, isArc, type);
		
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.IDLE, new Animation(0.1f, assets.getSpriteAnimation(Assets.manaBombExplosion).getKeyFrame(0)));
		animMap.put(EntityAnim.DYING, assets.getSpriteAnimation(Assets.manaBombExplosion));
		
		explosive = new EntityBuilder(explosive)
				.render(animMap.get(EntityAnim.IDLE).getKeyFrame(0.0f), false)
				.animation(animMap)
				.build();
		explosive.add(engine.createComponent(CombustibleComponent.class).set(radius, radius * 2f, damage, damageDropOffRate));
		
		EntityStateMachine esm = new StateFactory.EntityStateBuilder("Explosive ESM", engine, explosive).build();
		
		esm.createState(EntityStates.IDLING)
			.addAnimation(EntityAnim.IDLE);
		esm.createState(EntityStates.DYING)
			.addAnimation(EntityAnim.DYING)
			.addChangeListener(new StateChangeListener() {
				@Override
				public void onEnter(State prevState, Entity entity) {
					
				}
				@Override
				public void onExit(State nextState, Entity entity) {
					entity.add(new RemoveComponent());
				}
			});
		
		esm.addTransition(EntityStates.DYING, Transitions.ANIMATION_FINISHED, EntityStates.IDLING);
		
		esm.changeState(EntityStates.IDLING);
		
		explosive.getComponent(DeathComponent.class).set(new DeathBehavior() {
			@Override
			public void onDeath(Entity entity) {
				Mappers.esm.get(entity).first().changeState(EntityStates.DYING);
				CombustibleComponent combustibleComp = Mappers.combustible.get(entity);
				float time = combustibleComp.radius / combustibleComp.speed;
				TimerComponent timerComp = Mappers.timer.get(entity);
				timerComp.add("explosive_life", time * 2f, false, new TimeListener(){
					@Override
					public void onTime(Entity entity) {
						entity.add(new RemoveComponent());
					}
				});
			}
		});
		
		return explosive;
	}
	
	public static Entity createExplosiveParticle(Engine engine, World world, Level level, Entity parent, float speed, float angle, float x, float y){
		Entity particle = createProjectile(engine, world, level, "explosive_particle.json", speed, angle, x, y, false, Mappers.type.get(parent).type);
		CombustibleComponent combustibleComp = Mappers.combustible.get(parent);
		particle.add(engine.createComponent(ParentComponent.class).set(parent));
		particle.getComponent(TimerComponent.class).add("particle_life", combustibleComp.radius / combustibleComp.speed, false, new TimeListener(){
			@Override
			public void onTime(Entity entity) {
				entity.add(new RemoveComponent());
			}
		});
		return particle;
	}
	
	public static Entity createDamageText(Engine engine, World world, Level level, String text, Color color, BitmapFont font, float x, float y, float speed){
		Entity entity = new EntityBuilder("damage text", engine, world, level).build();
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
	
	public static Entity createParticle(Engine engine, World world, Level level, Animation animation, float x, float y){
		ArrayMap<State, Animation> animMap = new ArrayMap<State, Animation>();
		animMap.put(EntityAnim.JUMP, animation);
		
		Entity entity = new EntityBuilder("particle", engine, world, level)
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
	
	public static Entity createCamera(Engine engine, World world, Level level, OrthographicCamera worldCamera){
		Entity camera = new EntityBuilder("camera", engine, world, level).build();
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
	
	public static class EntityBuilder{
		private Engine engine;
		private World world;
		private Entity entity;
		
		/**
		 * Setups base entity with Engine, World and Level components.
		 * @param engine
		 * @param world
		 * @param level
		 */
		public EntityBuilder(String name, Engine engine, World world, Level level){
			this.engine = engine;
			this.world = world;
			entity = engine.createEntity();
			entity.add(engine.createComponent(EntityComponent.class).set(name));
			entity.add(engine.createComponent(EngineComponent.class).set(engine));
			entity.add(engine.createComponent(WorldComponent.class).set(world));
			entity.add(engine.createComponent(LevelComponent.class).set(level, entity));
			entity.add(engine.createComponent(TimerComponent.class));
			entity.add(engine.createComponent(DeathComponent.class).set(new DefaultDeathBehavior()));				
		}
		
		/**
		 * Use to decorate existing entities with new components
		 * @param entity
		 */
		public EntityBuilder(Entity entity){
			this.entity = entity;
			this.engine = Mappers.engine.get(entity).engine;
		}
		
		/**
		 * Adds Render, Texture and optional Facing components.
		 * 
		 * @param frame
		 * @param facing
		 * @return
		 */
		public EntityBuilder render(TextureRegion frame, boolean facing){ 
			entity.add(engine.createComponent(RenderComponent.class));
			entity.add(engine.createComponent(TextureComponent.class).set(frame));
			if(facing) entity.add(engine.createComponent(FacingComponent.class));
			return this;
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
		 * Adds Input, Type and Health components.
		 * 
		 * @param input
		 * @param type
		 * @param health
		 * @return
		 */ 
		public EntityBuilder mob(Input input, EntityType type, float health){
			entity.add(engine.createComponent(InputComponent.class).set(input));
			entity.add(engine.createComponent(TypeComponent.class).set(type).setCollideWith(type.getOpposite()));
			entity.add(engine.createComponent(HealthComponent.class).set(health, health));
			return this;
		}
		
		public Entity build(){
			return entity;
		}
	}
}