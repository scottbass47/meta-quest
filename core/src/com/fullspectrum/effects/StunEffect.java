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
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TintComponent;
import com.fullspectrum.component.TypeComponent;
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
				TypeComponent.class
		);
	}
	
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
					Effects.giveImmediateStun(child, duration);
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
		for(Iterator<Component> iter = removed.iterator(); iter.hasNext(); ){
			toEntity.add(iter.next());
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
