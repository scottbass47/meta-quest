
package com.cpubrew.arena;

import java.util.Iterator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import com.cpubrew.assets.AssetLoader;
import com.cpubrew.component.Mappers;
import com.cpubrew.component.RemoveComponent;
import com.cpubrew.entity.EntityIndex;
import com.cpubrew.entity.EntityManager;
import com.cpubrew.factory.EntityFactory;
import com.cpubrew.game.GameVars;
import com.cpubrew.utils.EntityUtils;

public class ArenaGame {

	private Arena arena;
	private ArrayMap<Integer, ArenaSpawn> spawnMap;
	private Array<ArenaWave> waves;
	private Array<Entity> currentWave;
	private ArrayMap<Integer, Queue<EntityIndex>> spawnQueue;
	private int waveNum = 0;
	private boolean queueEmpty = true;
	private float elapsed;
	private float interval; // how often do enemies spawn
	private boolean finished = true;
	private boolean shouldWait = false;

	// Rendering stuff
	private BitmapFont font;
	private GlyphLayout layout;
	private boolean drawingWave = false;
	private float duration = 2.0f;

	public ArenaGame(Arena arena, OrthographicCamera hudCamera) {
		this.arena = arena;
		
		spawnMap = new ArrayMap<Integer, ArenaSpawn>();
		waves = new Array<ArenaWave>();
		currentWave = new Array<Entity>();
		spawnQueue = new ArrayMap<Integer, Queue<EntityIndex>>();

		interval = 1.0f;

		font = AssetLoader.getInstance().getFont(AssetLoader.font48);
		layout = new GlyphLayout();
	}

	public void start() {
		reset();
		finished = false;
		createWave(waveNum);
	}

	public void reset() {
		currentWave.clear();
		spawnQueue.clear();
		waveNum = 0;
		queueEmpty = true;
		elapsed = 0.0f;
		finished = true;
		shouldWait = false;
		drawingWave = false;
		
		// Kill off existing enemies
		for(Entity entity : EntityFactory.engine.getEntities()){
			if(Mappers.player.get(entity) != null || (Mappers.parent.get(entity) != null && Mappers.player.get(Mappers.parent.get(entity).parent) != null)
					|| Mappers.camera.get(entity) != null) continue;
			entity.add(EntityFactory.engine.createComponent(RemoveComponent.class));
		}
	}

	private void createWave(int num) {
		spawnQueue.clear();
		ArenaWave wave = waves.get(num);

		ArrayMap<Integer, ArenaGroup> groupMap = wave.getGroupMap();

		for (int spawnID : groupMap.keys()) {
			ArenaGroup group = groupMap.get(spawnID);
			ArrayMap<EntityIndex, Integer> spawnMap = group.getSpawnAmount();
			spawnQueue.put(spawnID, new Queue<EntityIndex>());

			for (EntityIndex index : spawnMap.keys()) {
				int amount = spawnMap.get(index);
				for (int i = 0; i < amount; i++) {
					spawnQueue.get(spawnID).addLast(index);
				}
			}
		}
		queueEmpty = false;
		drawingWave = true;
	}

	private void spawnWave() {
		if (queueEmpty) return;
		boolean empty = true;
		for (int spawnID : spawnQueue.keys()) {
			ArenaSpawn arenaSpawn = spawnMap.get(spawnID);
			Vector2 pos = arenaSpawn.getPos();

			Queue<EntityIndex> queue = spawnQueue.get(spawnID);
			empty = empty && queue.size == 0; // wtf is this?

			if (queue.size == 0) continue;

			spawn(queue.removeFirst().create(pos.x, pos.y));
		}
		queueEmpty = empty;
	}

	private void spawn(Entity entity) {
		currentWave.add(entity);
		EntityManager.addEntity(entity);
	}

	public void update(float delta) {
		if(!EntityUtils.isValid(EntityUtils.getPlayer())) {
			arena.switchState(ArenaState.DEATH_SCREEN);
			return;
		}
		
		elapsed += delta;

		if (drawingWave && elapsed > duration) {
			drawingWave = false;
			elapsed = 0.0f;
		}
		else if (drawingWave || finished) {
			return;
		}

		if (elapsed > interval) {
			elapsed -= interval;
			spawnWave();
			shouldWait = true;
		}

		if (!shouldWait) {
			for (Iterator<Entity> iter = currentWave.iterator(); iter.hasNext();) {
				Entity entity = iter.next();
				if (!EntityUtils.isValid(entity)) {
					iter.remove();
				}
			}
		}

		if (queueEmpty && currentWave.size == 0 && !finished) {
			waveNum++;
			elapsed = 0.0f;
			if (waveNum == waves.size) {
				finished = true;
				drawingWave = true;
				return;
			}
			createWave(waveNum);
		}
		shouldWait = false;
	}

	public void renderHUD(SpriteBatch batch) {
		if (!drawingWave) return;

		batch.begin();

		String text = finished ? "Arena Completed" : "Wave " + (waveNum + 1);

		font.setColor(Color.WHITE);

		layout.setText(font, text);
		font.draw(batch, text, GameVars.SCREEN_WIDTH * 0.5f - layout.width * 0.5f, GameVars.SCREEN_HEIGHT * 0.5f + 150);

		batch.end();
	}

	public boolean isFinished() {
		return finished;
	}

	public void load(FileHandle config) {
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(config);

		interval = root.getFloat("spawn_interval", 1.0f);

		JsonValue spawnpoints = root.get("spawnpoints");

		for (Iterator<JsonValue> iter = spawnpoints.iterator(); iter.hasNext();) {
			JsonValue value = iter.next();
			int id = value.getInt("id");
			float x = value.getFloat("x");
			float y = value.getFloat("y");
			ArenaSpawn spawn = new ArenaSpawn(id, new Vector2(x, y));
			spawnMap.put(id, spawn);
		}

		JsonValue waves = root.get("waves");

		for (Iterator<JsonValue> iter = waves.iterator(); iter.hasNext();) {
			JsonValue value = iter.next();

			ArenaWave wave = new ArenaWave();
			for (Iterator<JsonValue> i = value.iterator(); i.hasNext();) {
				JsonValue v = i.next();
				ArenaGroup group = parseGroup(v);
				wave.addGroup(Integer.parseInt(v.name), group);
			}
			this.waves.add(wave);
		}
	}

	private ArenaGroup parseGroup(JsonValue value) {
		ArenaGroup group = new ArenaGroup();
		for (Iterator<JsonValue> iter = value.iterator(); iter.hasNext();) {
			JsonValue v = iter.next();

			EntityIndex index = EntityIndex.get(v.name);
			int amount = v.asInt();
			group.addEnemy(index, amount);
		}
		return group;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("-------------\n");
		builder.append("-   ARENA   -\n");
		builder.append("-------------\n\n");
		builder.append("-- Spawnpoints --\n");

		for (ArenaSpawn spawn : spawnMap.values()) {
			builder.append(spawn + "\n");
		}

		builder.append("\n-- Waves --\n\n");

		int counter = 1;
		for (ArenaWave wave : waves) {
			builder.append("Wave: " + counter++ + "\n");
			builder.append(wave + "\n\n");
		}

		return builder.toString();

	}

}
