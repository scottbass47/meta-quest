package com.fullspectrum.game;

import org.lwjgl.opengl.GL11;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.fullspectrum.ability.Ability;
import com.fullspectrum.ability.AbilityType;
import com.fullspectrum.component.AbilityComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.input.Actions;
import com.fullspectrum.input.Mouse;

public class PauseMenu {

	private ShapeRenderer renderer;
	private static Entity player;
	private OrthographicCamera hudCam;
	
	private float spacing = 20.0f;
	private float scale = 4.0f;
	private int icons = 3;
	private int activeAbilities = 0;
	
	
	public PauseMenu(OrthographicCamera hudCam) {
		renderer = new ShapeRenderer();
		this.hudCam = hudCam;
		
		AbilityComponent abilityComp = Mappers.ability.get(player);
		for(Ability ability : abilityComp.getAbilityMap().values()){
			if(ability.isActivated()){
				if(activeAbilities == 3){
					ability.deactivate();
					continue;
				}
				activeAbilities++;
			}
		}
	}
	
	public void update(float delta){
		Vector3 mousePos = hudCam.unproject(new Vector3(Mouse.getScreenPosition(), 0.0f));
		if(Mouse.isJustPressed()){
			Ability ability = getAbility(mousePos);
			if(ability != null){
				if(ability.isActivated()){
					ability.deactivate();
					activeAbilities--;
				}
				else{
					if(activeAbilities < 3){
						ability.activate();
						activeAbilities++;
					}
				}
				updateAbilityInputs();
			}
		}
	}
	
	private void updateAbilityInputs(){
		AbilityComponent abilityComp = Mappers.ability.get(player);
		int counter = 0;
		for(AbilityType type : abilityComp.getAbilityMap().keys()){
			Ability ability = abilityComp.getAbility(type);
			if(ability.isActivated()){
				switch(counter){
				case 0:
					ability.setInput(Actions.ABILITY_1);
					break;
				case 1:
					ability.setInput(Actions.ABILITY_2);
					break;
				case 2:
					ability.setInput(Actions.ABILITY_3);
					break;
				}
				counter++;
			}
		}
	}
	
	public void render(SpriteBatch batch){
		// Begin
		Gdx.gl.glEnable(GL11.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		renderer.setProjectionMatrix(batch.getProjectionMatrix());
		renderer.begin(ShapeType.Filled);
		
		// Background
		renderer.setColor(0.0f, 0.0f, 0.0f, 0.8f);
		renderer.rect(0, 0, GameVars.SCREEN_WIDTH, GameVars.SCREEN_HEIGHT);
		
		// End
		renderer.end();

		// Render Abilities
		batch.begin();
		float startX = getStartX();
		float y = getY();
		AbilityComponent abilityComp = Mappers.ability.get(player);
		for(AbilityType type : abilityComp.getAbilityMap().keys()){
			Ability ability = abilityComp.getAbility(type);
			if(ability.isActivated()) batch.setColor(Color.WHITE);
			else batch.setColor(Color.DARK_GRAY);
			TextureRegion icon = ability.getIcon();
			batch.draw(
					icon, 
					startX, 
					y, 
					0.0f, 
					0.0f, 
					icon.getRegionWidth(), 
					icon.getRegionHeight(), 
					scale, scale, 0.0f);
			startX += spacing + getScaledIconWidth();
		}
		batch.setColor(Color.WHITE);
		batch.end();
		Gdx.gl.glDisable(GL11.GL_BLEND);
	}
	
	public static void setPlayer(Entity p){
		player = p;
	}
	
	public float getStartX(){
		return GameVars.SCREEN_WIDTH * 0.5f - getWidth() * 0.5f;
	}
	
	public float getY(){
		return 360;
	}
	
	public float getWidth(){
		return icons * getScaledIconWidth() + (icons - 1) * spacing;
	}
	
	public float getScaledIconWidth(){
		return 18.0f * scale;
	}
	
	public Ability getAbility(Vector3 mousePos){
		for(int i = 0; i < icons; i++){
			float x = getStartX() + i * getScaledIconWidth() + i * spacing;
			float y = getY();
			float width = getScaledIconWidth();
			float height = width;
			
			if(mousePos.x >= x && mousePos.x <= x + width && mousePos.y >= y && mousePos.y <= y + height){
				return Mappers.ability.get(player).getAbilityMap().getValueAt(i);
			}
		}
		return null;
	}
}
