package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class NavMesh {

	private Array<Tile> nodes;
	private ShapeRenderer sRender;
	
	private NavMesh(Array<Tile> tiles) { 
		nodes = tiles; 
		sRender = new ShapeRenderer();
	}
	
	public static NavMesh createNavMesh(Entity entity, Level level){
		Array<Tile> tiles = level.getWalkableTiles();
		return new NavMesh(tiles);
	}
	
	public void render(SpriteBatch batch){
		sRender.setProjectionMatrix(batch.getProjectionMatrix());
		sRender.begin(ShapeType.Filled);
		sRender.setColor(Color.RED);
		for(Tile node : nodes){
			float x1 = node.getCol();
			float y1 = node.getRow();
			float width = 8.0f * PPM_INV;
			float height = width;
			sRender.rect(x1 - (PPM_INV * 0.5f - width * 0.5f), y1 - (PPM_INV * 0.5f - height * 0.5f), width * 0.5f, height * 0.5f, width, height, 1.0f, 1.0f, 45f);
		}
		sRender.end();
	}
	
}
