package com.fullspectrum.input;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class InputProfile {
	
	private ArrayMap<String, InputContext> contexts;
	private InputContext currentContext;

	public InputProfile(){
		contexts = new ArrayMap<String, InputContext>();
	}
	
	public Actions getKey(int keyCode){
		return currentContext.getKey(keyCode);
	}
	
	public Actions getButton(int buttonCode){
		return currentContext.getButton(buttonCode);
	}
	
	public Actions getPOV(String povCode){
		return currentContext.getPOV(povCode);
	}
	
	public void setContext(String contextName){
		currentContext = contexts.get(contextName);
	}
	
	public InputContext getContext(){
		return currentContext;
	}
	
	public void load(String path){
		XmlReader xml = new XmlReader();
		try {
			Element root = xml.parse(Gdx.files.internal(path));
			for(Element e : root.getChildrenByName("context")){
				InputContext context = new InputContext(e.get("type"));
				context.loadActions(e);
				contexts.put(context.getName(), context);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentContext = contexts.get("keyboard");
	}
	
}
