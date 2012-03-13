/****************************************************************************************
 * Copyright (c) 2009 Armin Widegreen <armin.widegreen@gmail.com>                       *
 *                    Dirk Reske <email@dirkreske.de>                                   *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation, either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/ 

package net.sf.pathfinder.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import net.sf.pathfinder.model.Edge;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.Path;
import net.sf.pathfinder.util.properties.PropertyInfo;


/**
 * 
 * @author Armin Widegreen
 *
 */

public class DijkstraAlgorithm extends Algorithm {
	//TODO: must be implemented
	public DijkstraAlgorithm() {
		addProperty(new PropertyInfo(STORAGE_LIMIT,
				STORAGE_LIMIT_DESC,
				Integer.class,
				new Object[] {PropertyInfo.RANGE, 0, STORAGE_LIMIT_MAX}), 
				  STORAGE_LIMIT_DEFAULT);
	}
	
	/**
	 * Finds the shortest way between the two specified nodes in the graph
	 * 
	 * @param graph
	 *            Graph to use for calculation
	 * @param start
	 *            Start node for the path
	 * @param destination
	 *            Destination node for the path
	 * @return The shortest path
	 */
	@Override
	protected void calculateRoute(Graph graph, Node start, Node destination) {
		if (!dijkstra(start, destination)) {
			onAlgorithmStep("No path found", new Path(), true, false);
		}
	}

	/**
	 * DijkstraAlgorithm implementation
	 * @see http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
	 * @param start node
	 * @param goal destination node
	 * @return boolean if goal found or not
	 */
	private boolean dijkstra(Node start, Node goal) {

		List<Node> visitedNodes = new ArrayList<Node>();
		Stack<NodeToken> stack = new Stack<NodeToken>();
		
		stack.push(new NodeToken(start, null, null, 0));
		while (!stack.isEmpty()) {
			NodeToken currentNodeToken = stack.pop();
			if (visitedNodes.contains(currentNodeToken.currentNode))
				continue;
			if (currentNodeToken.currentNode.equals(goal)) {
				onAlgorithmStep("Path found", buildPath(new NodeToken(currentNodeToken.currentNode, currentNodeToken.previousEdge, 
						currentNodeToken.prevNodeToken)), true, true);
				return true;
			}
			addBackTrackingNode(currentNodeToken.currentNode);
			onAlgorithmStep("Visiting Node", buildPath(currentNodeToken), false, false);
			visitedNodes.add(currentNodeToken.currentNode);
			
			List<NodeToken> tmpList = new ArrayList<NodeToken>();
			for (Edge edge : currentNodeToken.currentNode.getEdges()) {
				Node next = edge.getDestination();
				double value = buildPath(new NodeToken(next, edge, currentNodeToken)).getLength();
				value += estimateDistance(next, goal);
				NodeToken nt = new NodeToken(next, edge, currentNodeToken, value);
				
				int toDel = -1;
				boolean doAdd = true;
				for (int i=0 ; i<stack.size() ; i++) {
					if (nt.currentNode.equals(stack.get(i).currentNode)) {
						if (stack.get(i).value > nt.value)
							toDel = i;
						else 
							doAdd = false;
					}
				}
				if (toDel >= 0) 
					stack.remove(toDel);
				if (doAdd)
					tmpList.add(nt);
			}
			
			if (getStorageSize() == 0) {
				for (NodeToken nodeToken : tmpList) 
					stack.push(nodeToken);
			} else {
				for (int i=0 ; i<getStorageSize() && i<tmpList.size() ; i++)
					stack.push(tmpList.get(i));
			}
			
			//sort NodeToken.value
			Collections.sort(stack);
			//reverse because stack (LIFO) > bring shortest to front
			Collections.reverse(stack);
		}
	
		return false;
	}
}
