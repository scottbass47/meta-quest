package com.cpubrew.entity;

public enum CoinType {

	SILVER(1, 10),
	GOLD(11, 30),
	BLUE(31, 100);
	
	// Min and Max boundaries inclusive
	private int min;
	private int max;
	
	
	private CoinType(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	public static CoinType getCoin(int value){
		for(CoinType coin : CoinType.values()){
			if(coin.min <= value && coin.max >= value) return coin;
		}
		return null;
	}
	
	public static int getLowestCoinValue(){
		return 1; // silver
	}
	
	public static int getHighestCoinValue(){
		return 100; // blue
	}
	
}
