package com.fullspectrum.ai;

import com.fullspectrum.level.NavLink;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;

public class XYAlignHeuristic implements PathHeuristic{

	@Override
	public float cost(NavLink link, NavMesh navMesh, Node goal) {
		int goalX = goal.getCol();
		int goalY = goal.getRow();
		int startX = link.fromNode.getCol();
		int startY = link.fromNode.getRow();
		int endX = link.toNode.getCol();
		int endY = link.toNode.getRow();
		
		// If you aren't aligned vertically, then first priority is to align vertically
		// without venturing too far away horizontally
		if(startY != goalY){
			float horizontalCost = Math.abs(endX - startX) * 5;
			float verticalCost = Math.abs(endY - goalY);
			return horizontalCost + verticalCost;
		}else{
			return Math.abs(endX - goalX);
		}
	}

}
