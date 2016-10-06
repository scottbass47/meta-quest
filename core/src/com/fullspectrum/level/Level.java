package com.fullspectrum.level;

import static com.fullspectrum.game.GameVars.PPM_INV;
import static com.fullspectrum.game.GameVars.R_WORLD_HEIGHT;
import static com.fullspectrum.game.GameVars.R_WORLD_WIDTH;

import java.util.Comparator;
import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.fullspectrum.level.Tile.Side;

public class Level {

	// Physics
	private World world;

	// Tile Map
	private TiledMap map;
	private TmxMapLoader loader;
	private OrthogonalTiledMapRenderer mapRenderer;
	private int width;
	private int height;
	private Tile[][] mapTiles;

	// Camera
	private OrthographicCamera cam;

	// Rendering
	private SpriteBatch batch;

	public Level(World world, OrthographicCamera cam, SpriteBatch batch) {
		this.world = world;
		this.cam = cam;
		this.batch = batch;
		loader = new TmxMapLoader();
	}

	public void loadMap(String path) {
		map = loader.load(path);
		mapRenderer = new OrthogonalTiledMapRenderer(map, PPM_INV, batch);
		setupTilePhysics();
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}

	public void update(float delta) {
		cam.position.x = R_WORLD_WIDTH * 0.5f;
		cam.position.y = R_WORLD_HEIGHT * 0.5f;
	}
	
	public void render() {
		mapRenderer.setView(cam);
		mapRenderer.render();
	}
	
	private void setupTilePhysics() {
		final TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("ground");
		width = layer.getWidth();
		height = layer.getHeight();
		
		// Init Physics Object
		BodyDef bdef = new BodyDef();
		bdef.type = BodyType.StaticBody;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f);
		FixtureDef fdef = new FixtureDef();
		fdef.shape = shape;
		fdef.friction = 0.0f;

		Array<Tile> tiles = new Array<Tile>();
		mapTiles = new Tile[height][width];
		Boolean[][] tileExists = new Boolean[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				Cell cell = layer.getCell(col, row);
				if (cell == null || cell.getTile() == null){
					tileExists[row][col] = false;
					mapTiles[row][col] = new Tile(row, col, true);
					continue;
				}
				tileExists[row][col] = true;
				
				Tile tile = new Tile(row, col, false);
				
				if(row > 0){
					Cell c = layer.getCell(col, row - 1);
					if(c == null || c.getTile() == null) {
						tile.addSide(Side.SOUTH);
					}
				}
				if(row < height - 1){
					Cell c = layer.getCell(col, row + 1);
					if(c == null || c.getTile() == null) {
						tile.addSide(Side.NORTH);
					}
				}
				if(col > 0){
					Cell c = layer.getCell(col - 1, row);
					if(c == null || c.getTile() == null) {
						tile.addSide(Side.WEST);
					}
				}
				if(col < width - 1){
					Cell c = layer.getCell(col + 1, row);
					if(c == null || c.getTile() == null) {
						tile.addSide(Side.EAST);
					}
				}
				tiles.add(tile);
				mapTiles[row][col] = tile;
			}
		}
		tiles.sort(new Comparator<Tile>() {
			@Override
			public int compare(Tile o1, Tile o2) {
				if(o1.isSurrounded() && !o2.isSurrounded()) return 1;
				if(!o1.isSurrounded() && o2.isSurrounded()) return -1;
				return o1.getIndex(layer.getWidth()) < o2.getIndex(layer.getWidth()) ? -1 : 1;
			}
		});
		while(tiles.size > 0){
			Tile t = tiles.first();
			if(!tileExists[t.getRow()][t.getCol()]) continue;
			tileExists[t.getRow()][t.getCol()] = false;
			int startCol = t.getCol();
			int startRow = t.getRow();
			int endCol = t.getCol();
			int endRow = t.getRow();
			
			// Do expansion
			if(!t.isSurrounded() && (t.isOpen(Side.WEST) || t.isOpen(Side.EAST))){
				int[] coords = expandCol(startRow, t.getCol(), layer.getHeight(), tileExists);
				startRow = coords[0];
				endRow = coords[1];
				coords = expandRow(startRow, endRow, startCol, layer.getWidth(), tileExists);
				startCol = coords[0];
				endCol = coords[1];
			}
			else{
				int[] coords = expandRow(startCol, t.getRow(), layer.getWidth(), tileExists);
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
	
	public Tile tileAt(int row, int col){
		return mapTiles[row][col];
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
			if(t.getRow() >= startRow && t.getRow() <= endRow && t.getCol() >= startCol && t.getCol() <= endCol){
				iter.remove();
			}
		}
	}
}
