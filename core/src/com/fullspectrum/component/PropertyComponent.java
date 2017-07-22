package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PropertyComponent implements Component, Poolable{

	private ArrayMap<String, Float> floatMap;
	private ArrayMap<String, String> stringMap;
	private ArrayMap<String, Object> objectMap;
	private ArrayMap<String, Boolean> booleanMap;
	
	public PropertyComponent() {
		floatMap = new ArrayMap<String, Float>();
		stringMap = new ArrayMap<String, String>();
		objectMap = new ArrayMap<String, Object>();
		booleanMap = new ArrayMap<String, Boolean>();
	}
	
	public void setProperty(String name, float value) {
		floatMap.put(name, value);
	}
	
	public void setProperty(String name, String value) {
		stringMap.put(name, value);
	}
	
	public void setProperty(String name, Object value) {
		objectMap.put(name, value);
	}
	
	public void setProperty(String name, boolean value) {
		booleanMap.put(name, value);
	}
	
	public float getFloat(String name) {
		return floatMap.get(name);
	}
	
	public String getString(String name) {
		return stringMap.get(name);
	}
	
	public Object getObject(String name) {
		return objectMap.get(name);
	}
	
	public boolean getBoolean(String name) {
		return booleanMap.get(name);
	}
	
	@Override
	public void reset() {
		floatMap.clear();
		stringMap.clear();
		objectMap.clear();
		booleanMap.clear();
	}

	
	
}
