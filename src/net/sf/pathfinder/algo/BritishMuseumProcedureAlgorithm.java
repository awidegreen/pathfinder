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
import java.util.Stack;

import net.sf.pathfinder.model.Edge;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.Path;
import net.sf.pathfinder.util.properties.PropertyInfo;

public class BritishMuseumProcedureAlgorithm extends Algorithm {
	public BritishMuseumProcedureAlgorithm() {
		addProperty(new PropertyInfo(BASE_ALGORITHM,
				BASE_ALGORITHM_DESC,
				String.class,
				new String[] {BASE_ALGORITHM_BREADTHFIRST, BASE_ALGORITHM_DEPTHFIRST}),
					BASE_ALGORITHM_BREADTHFIRST);
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
		if (!britishMuseumProcedure(start, destination)) {
			onAlgorithmStep("No path found", new Path(), true, false);
		}
	}
	
	/**
	 * British-Museum-Search implementation
	 * @param start starting node
	 * @param goal destination node
	 * @return boolean if goal found or not
	 */
	private boolean britishMuseumProcedure(Node start, Node goal) {
		List<Node> visitedNodes = new ArrayList<Node>();
		List<Path> pathList = new ArrayList<Path>();
		Queue<NodeToken> queue = new LinkedList<NodeToken>();
		Stack<NodeToken> stack = new Stack<NodeToken>();
		
		if (getBaseAlgorithm().equals(BASE_ALGORITHM_BREADTHFIRST))
			queue.add(new NodeToken(start, null, null));
		else if (getBaseAlgorithm().equals(BASE_ALGORITHM_DEPTHFIRST)) 
			stack.push(new NodeToken(start, null, null, 0));
		
		while (!queue.isEmpty() || !stack.isEmpty()) {
			NodeToken currentNodeToken = null;
			if (getBaseAlgorithm().equals(BASE_ALGORITHM_BREADTHFIRST))
				currentNodeToken = queue.poll();
			else if (getBaseAlgorithm().equals(BASE_ALGORITHM_DEPTHFIRST)) 
				currentNodeToken = stack.pop();
			if (visitedNodes.contains(currentNodeToken.currentNode))
				continue; 
			if (currentNodeToken.currentNode.equals(goal)) {
				Path path = buildPath(new NodeToken(currentNodeToken.currentNode, currentNodeToken.previousEdge, currentNodeToken.prevNodeToken));
				onAlgorithmStep("Possible Path", path, false, true);
				pathList.add(path);
				continue;
			}
			addBackTrackingNode(currentNodeToken.currentNode);
			onAlgorithmStep("Visiting Node", buildPath(currentNodeToken), false, false);
			visitedNodes.add(currentNodeToken.currentNode);
			
			for (Edge edge : currentNodeToken.currentNode.getEdges()) {
				Node next = edge.getDestination();
				if (visitedNodes.contains(next))
					continue;
				
				NodeToken ntTmp = new NodeToken(next, edge, currentNodeToken);
				if (getBaseAlgorithm().equals(BASE_ALGORITHM_BREADTHFIRST) && !queue.contains(ntTmp))
					queue.add(ntTmp);
				else if (getBaseAlgorithm().equals(BASE_ALGORITHM_DEPTHFIRST) && !stack.contains(ntTmp)) 
					stack.push(ntTmp);
			}
		}
		
		if (pathList.isEmpty())
			return false;
		else {
			Path resultPath = pathList.get(0);
			for (Path path : pathList) {
				if (path.getLength() < resultPath.getLength())
					resultPath = path;
			}
			onAlgorithmStep("Path found", resultPath, true, true);
			return true;
		}
	}
}
