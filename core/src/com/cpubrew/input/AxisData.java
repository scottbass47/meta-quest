package com.cpubrew.input;

public class AxisData {

	public String dir;
	public int axisNum;
	
	public AxisData(String dir, int axisNum){
		this.dir = dir;
		this.axisNum = axisNum;
	}
	
	@Override
	public String toString(){
		return axisNum + ", " + dir;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AxisData)) return false;
		AxisData axisData = (AxisData)obj;
		return axisData.dir.equals(dir) && axisData.axisNum == axisNum;
	}
	
	@Override
	public int hashCode(){
		int result = 17;
		result = 31 * result + (dir.equals("neg") ? 2 : 3);
		result = 31 * result + axisNum;
		return result;
	}
}
