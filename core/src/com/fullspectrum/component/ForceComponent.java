package com.fullspectrum.component;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;

// TODO Add in support for continuous forces and multiple forces
public class ForceComponent implements Component, Poolable{

	public float fx = 0.0f;
	public float fy = 0.0f;
	public ArrayMap<CForce, Vector2> cForceMap;
	
	public ForceComponent(){
		cForceMap = new ArrayMap<CForce, Vector2>();
	}
	
	public ForceComponent set(float fx, float fy){
		this.fx = fx;
		this.fy = fy;
		return this;
	}
	
	public ForceComponent add(float fx, float fy){
		this.fx += fx;
		this.fy += fy;
		return this;
	}
	
	public ForceComponent addForce(float force, float angle){
		return add(force * MathUtils.cos(angle), force * MathUtils.sin(angle));
	}
	
	public ForceComponent add(CForce cForce, float fx, float fy){
		createCForce(cForce);
		Vector2 prevForce = cForceMap.get(cForce);
		float dx = fx - prevForce.x;
		float dy = fy - prevForce.y;
		cForceMap.put(cForce, cForceMap.get(cForce).set(fx, fy));
		return add(dx, dy);
	}
	
	public ForceComponent addForce(CForce cForce, float force, float angle){
		return add(cForce, force * MathUtils.cos(angle), force * MathUtils.sin(angle));
	}
	
	public float getFX(CForce cForce){
		createCForce(cForce);
		return cForceMap.get(cForce).x;
	}
	
	public float getFY(CForce cForce){
		createCForce(cForce);
		return cForceMap.get(cForce).y;
	}
	
	public void removeCForce(CForce cForce){
		if(!cForceMap.containsKey(cForce)) return;
		cForceMap.removeKey(cForce);
	}
	
	private void createCForce(CForce cForce){
		if(!cForceMap.containsKey(cForce)){
			cForceMap.put(cForce, new Vector2(0.0f, 0.0f));
		}
	}
	
	public Vector2 sumCForces(){
		Vector2 sum = new Vector2();
		for(Iterator<CForce> iter = cForceMap.keys().iterator(); iter.hasNext();){
			sum.add(cForceMap.get(iter.next()));
		}
		return sum;
	}
	
	@Override
	public void reset() {
		fx = 0.0f;
		fy = 0.0f;
	}
	
	public enum CForce{
		MOVEMENT {
			@Override
			public boolean isActive(Entity entity) {
				return Mappers.groundMovement.get(entity) != null
					|| Mappers.flying.get(entity) != null
					|| Mappers.ladder.get(entity) != null
					|| Mappers.dropMovement.get(entity) != null;
			}

			@Override
			public Vector2 error(Entity entity, Vector2 expected, Vector2 actual) {
				Vector2 error = new Vector2();
				if(Mappers.groundMovement.get(entity) != null || Mappers.dropMovement.get(entity) != null){
					error.x = expected.x - actual.x;
				}else if(Mappers.ladder.get(entity) != null || Mappers.flying.get(entity) != null){
					error = expected.sub(actual);
				}
				return error;
			}
		},
		BOBBING {
			@Override
			public boolean isActive(Entity entity) {
				return Mappers.bob.get(entity) != null;
			}

			@Override
			public Vector2 error(Entity entity, Vector2 expected, Vector2 actual) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		public abstract Vector2 error(Entity entity, Vector2 expected, Vector2 actual);
		public abstract boolean isActive(Entity entity);
	}
}
