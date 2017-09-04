package com.cpubrew.shader;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public abstract class Shader {

	protected ShaderProgram program;
	
	public Shader(String vertex, String fragment){
		this(load(vertex, fragment));
	}
	
	public Shader(ShaderProgram program){
		this.program = program;
	}
	
	public abstract void passUniforms(Entity entity);
	
	public void setUniforms(Entity entity){
		passUniforms(entity);
	}
	
	public ShaderProgram getProgram() {
		return program;
	}
	
	public static ShaderProgram load(String vertex, String fragment){
		ShaderProgram program = new ShaderProgram(Gdx.files.internal("shaders/" + vertex), Gdx.files.internal("shaders/" + fragment));
		if (!program.isCompiled()) {
			throw new GdxRuntimeException(program.getLog());
		}
		return program;
	}
	
}
