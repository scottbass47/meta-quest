package com.cpubrew.ai;

import com.cpubrew.level.NavLink;
import com.cpubrew.level.NavMesh;
import com.cpubrew.level.Node;

public interface PathHeuristic {

	public float cost(NavLink link, NavMesh navMesh, Node goal);
	
}