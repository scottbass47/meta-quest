package com.fullspectrum.level.tiles;

import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TilesetLoader {

	public TilesetLoader() {
		
	}
	
	public Tileset load(FileHandle atlasHandle) {
		Tileset tileset = new Tileset();
		
		String atlas = atlasHandle.readString();
		Scanner scanner = new Scanner(atlas);
		
		String imageFile = scanner.nextLine();
		FileHandle imageHandle = atlasHandle.sibling(imageFile);
		
		String size = scanner.nextLine();
		String[] dimensions = size.replace("size:", "").trim().split(",");
		
		int width = Integer.parseInt(dimensions[0].trim());
		int height = Integer.parseInt(dimensions[1].trim());
		
		tileset.setWidth(width);
		tileset.setHeight(height);
		
		scanner.nextLine();
		
		Array<TilesetTile> tiles = new Array<TilesetTile>();

		Array<String> currentTile = new Array<String>();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if(line.startsWith("tile")) {
				if(currentTile.size > 0) {
					tiles.add(parseTilesetTile(currentTile));
				}
				currentTile.clear();
			}
			if(currentTile != null) currentTile.add(line.trim());
		}
		scanner.close();
		
		tileset.addTiles(tiles);
		tileset.setTilesheet(new TextureRegion(new Texture(imageHandle)));
		return tileset;
	}

	private TilesetTile parseTilesetTile(Array<String> currentTile) {
		TilesetTile tile = new TilesetTile();
		
		int id = Integer.parseInt(currentTile.removeIndex(0).split("[\\[\\]]")[1]);
		tile.setTileID(id);
		
		for(String line : currentTile) {
			TileProperties property = TileProperties.getProperty(line);
			String value = line.split(":")[1].trim();
			
			switch (property) {
			case CLUSTER:
				tile.setClusterID(Integer.parseInt(value));
				break;
			case SLOT:
				tile.setSlot(TileSlot.parse(value));
				break;
			case XY:
				String[] parts = value.replace(" ", "").split(",");
				tile.setSheetX(Integer.parseInt(parts[0]));
				tile.setSheetY(Integer.parseInt(parts[1]));
				break;
			default:
				System.out.println("No property '" + line.split(":")[0] + "' exists.");
				break;
			}
		}
		return tile;
	}
	
	public enum TileProperties {
		SLOT,
		XY,
		CLUSTER;
		
		public static TileProperties getProperty(String line) {
			for(TileProperties property : values()) {
				if(line.startsWith(property.name().toLowerCase())) return property;
			}
			return null;
		}
	}
	
	
}
