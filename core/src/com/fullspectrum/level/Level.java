package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM;
import static com.fullspectrum.game.GameVars.R_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.R_WORLD_WIDTH;
import static com.fullspectrum.game.GameVars.TILE_SCALE;

import java.util.Comparator;
import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.level.Tile.Side;

public class Level {

	// Physics
	private World world;
	private Box2DDebugRenderer b2dr;

	// Tile Map
	private TiledMap map;
	private TmxMapLoader loader;
	private OrthogonalTiledMapRenderer mapRenderer;

	// Player
	private Entity player;

	// Camera
	private OrthographicCamera cam;

	// Rendering
	private SpriteBatch batch;

	public Level(World world, OrthographicCamera cam, SpriteBatch batch) {
		this.world = world;
		this.cam = cam;
		this.batch = batch;
		b2dr = new Box2DDebugRenderer();
		loader = new TmxMapLoader();
	}

	public void loadMap(String path) {
		map = loader.load(path);
		mapRenderer = new OrthogonalTiledMapRenderer(map, TILE_SCALE / PPM);
		setupTilePhysics();
	}

	private void setupTilePhysics() {
		final TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("ground");

		// Init Physics Object
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 0.0f;

		Array<Tile> tiles = new Array<Tile>();
		Boolean[][] tileExists = new Boolean[layer.getHeight()][layer.getWidth()];
		for (int row = 0; row < layer.getHeight(); row++) {
			for (int col = 0; col < layer.getWidth(); col++) {
				Cell cell = layer.getCell(col, row);
				if (cell == null){
					tileExists[row][col] = false;
					continue;
				}
				if (cell.getTile() == null){
					tileExists[row][col] = false;
					continue;
				}
				tileExists[row][col] = true;
				
				Tile tile = new Tile();
				tile.row = row;
				tile.col = col;
				
				boolean surrounded = true;
				if(row > 0){
					Cell c = layer.getCell(col, row - 1);
					if(c == null || c.getTile() == null) {
						tile.side = Side.SOUTH;
						surrounded = false;
					}
				}
				if(row < layer.getHeight() - 1 && surrounded){
					Cell c = layer.getCell(col, row + 1);
					if(c == null || c.getTile() == null) {
						tile.side = Side.NORTH;
						surrounded = false;
					}
				}
				if(col > 0 && surrounded){
					Cell c = layer.getCell(col - 1, row);
					if(c == null || c.getTile() == null) {
						tile.side = Side.WEST;
						surrounded = false;
					}
				}
				if(col < layer.getWidth() - 1 && surrounded){
					Cell c = layer.getCell(col + 1, row);
					if(c == null || c.getTile() == null) {
						tile.side = Side.EAST;
						surrounded = false;
					}
				}
				tile.surrounded = surrounded;
				tiles.add(tile);
//				bdef.position.set(col + 0.5f, row + 0.5f);
//				world.createBody(bdef).createFixture(fdef);
			}
		}
		tiles.sort(new Comparator<Tile>() {
			@Override
			public int compare(Tile o1, Tile o2) {
				if(o1.surrounded && !o2.surrounded) return 1;
				if(!o1.surrounded && o2.surrounded) return -1;
				return o1.getIndex(layer.getWidth()) < o2.getIndex(layer.getWidth()) ? -1 : 1;
			}
		});
		while(tiles.size > 0){
			Tile t = tiles.first();
			if(!tileExists[t.row][t.col]) continue;
			tileExists[t.row][t.col] = false;
			int startCol = t.col;
			int startRow = t.row;
			int endCol = t.col;
			int endRow = t.row;
			
			// Do expansion
			if(!t.surrounded && (t.side == Side.WEST || t.side == Side.EAST)){
				int[] coords = expandCol(startRow, t.col, layer.getHeight(), tileExists);
				startRow = coords[0];
				endRow = coords[1];
				coords = expandRow(startRow, endRow, startCol, layer.getWidth(), tileExists);
				startCol = coords[0];
				endCol = coords[1];
			}
			else{
				int[] coords = expandRow(startCol, t.row, layer.getWidth(), tileExists);
				startCol = coords[0];
				endCol = coords[1];
				coords = expandCol(startCol, endCol, startRow, layer.getHeight(), tileExists);
				startRow = coords[0];
				endRow = coords[1];
			}
			
			int width = endCol - startCol + 1;
			int height = endRow - startRow + 1;
			shape.setAsBox(width * 0.5f, height * 0.5f);
			bdef.position.set(startCol + width * 0.5f, startRow + height * 0.5f);
			world.createBody(bdef).createFixture(fdef);
			removeTiles(startCol, endCol, startRow, endRow, tiles);
		}
	}
	
	private int[] expandRow(int startCol, int row, int maxWidth, Boolean[][] tileExists){
		int[] coords = new int[2];
		int sCol = startCol;
		int eCol = startCol;
		for(int col = startCol - 1; col >= 0; col--){
			if(!tileExists[row][col]) break;
			sCol--;
			tileExists[row][col] = false;
		}
		for(int col = startCol + 1; col < maxWidth; col++){
			if(!tileExists[row][col]) break;
			eCol++;
			tileExists[row][col] = false;
		}
		coords[0] = sCol;
		coords[1] = eCol;
		return coords;
	}
	
	private int[] expandCol(int startRow, int col, int maxHeight, Boolean[][] tileExists){
		int[] coords = new int[2];
		int sRow = startRow;
		int eRow = startRow;
		for(int row = startRow - 1; row >= 0; row--){
			if(!tileExists[row][col]) break;
			sRow--;
			tileExists[row][col] = false;
		}
		for(int row = startRow + 1; row < maxHeight; row++){
			if(!tileExists[row][col]) break;
			eRow++;
			tileExists[row][col] = false;
		}
		coords[0] = sRow;
		coords[1] = eRow;
		return coords;
	}
	
	private int[] expandRow(int startRow, int endRow, int startCol, int maxWidth, Boolean[][] tileExists){
		int[] coords = new int[2];
		int sCol = startCol;
		int eCol = startCol;
		for(int col = startCol - 1; col >= 0; col--){
			if(!validCol(startRow, endRow, col, tileExists))break;
			invalidateCol(startRow, endRow, col, tileExists);
			sCol--;
		}
		for(int col = startCol + 1; col < maxWidth; col++){
			if(!validCol(startRow, endRow, col, tileExists))break;
			invalidateCol(startRow, endRow, col, tileExists);
			eCol++;
		}
		coords[0] = sCol;
		coords[1] = eCol;
		return coords;
	}
	
	private int[] expandCol(int startCol, int endCol, int startRow, int maxHeight, Boolean[][] tileExists){
		int[] coords = new int[2];
		int sRow = startRow;
		int eRow = startRow;
		for(int row = startRow - 1; row >= 0; row--){
			if(!validRow(startCol, endCol, row, tileExists))break;
			invalidateRow(startCol, endCol, row, tileExists);
			sRow--;
		}
		for(int row = startRow + 1; row < maxHeight; row++){
			if(!validRow(startCol, endCol, row, tileExists))break;
			invalidateRow(startCol, endCol, row, tileExists);
			eRow++;
		}
		coords[0] = sRow;
		coords[1] = eRow;
		return coords;
	}
	
	private boolean validRow(int startCol, int endCol, int row, Boolean[][] tileExists){
		for(int i = startCol; i <= endCol; i++){
			if(!tileExists[row][i])return false;
		}
		return true;
	}
	
	private boolean validCol(int startRow, int endRow, int col, Boolean[][] tileExists){
		for(int i = startRow; i <= endRow; i++){
			if(!tileExists[i][col])return false;
		}
		return true;
	}
	
	private void invalidateRow(int startCol, int endCol, int row, Boolean[][] tileExists){
		for(int i = startCol; i <= endCol; i++){
			tileExists[row][i] = false;
		}
	}
	
	private void invalidateCol(int startRow, int endRow, int col, Boolean[][] tileExists){
		for(int i = startRow; i <= endRow; i++){
			tileExists[i][col] = false;
		}
	}
	
	private void removeTiles(int startCol, int endCol, int startRow, int endRow, Array<Tile> tiles){ 
		Iterator<Tile> iter = tiles.iterator();
		while(iter.hasNext()){
			Tile t = iter.next();
			if(t.row >= startRow && t.row <= endRow && t.col >= startCol && t.col <= endCol){
				iter.remove();
			}
		}
	}
	
	public void update(float delta) {
		if (player != null) player.update(delta);
		cam.position.x = R_WORLD_WIDTH * 0.5f;
		cam.position.y = R_WORLD_HEIGHT * 0.5f;
	}

	public void render() {
		cam.update();
		batch.setProjectionMatrix(cam.combined);

		mapRenderer.setView(cam);
		mapRenderer.render();

//		b2dr.render(world, cam.combined);

		if (player != null) {
			batch.begin();
			player.render(batch);
			batch.end();
		}
	}

	public void setPlayer(Entity player) {
		this.player = player;
	}

}
