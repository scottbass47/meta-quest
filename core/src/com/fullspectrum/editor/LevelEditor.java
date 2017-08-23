package com.fullspectrum.editor;

import java.util.Iterator;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.fullspectrum.assets.AssetLoader;
import com.fullspectrum.editor.action.ActionManager;
import com.fullspectrum.editor.action.EditorActions;
import com.fullspectrum.editor.action.MoveAction;
import com.fullspectrum.editor.action.SelectAction;
import com.fullspectrum.editor.command.Command;
import com.fullspectrum.editor.command.ResizeMapCommand;
import com.fullspectrum.editor.command.ResizeMapCommand.Direction;
import com.fullspectrum.editor.command.UpdateSurroundingTilesCommand;
import com.fullspectrum.entity.EntityIndex;
import com.fullspectrum.game.GameVars;
import com.fullspectrum.gui.Label;
import com.fullspectrum.gui.Window;
import com.fullspectrum.level.ExpandableGrid;
import com.fullspectrum.level.GridPoint;
import com.fullspectrum.level.Level;
import com.fullspectrum.level.Level.EntitySpawn;
import com.fullspectrum.level.LevelUtils;
import com.fullspectrum.level.MapRenderer;
import com.fullspectrum.level.tiles.MapTile;
import com.fullspectrum.level.tiles.MapTile.Side;
import com.fullspectrum.level.tiles.MapTile.TileType;
import com.fullspectrum.level.tiles.TileSlot;
import com.fullspectrum.level.tiles.Tileset;
import com.fullspectrum.level.tiles.TilesetTile;

public class LevelEditor extends InputMultiplexer{

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
	private int ctrlCount = 0;
	private int shiftCount = 0;
	private float animTime;
	private boolean unsavedEdits = false;
	private boolean autoTiling = false;
	private int autoSaveInterval = 1000;
	private boolean open = false;
	private Array<InputProcessor> toRemove;
	
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
	private ArrayMap<Integer, EntitySpawn> spawnMap; // 0 is reserved for the player
	private ArrayMap<Integer, Boolean> entityAdded;
	private int nextID = 0;
	
	public LevelEditor() {
		tilePanel = new TilePanel();
		tilePanel.setX(0.0f);
		tilePanel.setY(0.0f);
		
		mapRenderer = new MapRenderer();
		mapRenderer.setGridLinesOn(true);
		mapRenderer.setTileset(tilePanel.getTileset());

		setupTextures();

		actionManager = new ActionManager(this);
		addProcessor(actionManager);
		
		history = new Stack<Command>();
		tileHistory = new Stack<TileChanges>();
		
		editorWindow = new Window();
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
		
		spawnMap = new ArrayMap<Integer, Level.EntitySpawn>();
		entityAdded = new ArrayMap<Integer, Boolean>();
		
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
						synchronized (currentLevel) {
							LevelUtils.saveLevel(getCurrentLevel());
							saved();
						}
					}
				}
			}
		}, "Editor Auto-Save");
		saveThread.setDaemon(true);
		saveThread.start();
		
		toRemove = new Array<InputProcessor>();
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
		updateLevelSpawns();
		return currentLevel;
	}
	
	public void setCurrentLevel(Level currentLevel) {
		if(this.currentLevel == currentLevel) return; // CLEANUP Bad check. Something more robust should be put in place to keep track of when levels are changing
		this.currentLevel = currentLevel;
		mapRenderer.setTileMap(currentLevel.getTileMap());
		tileMap = currentLevel.getTileMap();
		
		// Init the entity spawns
		initEntitySpawns();
	}
	
	/** Clears spawn map and disable map. Loads in spawns from new level */
	private void initEntitySpawns() {
		spawnMap.clear();
		entityAdded.clear();
		nextID = 1;
		
		if(currentLevel.getPlayerSpawn() != null) {
			setPlayerSpawn(currentLevel.getPlayerSpawn());
		}
		for(EntitySpawn spawn : currentLevel.getEntitySpawns()) {
			addSpawn(spawn);
		}
	}
	
	public int addSpawn(EntitySpawn spawn) {
		spawnMap.put(nextID, new EntitySpawn(spawn));
		entityAdded.put(nextID, true);
		return nextID++;
	}
	
	public void removeSpawn(int id){
		if(id == 0) return;
		entityAdded.put(id, false);
	}
	
	public EntitySpawn getSpawn(int id){
		return spawnMap.get(id);
	}
	
	public void updateLevelSpawns(){
		currentLevel.removeAllSpawns();
		currentLevel.setPlayerSpawn(spawnMap.get(0));
		
		for(Integer id : spawnMap.keys()){
			if(id == 0) continue;
			if(entityAdded.get(id)) {
				currentLevel.addEntitySpawn(spawnMap.get(id));
			}
		}
	}
	
	public Array<EntitySpawn> getActiveSpawns(){
		Array<EntitySpawn> spawns = new Array<Level.EntitySpawn>();
		for(int id : spawnMap.keys()) {
			if(entityAdded.get(id)) {
				spawns.add(spawnMap.get(id));
			}
		}
		return spawns;
	}
	
	public int nextID(){
		return nextID;
	}
	
	public void setPlayerSpawn(EntitySpawn spawn){
		spawnMap.put(0, spawn);
		enableSpawn(0);
	}
	
	public EntitySpawn getPlayerSpawn(){
		return spawnMap.get(0);
	}
	
	public void enableSpawn(int id){
		entityAdded.put(id, true);
	}
	
	public boolean isEnabled(int spawnID) {
		return entityAdded.containsKey(spawnID) && entityAdded.get(spawnID);
	}
	
	public void setWorldCamera(OrthographicCamera camera) {
		this.worldCamera = camera;
		actionManager.setWorldCamera(camera);
	}
	
	public void setHudCamera(OrthographicCamera hudCamera) {
		this.hudCamera = hudCamera;
		actionManager.setHudCamera(hudCamera);
		editorWindow.setHudCamera(hudCamera);
	}
	
	public void update(float delta) {
		for(Iterator<InputProcessor> iter = toRemove.iterator(); iter.hasNext();) {
			removeProcessor(iter.next());
			iter.remove();
		}

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
		
		editorWindow.update(delta);
	}
	
	private void moveCamera(float delta) {
		if(ctrlDown() || shiftDown() || actionManager.isBlocking()){
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
		
		if (ctrlDown()) {
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
		if (shiftDown()) {
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
		
		for(int id : spawnMap.keys()) { 
			if(!entityAdded.get(id)) continue;
			EntitySpawn entitySpawn = spawnMap.get(id);
			
			boolean selected = selectAction == null ? false : selectAction.isSelected(entitySpawn);
			EntityIndex index = entitySpawn.getIndex();
			Vector2 pos = entitySpawn.getPos();
			Animation<TextureRegion> animation = index.getIdleAnimation();
			TextureRegion frame = animation.getKeyFrame(animTime);
			
			float w = frame.getRegionWidth();
			float h = frame.getRegionHeight();
			
			float x = pos.x - w * 0.5f;
			float y = pos.y - h * 0.5f;
			
			if(!entitySpawn.isFacingRight()) {
				frame.flip(true, false);
			}
			
			if(selected) {
				batch.setColor(Color.WHITE);
			} else {
				batch.setColor(1.0f, 1.0f, 1.0f, 0.75f);
			}
			batch.draw(frame, x, y, w * 0.5f, h * 0.5f, w, h, GameVars.PPM_INV, GameVars.PPM_INV, 0.0f);
			frame.flip(frame.isFlipX(), false);
		}
		batch.setColor(Color.WHITE);
		batch.end();
		
		if(actionManager.renderInFront()) {
			editorWindow.render(batch);
			tilePanel.render(hudCamera, batch);
			actionManager.render(batch);
		} else {
			actionManager.render(batch);
			editorWindow.render(batch);
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
			tile.setId(tilesetTile.getID());

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
		mapTile.setId(tile.getID());
		
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
	public boolean keyDown(int keycode) {
//		actionManager.keyDown(keycode);
		if(keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftCount++;
		}
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlCount++;
		}
		return super.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
//		actionManager.keyUp(keycode);
		if(keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) {
			shiftCount--;
		}
		if(keycode == Keys.CONTROL_LEFT || keycode == Keys.CONTROL_RIGHT) {
			ctrlCount--;
		}
		return super.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
//		actionManager.keyTyped(character);
		return super.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//		actionManager.touchDown(screenX, screenY, pointer, button);
		mouseDown = true;
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//		actionManager.touchUp(screenX, screenY, pointer, button);
		mouseDown = false;
		return super.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		actionManager.touchDragged(screenX, screenY, pointer);
		mousePos = toHudCoords(screenX, screenY);
		mouseOnMap = !onTilePanel(mousePos.x, mousePos.y);
		return super.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
//		actionManager.mouseMoved(screenX, screenY);
		mousePos = toHudCoords(screenX, screenY);
		mouseOnMap = !onTilePanel(mousePos.x, mousePos.y);
		return super.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean scrolled(int amount) {
//		actionManager.scrolled(amount);
		if(actionManager.isBlocking()) return super.scrolled(amount);
		worldCamera.zoom += amount * 0.02f;
		worldCamera.zoom = MathUtils.clamp(worldCamera.zoom, 0.25f, 2.0f);
		return super.scrolled(amount);
	}
	
	public void removeInputProcessor(InputProcessor processor) {
		toRemove.add(processor);
	}
	
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
		open = true;
	}
	
	public void onExit() {
		if(actionManager.getCurrentAction() == EditorActions.MOVE) {
			MoveAction moveAction = (MoveAction) actionManager.getCurrentActionInstance();
			moveAction.move();
		}
		open = false;
		updateLevelSpawns();
	}
	
	public boolean shiftDown() {
		return shiftCount > 0;
	}
	
	public boolean ctrlDown() {
		return ctrlCount > 0;
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
}