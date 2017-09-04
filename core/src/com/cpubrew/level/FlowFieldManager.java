package com.cpubrew.level;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;

public class FlowFieldManager {

	private ArrayMap<GridPoint2, FlowField> fieldMap;
	private Level level;
	private int radius;
	private ObjectSet<GridPoint2> points;
	private Array<FlowField> pool;
	
	public FlowFieldManager(Level level, int radius){
		fieldMap = new ArrayMap<GridPoint2, FlowField>();
		points = new ObjectSet<GridPoint2>();
		pool = new Array<FlowField>();
		init(level, radius, 4);
	}
	
	public void init(Level level, int radius, int poolSize){
		reset();
		this.level = level;
		this.radius = radius;
		for(int i = 0; i < poolSize; i++){
			pool.add(new FlowField(level, radius));
		}
	}
	
	private  void reset(){
		fieldMap.clear();
		pool.clear();
		points.clear();
	}
	
	public void update(float delta){
//		System.out.println("Pool Size: " + pool.size);
//		System.out.println("Active: " + fieldMap.size);
		for(Iterator<Entry<GridPoint2, FlowField>> iter = fieldMap.iterator(); iter.hasNext();){
			Entry<GridPoint2, FlowField> entry = iter.next();
			
			// Recycle old flow field
			if(!points.contains(entry.key)){
				pool.add(entry.value);
				iter.remove();
			}
		}
		points.clear();
	}
	
	public void render(SpriteBatch batch){
		for(FlowField field : fieldMap.values()){
			field.render(batch);
		}
	}
	
	public FlowField getField(int row, int col){
		GridPoint2 point = new GridPoint2(col, row);
		points.add(point);
		if(!fieldMap.containsKey(point)){
			if(pool.size > 0){
				FlowField field = pool.pop();
				field.setGoal(row, col);
				fieldMap.put(point, field);
			}else{
				fieldMap.put(point, new FlowField(level, radius));
				fieldMap.get(point).setGoal(row, col);
			}
		}
		return fieldMap.get(point);
	}
	
	public FlowField getField(float x, float y){
		return getField((int)y, (int)x);
	}
	
}
