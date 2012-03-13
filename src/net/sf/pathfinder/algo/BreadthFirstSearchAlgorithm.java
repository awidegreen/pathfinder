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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.sf.pathfinder.model.Edge;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.Path;
import net.sf.pathfinder.util.properties.PropertyInfo;

/**
 * @author Armin Widegreen
 * 
 */
public class BreadthFirstSearchAlgorithm extends Algorithm {
	
	public BreadthFirstSearchAlgorithm() {
		addProperty(new PropertyInfo(STORAGE_LIMIT,
				STORAGE_LIMIT_DESC,
				Integer.class,
				new Object[] {PropertyInfo.RANGE, 0, STORAGE_LIMIT_MAX}), 
					0);
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
		if (!breadthFirstSearch(start, destination)) {
			onAlgorithmStep("No path found", new Path(), true, false);
		}
	}
	
	/**
	 * Breadth-First-Search implementation
	 * @see http://en.wikipedia.org/wiki/Breadth-first_search
	 * @param start starting node
	 * @param goal destination node
	 * @return boolean if goal found or not
	 */
	private boolean breadthFirstSearch(Node start, Node goal) {
		List<Node> visitedNodes = new ArrayList<Node>();
		Queue<NodeToken> queue = new LinkedList<NodeToken>();
		
		queue.add(new NodeToken(start, null, null));
		while (!queue.isEmpty()) {
			NodeToken currentNodeToken = queue.poll();
			if (visitedNodes.contains(currentNodeToken.currentNode))
				continue;
			if (currentNodeToken.currentNode.equals(goal)) {
				onAlgorithmStep("Path found", buildPath(new NodeToken(currentNodeToken.currentNode, currentNodeToken.previousEdge, currentNodeToken.prevNodeToken)), true, true);
				return true;
			}
			addBackTrackingNode(currentNodeToken.currentNode);
			onAlgorithmStep("Visiting Node", buildPath(currentNodeToken), false, false);
			visitedNodes.add(currentNodeToken.currentNode);
			
			List<NodeToken> tmpList = new ArrayList<NodeToken>();
			for (Edge edge : currentNodeToken.currentNode.getEdges()) {
				Node next = edge.getDestination();
				if (visitedNodes.contains(next))
					continue;
				tmpList.add(new NodeToken(next, edge, currentNodeToken));		
			}
			
			if (getStorageSize() == 0) {
				for (NodeToken nodeToken : tmpList) 
					queue.add(nodeToken);
			} else {
				for (int i=0 ; i<getStorageSize() && i<tmpList.size() ; i++)
					queue.add(tmpList.get(i));
			}
		}
	
		return false;
	}
}
