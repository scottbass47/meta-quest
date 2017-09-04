package com.cpubrew.ai;

import com.cpubrew.level.NavLink;
import com.cpubrew.level.NavMesh;
import com.cpubrew.level.Node;

public class DefaultHeuristic implements PathHeuristic{
	@Override
	public float cost(NavLink link, NavMesh navMesh, Node goal) {
		return link.cost;
	}
}
