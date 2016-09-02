package com.fullspectrum.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component{

	public TextureRegion region = null;
	
	public TextureComponent(TextureRegion region){
		this.region = region;
	}
	
}