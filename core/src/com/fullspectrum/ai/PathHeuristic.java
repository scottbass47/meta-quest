package com.fullspectrum.ai;

import com.fullspectrum.level.NavLink;
import com.fullspectrum.level.NavMesh;
import com.fullspectrum.level.Node;

public interface PathHeuristic {

	public float cost(NavLink link, NavMesh navMesh, Node goal);
	
}