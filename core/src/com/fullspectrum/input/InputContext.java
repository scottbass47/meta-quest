package com.fullspectrum.input;

import java.util.Iterator;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.XmlReader.Element;

public class InputContext {

	private String name;
	private ArrayMap<Integer, Actions> keyMap;
	private ArrayMap<Integer, Actions> buttonMap;
	private ArrayMap<String, Actions> povMap;
	private ArrayMap<AxisData, Actions> axisMap;
	
	public InputContext(String name){
		this.setName(name);
		keyMap = new ArrayMap<Integer, Actions>();
		buttonMap = new ArrayMap<Integer, Actions>();
		povMap = new ArrayMap<String, Actions>();
		axisMap = new ArrayMap<AxisData, Actions>();
	}
	
	public void loadActions(Element element){
		Element actionsElement = element.getChildByName("actions");
		for(Element e: actionsElement.getChildrenByName("action")){
			String name = e.get("name");
			Element actionType = e.getChild(0);
			if(actionType.getName().equals("button")){
				buttonMap.put(XboxOneButtons.getCode(actionType.get("name")), Actions.getAction(name));
			}
			else if(actionType.getName().equals("key")){
				keyMap.put(Keys.valueOf(actionType.get("code")), Actions.getAction(name));
			}
			else if(actionType.getName().equals("pov")){
				povMap.put(actionType.get("name"), Actions.getAction(name));
			}
			else if(actionType.getName().equals("axis")){
				axisMap.put(new AxisData(actionType.get("dir"), actionType.getInt("num")), Actions.getAction(name));
			}
		}
	}
	
	public Array<Actions> getPOVActions(){
		Array<Actions> ret = new Array<Actions>();
		Iterator<Entry<String, Actions>> iter = povMap.iterator();
		while(iter.hasNext()){
			Entry<String, Actions> action = iter.next();
			ret.add(action.value);
		}
		return ret;
	}
	
	public Array<Actions> getAxisActions(){
		Array<Actions> ret = new Array<Actions>();
		Iterator<Entry<AxisData, Actions>> iter = axisMap.iterator();
		while(iter.hasNext()){
			Entry<AxisData, Actions> action = iter.next();
			ret.add(action.value);
		}
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Actions getKey(int keyCode){
		return keyMap.get(keyCode);
	}
	
	public Actions getButton(int buttonCode){
		return buttonMap.get(buttonCode);
	}
	
	public Actions getPOV(String povCode){
		return povMap.get(povCode);
	}
	
	public Actions getAxis(AxisData axisData){
		return axisMap.get(axisData);
	}
}
