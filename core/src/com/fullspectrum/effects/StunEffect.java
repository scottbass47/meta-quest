package com.fullspectrum.effects;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.component.AbstractSMComponent;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.ChildrenComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.CollisionListenerComponent;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.EffectComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.EntityComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.OffsetComponent;
import com.fullspectrum.component.ParentComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.RenderLevelComponent;
import com.fullspectrum.component.RotationComponent;
import com.fullspectrum.component.ShaderComponent;
import com.fullspectrum.component.StatusComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TintComponent;
import com.fullspectrum.component.WeightComponent;
import com.fullspectrum.component.WingComponent;
import com.fullspectrum.component.WorldComponent;
import com.fullspectrum.fsm.State;
import com.fullspectrum.fsm.StateMachine;
import com.fullspectrum.fsm.StateMachineSystem;
import com.fullspectrum.fsm.StateObject;
import com.fullspectrum.shader.StunShader;

@SuppressWarnings("unchecked")
public class StunEffect extends Effect{

	private static ObjectSet<Class<? extends Component>> requiredComponents = new ObjectSet<Class<? extends Component>>();
	private ObjectSet<Component> removed;
	private Array<StateMachine<? extends State, ? extends StateObject>> removedMachines;
	private static final StunShader shader = new StunShader();
	
	static {
		requiredComponents.addAll(
				BodyComponent.class, 
				RenderComponent.class, 
				PositionComponent.class, 
				TextureComponent.class,
				KnockBackComponent.class,
				WeightComponent.class,
				HealthComponent.class,
				BarrierComponent.class,
				MoneyComponent.class,
				FacingComponent.class,
				CollisionComponent.class,
				ShaderComponent.class,
				OffsetComponent.class,
				ParentComponent.class,
				WingComponent.class,
				EffectComponent.class,
				RotationComponent.class,
				RenderLevelComponent.class,
				CollisionListenerComponent.class,
				
				// Global Components (always required)
				TimerComponent.class,
				EngineComponent.class,
				WorldComponent.class,
				LevelComponent.class,
				DeathComponent.class,
				EntityComponent.class,
				StatusComponent.class
		);
	}
	
	// ==============
	// =    STUN    =
	// ==============
	// Apply
	// ------------------
	// 1. Set enemy to stunned
	// 2. Pause timers
	// 3. Add shader
	// 4. Stop movement
	// 5. Remove state machines
	// ------------------
	// Remove
	// ------------------
	// 1. Set enemy to not be stunned
	// 2. Resume timers
	// 3. Remove shader
	// 4. Add back state machines
	
	public StunEffect(Entity toEntity, float duration) {
		this(toEntity, duration, false);
	}
	
    public StunEffect(Entity toEntity, float duration, boolean delayed) {
    	super(toEntity, duration, delayed);
		removed = new ObjectSet<Component>();
		removedMachines = new Array<StateMachine<? extends State, ? extends StateObject>>();
	}

	@Override
	protected void give() {
//		// 1. Set enemy to stunned
//		EntityUtils.setStunned(toEntity, true);
//		
//		// 2. Pause Timers
//		TimerComponent timerComp = Mappers.timer.get(toEntity);
//		for(String name : timerComp.timers.keys()){
//			if(name.contains("_effect")) continue;
//			timerComp.get(name).pause();
//		}
//		
//		// 3. Add shader
//		Mappers.shader.get(toEntity).shader = shader;
//
//		// 4. Stop movement
//		Mappers.body.get(toEntity).body.setLinearVelocity(0.0f, 0.0f);
//		
//		// 5. Remove state machines
//		if(Mappers.esm.get(toEntity) != null) {
//			removedMachines.addAll(Mappers.esm.get(toEntity).getMachines());
//		}
//		if(Mappers.asm.get(toEntity) != null) {
//			removedMachines.addAll(Mappers.asm.get(toEntity).getMachines());
//		}
//		if(Mappers.aism.get(toEntity) != null) {
//			removedMachines.addAll(Mappers.aism.get(toEntity).getMachines());
//		}
//		if(Mappers.fsm.get(toEntity) != null) {
//			removedMachines.addAll(Mappers.fsm.get(toEntity).getMachines());
//		}
//		StateMachineSystem.getInstance().removeStateMachines(removedMachines);
//		StateMachineSystem.getInstance().updateMachines();
//		
//		// HACK Can't add null keys to an object set, but fuck it
//		try {
//			removed.add(toEntity.remove(ESMComponent.class));
//			removed.add(toEntity.remove(ASMComponent.class));
//			removed.add(toEntity.remove(AISMComponent.class));
//			removed.add(toEntity.remove(FSMComponent.class));
//		} catch(Exception e ) {}
		
		boolean hasKnockback = false;
		for(int i = 0; i < toEntity.getComponents().size(); i++){
			Component comp = toEntity.getComponents().get(i);
			
			// Pause timers
			if(comp instanceof TimerComponent){
				TimerComponent timerComp = (TimerComponent) comp;
				for(String name : timerComp.timers.keys()){
					if(name.contains("_effect")) continue;
					timerComp.get(name).pause();
				}
			}
			if(comp instanceof TintComponent){
				TintComponent tintComp = (TintComponent)comp;
				tintComp.tint = Color.WHITE;
			}
			// HACK Effects don't work well together. You shouldn't have to explicitly check things like this.
			if(comp instanceof KnockBackComponent) hasKnockback = true;
			if(requiredComponents.contains(comp.getClass())) continue;
			if(comp instanceof AbstractSMComponent<?>){
				AbstractSMComponent<StateMachine<? extends State,? extends StateObject>> smComp = (AbstractSMComponent<StateMachine<? extends State,? extends StateObject>>)comp;
				removedMachines.addAll(smComp.getMachines());
				StateMachineSystem.getInstance().removeStateMachines(smComp.getMachines());
			}
			if(comp instanceof ChildrenComponent){
				ChildrenComponent childrenComp = (ChildrenComponent) comp;
				for(Entity child : childrenComp.getChildren()){
					Effects.giveImmediateStun(child, duration); // Recursively stun
				}
			}
			removed.add(toEntity.remove(comp.getClass()));
			i--;
		}
		StateMachineSystem.getInstance().updateMachines();
		if(Mappers.body.get(toEntity) != null && !hasKnockback){
			Mappers.body.get(toEntity).body.setLinearVelocity(0.0f, 0.0f);
		}
		Mappers.shader.get(toEntity).shader = shader;
	}

	@Override
	protected void cleanUp() {
//		// 1. Set enemy to not be stunned
//		EntityUtils.setStunned(toEntity, false);
//		
//		// 2. Resume timers
//		TimerComponent timerComp = Mappers.timer.get(toEntity);
//		for(int i = 0; i < timerComp.timers.size; i++){
//			timerComp.get(timerComp.timers.getKeyAt(i)).unpause();
//		}
//		
//		// 3. Remove shader
//		Mappers.shader.get(toEntity).shader = null;
//		
//		// 4. Add back state machines
//		for(Iterator<Component> iter = removed.iterator(); iter.hasNext(); ){
//			Component comp = iter.next();
//			toEntity.add(comp);
//			iter.remove();
//		}
//		StateMachineSystem.getInstance().addStateMachines(removedMachines);
//		removedMachines.clear();
//		StateMachineSystem.getInstance().updateMachines();
		
		// Add back removed components
		for(Iterator<Component> iter = removed.iterator(); iter.hasNext(); ){
			Component comp = iter.next();
			toEntity.add(comp);
			iter.remove();
		}
		
		StateMachineSystem.getInstance().addStateMachines(removedMachines);
		removedMachines.clear();
		if(Mappers.body.get(toEntity) != null) {
			Effects.giveEase(toEntity, 0.5f, 10.0f);
		}
		// Unpause timer
		TimerComponent timerComp = Mappers.timer.get(toEntity);
		for(int i = 0; i < timerComp.timers.size; i++){
			timerComp.get(timerComp.timers.getKeyAt(i)).unpause();
		}
		StateMachineSystem.getInstance().updateMachines();
		Mappers.shader.get(toEntity).shader = null;
	}

	@Override
	public EffectType getType() {
		return EffectType.STUN;
	}

	@Override
	public String getName() {
		return "stun";
	}

}
