package com.fullspectrum.level;

public class LevelInfo {

	private Theme theme;
	private LevelType type;
	private int level;
	private int secret;
	private int section;
	
	public LevelInfo(Theme theme, LevelType type, int level, int secret, int section){
		this.theme = theme;
		this.type = type;
		this.level = level;
		this.secret = secret;
		this.section = section;
	}
	
	public String toFileFormat(){
		if(type.equals(LevelType.HUB)){
			return theme + "-" + type;
		}else if(type.equals(LevelType.LEVEL)){
			return theme + "-" + type + "-" + level + "-" + section;
		}else{
			return theme + "-" + type + "-" + level + "-" + secret + "-" + section;
		}
	}
	
	public String toFileFormatExtension(){
		return toFileFormat() + ".tmx";
	}
	
	public Theme getTheme(){
		return theme;
	}
	
	public LevelType getLevelType(){
		return type;
	}
	
	public int getLevel(){
		return level;
	}
	
	public int getSecret(){
		return secret;
	}
	
	public int getSection(){
		return section;
	}
	
	public boolean isHub(){
		return type == LevelType.HUB;
	}
	
	public boolean isSecret(){
		return type == LevelType.SECRET;
	}
	
	public boolean isLevel(){
		return type == LevelType.LEVEL;
	}
	
	@Override
	public String toString() {
		return toFileFormat();
	}
	
	public enum LevelType{
		HUB,
		LEVEL,
		SECRET;
		
		public String lowerCaseName(){
			return name().toLowerCase();
		}
		
		@Override
		public String toString() {
			return lowerCaseName();
		}
		
		public static LevelType get(String name){
			for(LevelType type : LevelType.values()){
				if(type.name().equalsIgnoreCase(name)) return type;
			}
			return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + secret;
		result = prime * result + section;
		result = prime * result + ((theme == null) ? 0 : theme.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LevelInfo other = (LevelInfo) obj;
		if (level != other.level)
			return false;
		if (secret != other.secret)
			return false;
		if (section != other.section)
			return false;
		if (theme != other.theme)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
