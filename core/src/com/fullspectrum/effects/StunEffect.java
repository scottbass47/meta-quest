package com.fullspectrum.effects;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.fullspectrum.component.AbstractSMComponent;
import com.fullspectrum.component.BarrierComponent;
import com.fullspectrum.component.BodyComponent;
import com.fullspectrum.component.CollisionComponent;
import com.fullspectrum.component.DeathComponent;
import com.fullspectrum.component.EngineComponent;
import com.fullspectrum.component.EntityComponent;
import com.fullspectrum.component.FacingComponent;
import com.fullspectrum.component.HealthComponent;
import com.fullspectrum.component.KnockBackComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.MoneyComponent;
import com.fullspectrum.component.PositionComponent;
import com.fullspectrum.component.RenderComponent;
import com.fullspectrum.component.ShaderComponent;
import com.fullspectrum.component.TextureComponent;
import com.fullspectrum.component.TimerComponent;
import com.fullspectrum.component.TintComponent;
import com.fullspectrum.component.TypeComponent;
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
				TintComponent.class,
				ShaderComponent.class,
				TypeComponent.class,
				
				// Global Components (always required)
				TimerComponent.class,
				EngineComponent.class,
				WorldComponent.class,
				LevelComponent.class,
				DeathComponent.class,
				EntityComponent.class
		);
	}
	
	// BUG When rendering entities are sometimes invisible
	public StunEffect(Entity toEntity, float duration) {
		super(toEntity, duration, true);
		delayed = true;
		removed = new ObjectSet<Component>();
		removedMachines = new Array<StateMachine<? extends State, ? extends StateObject>>();
	}

	@Override
	protected void give() {
		for(int i = 0; i < toEntity.getComponents().size(); i++){
			Component comp = toEntity.getComponents().get(i);
			if(requiredComponents.contains(comp.getClass())) continue;
			if(comp instanceof AbstractSMComponent<?>){
				AbstractSMComponent<StateMachine<? extends State,? extends StateObject>> smComp = (AbstractSMComponent<StateMachine<? extends State,? extends StateObject>>)comp;
				removedMachines.addAll(smComp.getMachines());
				StateMachineSystem.getInstance().removeStateMachines(smComp.getMachines());
			}
			removed.add(toEntity.remove(comp.getClass()));
			i--;
		}
		StateMachineSystem.getInstance().updateMachines();
		Mappers.body.get(toEntity).body.setLinearVelocity(0.0f, 0.0f);
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
		Effects.giveEase(toEntity, 0.5f, 10.0f);
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
