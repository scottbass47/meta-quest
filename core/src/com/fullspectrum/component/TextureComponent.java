package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TextureComponent implements Component, Poolable{

	public Array<TextureRegion> regions = null;
	
	public TextureComponent() {
		regions = new Array<TextureRegion>();
	}
	
	public TextureRegion first(){
		return regions.first();
	}
	
	public Array<TextureRegion> getRegions(){
		return regions;
	}
	
	public int numTextures(){
		return regions.size;
	}
	
	@Override
	public void reset() {
		regions = null;
	}
	
	public TextureComponent set(TextureRegion region){
		regions.clear();
		regions.add(region);
		return this;
	}
	
}