package com.fullspectrum.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.component.InputComponent;
import com.fullspectrum.component.LevelComponent;
import com.fullspectrum.component.LevelSwitchComponent;
import com.fullspectrum.component.Mappers;
import com.fullspectrum.component.PlayerComponent;
import com.fullspectrum.input.Actions;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.LevelInfo.LevelType;
import com.fullspectrum.level.Theme;

public class LevelSwitchSystem extends IteratingSystem{

	public LevelSwitchSystem() {
		super(Family.all(LevelSwitchComponent.class, PlayerComponent.class).get());
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		InputComponent inputComp = Mappers.input.get(entity);
		LevelSwitchComponent switchComp = Mappers.levelSwitch.get(entity);
		LevelComponent levelComp = Mappers.level.get(entity);
		Level level = levelComp.level;
		
		if(inputComp.input.isPressed(Actions.MOVE_UP)){
			// switch
			String switchData = switchComp.data;
			if(switchData.trim().equalsIgnoreCase("next")){
				level.getManager().switchNext();
			}else if(switchData.trim().equalsIgnoreCase("back")){
				// switch back
			}else{
				Array<String> parts = new Array<String>(switchData.split("-"));
				Theme theme = Theme.get(parts.first());
				if(theme == null){
					theme = level.getInfo().getTheme();
				}else{
					parts.removeIndex(0);
				}
				LevelType type = LevelType.get(parts.removeIndex(0));
				switch(type){
				case HUB:
					level.getManager().switchHub(theme);
					break;
				case LEVEL:
					int levelNum = Integer.parseInt(parts.removeIndex(0));
					int sectionNum = parts.size == 0 ? 1 : Integer.parseInt(parts.removeIndex(0));
					level.getManager().switchLevel(theme, levelNum, sectionNum);
					break;
				case SECRET:
					levelNum = level.getInfo().getLevel();
					if(parts.size == 3){
						levelNum = Integer.parseInt(parts.removeIndex(0));
					}
					int secretNum = Integer.parseInt(parts.removeIndex(0));
					sectionNum = parts.size == 0 ? 1 : Integer.parseInt(parts.removeIndex(0));
					level.getManager().switchLevel(theme, LevelType.SECRET, levelNum, secretNum, sectionNum);
					break;
				default:
					break;
				}
				
			}
			entity.remove(LevelSwitchComponent.class);
		}
	}
}
