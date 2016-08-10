package com.fullspectrum.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader.Element;

public class InputContext {

	private String name;
	private ArrayMap<Integer, Actions> keyMap;
	private ArrayMap<Integer, Actions> buttonMap;
	private ArrayMap<String, Actions> povMap;
	
	public InputContext(String name){
		this.setName(name);
		keyMap = new ArrayMap<Integer, Actions>();
		buttonMap = new ArrayMap<Integer, Actions>();
		povMap = new ArrayMap<String, Actions>();
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
		}
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
}
