package com.fullspectrum.entity;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class EntityLoader {

	private static ArrayMap<EntityIndex, EntityStats> statsMap;

	static {
		Thread poll = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Path dir = Paths.get(Gdx.files.internal("config").path());
					WatchService watcher = FileSystems.getDefault().newWatchService();

					WatchKey key = dir.register(watcher, ENTRY_MODIFY);
					key.pollEvents();
					for (;;) {
						if (key.pollEvents().size() == 0) Thread.sleep(10);
						for (WatchEvent<?> event : key.pollEvents()) {
							WatchEvent<Path> ev = cast(event);
							Path name = ev.context();
							
							String indexName = name.toString().replaceAll(".json", "");
							EntityIndex index = EntityIndex.get(indexName);

							if(index != null) {
								synchronized(statsMap) {
									statsMap.put(index, load(index));
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "hotloading");
		poll.setDaemon(true);
		poll.start();

		statsMap = new ArrayMap<EntityIndex, EntityStats>();
		for (EntityIndex index : EntityIndex.values()) {
			statsMap.put(index, load(index));
		}
	}

	 @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
	
	public static EntityStats load(EntityIndex index) {
		EntityStats ret = new EntityStats(index);
		String file = Gdx.files.internal("config/" + index.getName() + ".json").readString();
		JsonReader reader = new JsonReader();
		JsonValue root = reader.parse(file);
		for (Iterator<JsonValue> iter = root.iterator(); iter.hasNext();) {
			JsonValue child = iter.next();
			String attribute = child.name();
			float value = child.asFloat();
			ret.set(attribute, value);
		}
		return ret;
	}

	public static EntityStats get(EntityIndex index) {
		return statsMap.get(index);
	}

}
