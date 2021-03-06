package com.cpubrew.editor;

import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.editor.action.ActionManager;
import com.cpubrew.editor.action.EditorActions;
import com.cpubrew.editor.action.MoveAction;
import com.cpubrew.editor.action.SelectAction;
import com.cpubrew.editor.command.Command;
import com.cpubrew.editor.command.ResizeMapCommand;
import com.cpubrew.editor.command.ResizeMapCommand.Direction;
import com.cpubrew.editor.command.UpdateSurroundingTilesCommand;
import com.cpubrew.editor.mapobject.MapObject;
import com.cpubrew.game.GameVars;
import com.cpubrew.gui.KeyBoardManager;
import com.cpubrew.gui.KeyEvent;
import com.cpubrew.gui.KeyListener;
import com.cpubrew.gui.Label;
import com.cpubrew.gui.MouseEvent;
import com.cpubrew.gui.MouseListener;
import com.cpubrew.gui.ScrollListener;
import com.cpubrew.gui.Window;
import com.cpubrew.input.Mouse;
import com.cpubrew.level.ExpandableGrid;
import com.cpubrew.level.GridPoint;
import com.cpubrew.level.Level;
import com.cpubrew.level.LevelUtils;
import com.cpubrew.level.MapRenderer;
import com.cpubrew.level.tiles.MapTile;
import com.cpubrew.level.tiles.MapTile.Side;
import com.cpubrew.level.tiles.MapTile.TileType;
import com.cpubrew.level.tiles.TileSlot;
import com.cpubrew.level.tiles.Tileset;
import com.cpubrew.level.tiles.TilesetTile;

public class LevelEditor implements KeyListener, MouseListener, ScrollListener {

	// Level
	private Level currentLevel;
	private ExpandableGrid<MapTile> tileMap;
	private TilePanel tilePanel;
	private MapRenderer mapRenderer;
	private Texture eraseTexture;
	private Texture selectTexture;
	private ActionManager actionManager;
	
	// Camera
	private OrthographicCamera worldCamera;
	private OrthographicCamera hudCamera;
	private float moveVel = 20.0f;
	
	private boolean mouseOnMap = false;
	private Vector2 mousePos;
	private boolean mouseDown = false;
	private float animTime;
	private boolean unsavedEdits = false;
	private boolean autoTiling = false;
	private int autoSaveInterval = 1000;
	private boolean open = false;
	
	// Commands
	private Stack<Command> history;
	private int savePointer = 0;
	private TileChanges currTileChanges;
	private Stack<TileChanges> tileHistory;
	private boolean editingTiles = false;
	private boolean discardTileChanges = false;
	
	// UI
	private Window editorWindow;
	private Label saveLabel;
	private Label actionLabel;
	private Label autoTileLabel;
	
	// Entity Spawns
//	private ArrayMap<Integer, EntitySpawn> spawnMap; // 0 is reserved for the player
//	private ArrayMap<Integer, Boolean> entityAdded;
//	private int nextID = 0;
	
	// Map Objects
	private ArrayMap<Integer, MapObject> objectMap;
	private ArrayMap<Integer, Boolean> enabledMap;
	private int id; // Represents the NEXT id that is open
	private ObjectSet<Integer> unusedID;
	
	public LevelEditor() {
		tilePanel = new TilePanel();
		tilePanel.setX(0.0f);
		tilePanel.setY(0.0f);
		
		mapRenderer = new MapRenderer();
		mapRenderer.setGridLinesOn(true);
		mapRenderer.setTileset(tilePanel.getTileset());

		setupTextures();

		history = new Stack<Command>();
		tileHistory = new Stack<TileChanges>();
		
		editorWindow = new Window("Level Editor");
		editorWindow.setPosition(0, 0);
		editorWindow.setSize(GameVars.SCREEN_WIDTH, GameVars.SCREEN_HEIGHT);
		
		BitmapFont font = AssetLoader.getInstance().getFont(AssetLoader.font18);
		
		saveLabel = new Label("Saved");
		saveLabel.setPosition(10, 10 + (int)tilePanel.getHeight());
		saveLabel.setFont(font);
		saveLabel.autoSetSize();
		
		autoTileLabel = new Label("Auto-Tiling: No");
		autoTileLabel.setPosition(saveLabel.getX() + saveLabel.getWidth() + 30, saveLabel.getY());
		autoTileLabel.setFont(font);
		autoTileLabel.autoSetSize();

		actionLabel = new Label("Action: Select");
		actionLabel.setPosition(autoTileLabel.getX() + autoTileLabel.getWidth() + 30, saveLabel.getY());
		actionLabel.setFont(font);
		actionLabel.autoSetSize();
		
		editorWindow.add(saveLabel);
		editorWindow.add(autoTileLabel);
		editorWindow.add(actionLabel);
		editorWindow.setRenderBackground(false);
		
		editorWindow.addKeyListener(this);
		editorWindow.addMouseListener(this);
		
//		spawnMap = new ArrayMap<Integer, Level.EntitySpawn>();
//		entityAdded = new ArrayMap<Integer, Boolean>();
		
		objectMap = new ArrayMap<Integer, MapObject>();
		enabledMap = new ArrayMap<Integer, Boolean>();
		unusedID = new ObjectSet<Integer>();
		
		actionManager = new ActionManager(this);
		
		Thread saveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					if(currentLevel == null || !open || !unsavedEdits) {
						try {
							Thread.sleep(autoSaveInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						synchronized (this) {
							try {
								// HACK This isn't very good
								LevelUtils.saveLevel(getCurrentLevel());
								saved();
							} catch(Exception e) {}
						}
					}
				}
			}
		}, "Editor Auto-Save");
		saveThread.setDaemon(true);
		saveThread.start();
	}
	
	private void setupTextures() {
		Pixmap pixmap = new Pixmap(16, 16, Format.RGBA8888);
		Color color = new Color(Color.DARK_GRAY);
		pixmap.setColor(color.mul(1.0f, 1.0f, 1.0f, 0.65f));
		pixmap.fillRectangle(0, 0, 16, 16);
		selectTexture = new Texture(pixmap);
		
		color = new Color(Color.PINK);
		pixmap.setColor(color.mul(1.0f, 1.0f, 1.0f, 0.65f));
		pixmap.fillRectangle(0, 0, 16, 16);
		eraseTexture = new Texture(pixmap);
		
		pixmap.dispose();
	}
	
	public Level getCurrentLevel() {
		// Setup spawns
//		updateLevelSpawns();
		
		// Update the object list in the level
		currentLevel.removeAllMapObjects();
		
		for(int id : objectMap.keys()) {
			if(enabledMap.get(id)) {
				currentLevel.addMapObject(objectMap.get(id));
			}
		}
		
		currentLevel.setCurrentID(id);
		
		return currentLevel;
	}
	
	public void setCurrentLevel(Level currentLevel) {
		if(this.currentLevel == currentLevel) return; // CLEANUP Bad check. Something more robust should be put in place to keep track of when levels are changing
		this.currentLevel = currentLevel;
		mapRenderer.setTileMap(currentLevel.getTileMap());
		tileMap = currentLevel.getTileMap();
		
		// Init the entity spawns
//		initEntitySpawns();
		
		// Populate MapObjects
		id = currentLevel.getCurrentID(); // Set the ID
		
		// Clear Maps
		objectMap.clear();
		enabledMap.clear();
		unusedID.clear();
		
		// Add all the IDs to the unused set
		for(int i = 0; i < id; i++) unusedID.add(i);
		
		Array<MapObject> objects = currentLevel.getMapObjects();
		for(MapObject mobj : objects) {
			// Remove ID
			unusedID.remove(mobj.getId()); 
		
			objectMap.put(mobj.getId(), mobj);
			enabledMap.put(mobj.getId(), true);
			
			// We don't serialize the level editor for each map object, so if this 
			// level was just loaded the level editor would be null
			mobj.setEditor(this);
		}
		
		
	}

	/** Returns the next id and INCREMENTS the id counter */
	public int nextID() {
		// If there is an unusedID use that first before creating a new one
		if(unusedID.size > 0) {
			int ret = unusedID.first();
			unusedID.remove(ret);
			return ret;
		}
		return id++;
	}
	
	public void removeMapObject(int id) {
		enabledMap.put(id, false);
		printIds();
	}
	
	public void addMapObject(MapObject mobj) {
		// IF this object already exists, then just enable it
		if(objectMap.containsKey(mobj.getId())) {
			enableMapObject(mobj.getId());
			printIds();
			return;
		}
		
		objectMap.put(mobj.getId(), mobj);
		enabledMap.put(mobj.getId(), true);
		printIds();
	}
	
	public void enableMapObject(int id) {
		enabledMap.put(id, true);
	}
	
	public Array<MapObject> getEnabledMapObjects() {
		Array<MapObject> ret = new Array<MapObject>();
		for(int id : objectMap.keys()) {
			if(enabledMap.get(id)) {
				ret.add(objectMap.get(id));
			}
		}
		return ret;
	}
	
	private void printIds() {
		System.out.println("ID Status\n---------");
		for(int id : objectMap.keys()) {
			System.out.println(objectMap.get(id) + " - " + enabledMap.get(id));
		}
		System.out.println();
	}
	
	/** Clears spawn map and disable map. Loads in spawns from new level */
//	private void initEntitySpawns() {
//		spawnMap.clear();
//		entityAdded.clear();
//		nextID = 1;
//		
//		if(currentLevel.getPlayerSpawn() != null) {
//			setPlayerSpawn(currentLevel.getPlayerSpawn());
//		}
//		for(EntitySpawn spawn : currentLevel.getEntitySpawns()) {
//			addSpawn(spawn);
//		}
//	}
//	
//	public int addSpawn(EntitySpawn spawn) {
//		spawnMap.put(nextID, new EntitySpawn(spawn));
//		entityAdded.put(nextID, true);
//		return nextID++;
//	}
//	
//	public void removeSpawn(int id){
//		if(id == 0) return;
//		entityAdded.put(id, false);
//	}
//	
//	public EntitySpawn getSpawn(int id){
//		return spawnMap.get(id);
//	}
//	
//	public void updateLevelSpawns(){
//		currentLevel.removeAllSpawns();
//		currentLevel.setPlayerSpawn(spawnMap.get(0));
//		
//		for(Integer id : spawnMap.keys()){
//			if(id == 0) continue;
//			if(entityAdded.get(id)) {
//				currentLevel.addEntitySpawn(spawnMap.get(id));
//			}
//		}
//	}
//	
//	public Array<EntitySpawn> getActiveSpawns(){
//		Array<EntitySpawn> spawns = new Array<Level.EntitySpawn>();
//		for(int id : spawnMap.keys()) {
//			if(entityAdded.get(id)) {
//				spawns.add(spawnMap.get(id));
//			}
//		}
//		return spawns;
//	}
//	
//	public int nextID(){
//		return nextID;
//	}
//	
//	public void setPlayerSpawn(EntitySpawn spawn){
//		spawnMap.put(0, spawn);
//		enableSpawn(0);
//	}
//	
//	public EntitySpawn getPlayerSpawn(){
//		return spawnMap.get(0);
//	}
//	
//	public void enableSpawn(int id){
//		entityAdded.put(id, true);
//	}
//	
//	public boolean isEnabled(int spawnID) {
//		return entityAdded.containsKey(spawnID) && entityAdded.get(spawnID);
//	}
	
	public void setWorldCamera(OrthographicCamera camera) {
		this.worldCamera = camera;
		actionManager.setWorldCamera(camera);
	}
	
	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
		actionManager.setHudCamera(hudCamera);
	}
	
	public void update(float delta) {
		animTime += delta;
		
		actionManager.update(delta);
		moveCamera(delta);
		
		unsavedEdits = history.size() != savePointer;
		
		saveLabel.setText((unsavedEdits ? "Unsaved Changes" : "Saved"));
		saveLabel.autoSetSize();
		
		autoTileLabel.setText("Auto-Tiling: " + (autoTiling ? "Yes" : "No"));
		autoTileLabel.setPosition(saveLabel.getX() + saveLabel.getWidth() + 30, saveLabel.getY());
		autoTileLabel.autoSetSize();
		
		actionLabel.setText("Action: " + actionManager.getCurrentAction().getDisplayName());
		actionLabel.autoSetSize();
		actionLabel.setPosition(autoTileLabel.getX() + autoTileLabel.getWidth() + 30, saveLabel.getY());
		
//		editorWindow.update(delta);
	}
	
	private void moveCamera(float delta) {
		if(KeyBoardManager.isControlDown() || KeyBoardManager.isShiftDown() || actionManager.isBlocking()){
			return;
		}
		if(Gdx.input.isKeyPressed(Keys.W)) {
			worldCamera.position.y += delta * moveVel * worldCamera.zoom * 0.5f + 0.15f;
		}
		if(Gdx.input.isKeyPressed(Keys.A)) {
			worldCamera.position.x -= delta * moveVel * worldCamera.zoom * 0.5f + 0.15f;
		}
		if(Gdx.input.isKeyPressed(Keys.S)) {
			worldCamera.position.y -= delta * moveVel * worldCamera.zoom * 0.5f + 0.15f; 
		}
		if(Gdx.input.isKeyPressed(Keys.D)) {
			worldCamera.position.x += delta * moveVel * worldCamera.zoom * 0.5f + 0.15f;
		}
	}
	
	private void handleInput() {
		// Adding rows/cols
		if(actionManager.isBlocking()) return;
		
		if (KeyBoardManager.isControlDown()) {
			if (Gdx.input.isKeyJustPressed(Keys.UP)) {
				executeCommand(new ResizeMapCommand(Direction.UP, true));
			}
			if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				executeCommand(new ResizeMapCommand(Direction.DOWN, true));
			}
			if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
				executeCommand(new ResizeMapCommand(Direction.LEFT, true));
			}
			if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				executeCommand(new ResizeMapCommand(Direction.RIGHT, true));
			}
		}

		// Removing rows/cols
		if (KeyBoardManager.isShiftDown()) {
			if (Gdx.input.isKeyJustPressed(Keys.UP)) {
				if(tileMap.rowAllEmpty(false)) executeCommand(new ResizeMapCommand(Direction.UP, false));
			}
			if (Gdx.input.isKeyJustPressed(Keys.DOWN)) {
				if(tileMap.rowAllEmpty(true)) executeCommand(new ResizeMapCommand(Direction.DOWN, false));
			}
			if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
				if(tileMap.colAllEmpty(true)) executeCommand(new ResizeMapCommand(Direction.LEFT, false));
			}
			if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
				if(tileMap.colAllEmpty(false)) executeCommand(new ResizeMapCommand(Direction.RIGHT, false));
			}
		}
	}
	
	public void render(SpriteBatch batch) {
		handleInput();
		worldCamera.update();
		hudCamera.update();
	
		mapRenderer.setView(worldCamera);
		mapRenderer.render(batch);
		
		// Render entity spawns
		batch.begin();
		
		SelectAction selectAction = null;
		if(actionManager.getCurrentAction() == EditorActions.SELECT) {
			selectAction = (SelectAction) actionManager.getCurrentActionInstance();
		}
		
		for(int id : objectMap.keys()) { 
			if(!enabledMap.get(id)) continue;
			
			MapObject mobj = objectMap.get(id);
			
			if(selectAction != null && selectAction.isSelected(mobj)) {
				batch.setColor(Color.WHITE);
			} else {
				batch.setColor(1.0f, 1.0f, 1.0f, 0.75f);
			}
			mobj.render(batch, mobj.getPos(), this);
			
//			EntitySpawn entitySpawn = spawnMap.get(id);
//			
//			boolean selected = selectAction == null ? false : selectAction.isSelected(entitySpawn);
//			EntityIndex index = entitySpawn.getIndex();
//			Vector2 pos = entitySpawn.getPos();
//			Animation<TextureRegion> animation = index.getIdleAnimation();
//			TextureRegion frame = animation.getKeyFrame(animTime);
//			
//			float w = frame.getRegionWidth();
//			float h = frame.getRegionHeight();
//			
//			float x = pos.x - w * 0.5f;
//			float y = pos.y - h * 0.5f;
//			
//			if(!entitySpawn.isFacingRight()) {
//				frame.flip(true, false);
//			}
//			
//			if(selected) {
//				batch.setColor(Color.WHITE);
//			} else {
//				batch.setColor(1.0f, 1.0f, 1.0f, 0.75f);
//			}
//			batch.draw(frame, x, y, w * 0.5f, h * 0.5f, w, h, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f);
//			frame.flip(frame.isFlipX(), false);
		}
		batch.setColor(Color.WHITE);
		batch.end();
		
		if(actionManager.renderInFront()) {
//			editorWindow.render(batch);
			tilePanel.render(hudCamera, batch);
			actionManager.render(batch);
		} else {
			actionManager.render(batch);
//			editorWindow.render(batch);
			tilePanel.render(hudCamera, batch);
		}
		
	}
	
	// **************************************************
	// *				TILE MAP STUFF					*
	// **************************************************
		
	public void placeTile(MapTile tile) {
		placeTile(tile, autoTiling);
	}
	
	public void setTile(int row, int col, MapTile tile){
		if(!editingTiles) throw new RuntimeException("Must begin tile editing before making changes to the tile map.");
		
		currTileChanges.addTile(row, col, tileMap.get(row, col));
		tileMap.add(row, col, tile);
	}
	
	/**
	 * WARNING using this method prevents UNDOs. Only use this method if you want immediate mode tilemap editing with no save states.
	 * @param row
	 * @param col
	 * @param tile
	 */
	public void unsafeSetTile(int row, int col, MapTile tile) {
		tileMap.add(row, col, tile);
	}
	
	public void addTile(int row, int col, MapTile tile){
		setTile(row, col, tile);
	}
	
	public void placeTile(MapTile tile, boolean autoTiling) {
		if(!editingTiles) throw new RuntimeException("Must begin tile editing before making changes to the tile map.");

		int row = tile.getRow();
		int col = tile.getCol();

		if(autoTiling) {
			TilePanel tilePanel = getTilePanel();
			
			TilesetTile tilesetTile = calculateTilesetTileAt(row, col, tilePanel.getActiveTile().getClusterID());
			tile.setId(tilesetTile.getTileID());

			currTileChanges.addTile(row, col, tileMap.get(row, col));
			tileMap.add(row, col, tile);
			
			executeCommand(new UpdateSurroundingTilesCommand(row, col));
		} else {
			currTileChanges.addTile(row, col, tileMap.get(row, col));
			tileMap.add(row, col, tile);
		}
	}
	
	public void eraseTile(int row, int col) {
		eraseTile(row, col, autoTiling);
	}
	
	public void eraseTile(int row, int col, boolean autoTiling) {
		if(!editingTiles) throw new RuntimeException("Must begin tile editing before making changes to the tile map.");
		
		if (tileMap.contains(row, col) && tileMap.get(row, col) != null) {
			currTileChanges.addTile(row, col, tileMap.get(row, col));
			tileMap.set(row, col, null);
			if(autoTiling) executeCommand(new UpdateSurroundingTilesCommand(row, col));
		}
	}
	
	public void updateTile(LevelEditor editor, int row, int col) {
		if(!editingTiles) throw new RuntimeException("Must begin tile editing before making changes to the tile map.");
		
		TilePanel tilePanel = editor.getTilePanel();
		if (!tileMap.contains(row, col) || tileMap.get(row, col) == null) return;
		MapTile mapTile = tileMap.get(row, col);
		currTileChanges.addTile(row, col, mapTile);

		Tileset tileset = tilePanel.getTileset();

		int clusterID = tileset.getClusterID(mapTile.getID());

		TilesetTile tile = calculateTilesetTileAt(row, col, clusterID);
		mapTile.setId(tile.getTileID());
		
		tileMap.set(row, col, mapTile);
	}
	
	public TilesetTile calculateTilesetTileAt(int row, int col, int clusterID) {
		TilePanel tilePanel = getTilePanel();

		Array<Side> sidesOpen = new Array<Side>(Side.class);

		if (isOpen(row + 1, col, clusterID)) sidesOpen.add(Side.NORTH);
		if (isOpen(row - 1, col, clusterID)) sidesOpen.add(Side.SOUTH);
		if (isOpen(row, col + 1, clusterID)) sidesOpen.add(Side.EAST);
		if (isOpen(row, col - 1, clusterID)) sidesOpen.add(Side.WEST);

		TileSlot slot = TileSlot.getSlot(sidesOpen.toArray());

		Tileset tileset = tilePanel.getTileset();
		TilesetTile tilesetTile = tileset.getTile(clusterID, slot);
		return tilesetTile;
	}

	public boolean isOpen(int row, int col, int clusterID) {
		TilePanel tilePanel = getTilePanel();
		return !tileMap.contains(row, col) || tileMap.get(row, col) == null || tileMap.get(row, col).getType() != TileType.GROUND || tilePanel.getTileset().getClusterID(tileMap.get(row, col).getID()) != clusterID;
	}
	
	public boolean contains(int row, int col) {
		return tileMap.contains(row, col);
	}
	
	public MapTile getTile(int row, int col) {
		return tileMap.get(row, col);
	}
	
	private void beginTile() {
		if(editingTiles) throw new RuntimeException("Already editing tiles.");
		editingTiles = true;
		currTileChanges = new TileChanges();
	}
	
	private void endTile() {
		if(!editingTiles) throw new RuntimeException("Begin was never called.");
		editingTiles = false;
		if(discardTileChanges) {
			discardTileChanges = false;
			return;
		}
		tileHistory.push(currTileChanges);
	}
	
	public void undoTile() {
		if(editingTiles) throw new RuntimeException("Can't undo tile changes while in the middle of editing.");
		if(tileHistory.isEmpty()) return;
		TileChanges tileChanges = tileHistory.pop();
		
//		System.out.println("Tile History Size: " + tileHistory.size());
		
		ArrayMap<GridPoint, MapTile> changes = tileChanges.getChanges();
		for(GridPoint point : changes.keys()){
			tileMap.set(point.row, point.col, changes.get(point));
		}
	}
	
	public void addRow(boolean above){
		tileMap.addRow(above);
	}
	
	public void addCol(boolean right){
		tileMap.addCol(right);
	}
	
	public void removeRow(boolean above, boolean allEmpty){
		tileMap.removeRow(above, allEmpty);
	}
	
	public void removeCol(boolean right, boolean allEmpty){
		tileMap.removeCol(right, allEmpty);
	}
	
	// ********************************************************************
	
	////////////////////////
	// 		  INPUT		  //
	////////////////////////
	
	@Override
	public void onMouseMove(MouseEvent ev) {
		mousePos = new Vector2(ev.getX(), ev.getY());
		mouseOnMap = !onTilePanel(mousePos.x, mousePos.y);
	}
	
	@Override
	public void onMouseDrag(MouseEvent ev) {
		mousePos = new Vector2(ev.getX(), ev.getY());
		mouseOnMap = !onTilePanel(mousePos.x, mousePos.y);
	}
	
	@Override
	public void onMouseUp(MouseEvent ev) {
		mouseDown = false;
	}
	
	@Override
	public void onMouseDown(MouseEvent ev) {
		mouseDown = true;
	}
	
	@Override
	public void onMouseEnter(MouseEvent ev) {
		
	}
	
	@Override
	public void onMouseExit(MouseEvent ev) {
		
	}
	
	@Override
	public void onKeyPress(KeyEvent ev) {
	}
	
	@Override
	public void onKeyRelease(KeyEvent ev) {
	}
	
	@Override
	public void onKeyType(KeyEvent ev) {
	}

	@Override
	public void onScroll(int amount) {
		if(actionManager.isBlocking()) return;
		worldCamera.zoom += amount * 0.02f;
		worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, 0.25f, 2.0f);
	}
	
//
//	@Override
//	public boolean scrolled(int amount) {
//		if(actionManager.isBlocking()) return super.scrolled(amount);
//		worldCamera.zoom += amount * 0.02f;
//		worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, 0.25f, 2.0f);
//		return super.scrolled(amount);
//	}
	
	public boolean onTilePanel(float screenX, float screenY) {
		return screenX >= tilePanel.getX() && screenX <= tilePanel.getX() + tilePanel.getWidth() &&
				screenY >= tilePanel.getY() && screenY <= tilePanel.getY() + tilePanel.getHeight();
	}
	
	public TilePanel getTilePanel() {
		return tilePanel;
	}
	
	public Vector2 toHudCoords(int screenX, int screenY) {
		Vector3 result = hudCamera.unproject(new Vector3(screenX, screenY, 0));
		return new Vector2(result.x, result.y);
	}
	
	public Vector2 toHudCoords(Vector2 coords) {
		return toHudCoords((int)coords.x, (int)coords.y);
	}
	
	public Vector2 toWorldCoords(float mouseX, float mouseY) {
		float x = mouseX * GameVars.PPM_INV * worldCamera.zoom;
		float y = mouseY * GameVars.PPM_INV * worldCamera.zoom;

		x += worldCamera.position.x - worldCamera.viewportWidth * 0.5f * worldCamera.zoom;
		y += worldCamera.position.y - worldCamera.viewportHeight * 0.5f * worldCamera.zoom;
		
		return new Vector2(x, y);
	}
	
	public Vector2 toWorldCoords(Vector2 coords) {
		return toWorldCoords(coords.x, coords.y);
	}
	
	public void onEnter(){
		Mouse.addScrollListener(this);
		editorWindow.setVisible(true);
		open = true;
	}
	
	public void onExit() {
		Mouse.removeScrollListener(this);
		editorWindow.setVisible(false);
		if(actionManager.getCurrentAction() == EditorActions.MOVE) {
			MoveAction moveAction = (MoveAction) actionManager.getCurrentActionInstance();
			moveAction.move();
		}
		open = false;
		getCurrentLevel(); // HACK Updates the level's object list
	}
	
	public boolean isMouseOnMap() {
		return mouseOnMap;
	}
	
	public boolean isMouseDown() {
		return mouseDown;
	}
	
	public Vector2 getMousePos() {
		return mousePos;
	}
	
	public Texture getEraseTexture() {
		return eraseTexture;
	}
	
	public Texture getSelectTexture() {
		return selectTexture;
	}
	
	public void madeChanges(){
		unsavedEdits = true;
	}
	
	public void saved() {
		unsavedEdits = false;
		savePointer = history.size();
	}
	
	public boolean requiresSaving() {
		return unsavedEdits;
	}
	
	public void setAutoTiling(boolean autoTiling) {
		this.autoTiling = autoTiling;
	}
	
	public boolean isAutoTiling() {
		return autoTiling;
	}
	
	public void executeCommand(Command command) {
		// If this is called while editing tiles, then this command must be called within another command
		// that edits tiles. Don't call begin or end if this is the case.
		boolean embeddedCall = editingTiles;
		
		if(!embeddedCall && command.editsTiles()) beginTile();
		command.execute(this);
		
		if(command.discard()) {
			discardTileChanges = command.editsTiles() && !embeddedCall; // Embedded calls can't determine if tile changes are discarded
			if(discardTileChanges) endTile();
			return; // If the command needs to be discarded, don't save it on the stack
		}
		if(!embeddedCall && command.editsTiles()) endTile();
//		System.out.println("Executing Command: " + command);
		
		// HACK Better system for embedded command calls
		if(!(command instanceof UpdateSurroundingTilesCommand)) history.add(command);
		
//		System.out.println("\nCommand History");
//		System.out.println(history);
//		
//		System.out.println("\nTile History");
//		System.out.println(tileHistory);
	}
	
	public void undo() {
		if(history.size() == 0) return;
		Command command = history.pop();
//		System.out.println("Undoing command: " + command);
		
		command.undo(this);
		
//		System.out.println("\nCommand History");
//		System.out.println(history);
//		
//		System.out.println("\nTile History");
//		System.out.println(tileHistory);
	}
	
	public Window getEditorWindow() {
		return editorWindow;
	}
	
	public ActionManager getActionManager() {
		return actionManager;
	}
}