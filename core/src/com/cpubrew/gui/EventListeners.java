package com.cpubrew.gui;

import com.badlogic.gdx.utils.Array;

public class EventListeners {

	private Array<Object> listeners;
	
	public EventListeners() {
		listeners = new Array<Object>();
	}
	
	public void addListener(Class<? extends EventListener> clazz, EventListener listener) {
		if(listener == null) return;
		if(!clazz.isInstance(listener)) throw new IllegalArgumentException("Listener and class are mismatched.");
		
		listeners.add(clazz);
		listeners.add(listener);
	}
	
	public void removeListener(EventListener listener) {
		if(listener == null) return;
		for(int i = 1; i < listeners.size; i += 2) {
			if(listeners.get(i).equals(listener)) {
				listeners.removeRange(i-1, i);
				break;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends EventListener> Array<T> getListeners(Class<T> clazz) {
		Array<T> arr = new Array<T>();
		
		for(int i = 0; i < listeners.size - 1 ; i += 2){
			if(listeners.get(i) == clazz) {
				arr.add((T) listeners.get(i+1));
			}
		}
		return arr;
	}
	
	public int getNumListeners() {
		return listeners.size / 2;
	}
}
