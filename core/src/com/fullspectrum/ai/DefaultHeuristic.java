package com.fullspectrum.ai;

import com.fullspectrum.level.NavLink;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;

public class DefaultHeuristic implements PathHeuristic{
	@Override
	public float cost(NavLink link, NavMesh navMesh, Node goal) {
		return link.cost;
	}
}
