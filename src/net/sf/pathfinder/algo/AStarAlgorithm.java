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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.pathfinder.model.Edge;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.Path;


/**
 * @author Dirk Reske
 * 
 */
public class AStarAlgorithm extends Algorithm {

	/**
	 * Creates a new A* Algorithm instance
	 */
	public AStarAlgorithm() {
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
		List<Node> closedList = new ArrayList<Node>();
		List<Node> openList = new ArrayList<Node>();
		Map<Node, NodeToken> tokenMap = new HashMap<Node, NodeToken>();

		tokenMap.put(start, new NodeToken(start, null, 0.0));
		openList.add(start);
		addBackTrackingNode(start);

		while (openList.size() > 0) {
			Node current = removeMin(openList, tokenMap);
			closedList.add(current);

			if (current.equals(destination)) {
				onAlgorithmStep("Path Found", buildPath(tokenMap, current),
						true, true);
				return;
			}

			for (Edge nextEdge : current.getEdges()) {
				Node next = nextEdge.getDestination();
				if (closedList.contains(next)) {
					continue;
				}

				double g = buildPath(tokenMap, current).getLength()
						+ nextEdge.getLength();
				double h = estimateDistance(next, destination);
				double f = g + h;

				if (tokenMap.containsKey(next)) {
					if (tokenMap.get(next).value > f) {
						NodeToken token = tokenMap.get(next);
						token.value = f;
						token.previousEdge = nextEdge;
					}
				} else {
					NodeToken token = new NodeToken(next, nextEdge, f);
					tokenMap.put(next, token);
					openList.add(next);
					addBackTrackingNode(next);
				}

				onAlgorithmStep("Step", buildPath(tokenMap, next), false, false);
			}
		}

		onAlgorithmStep("No Path found", null, true, false);
	}

	private Path buildPath(Map<Node, NodeToken> tokenMap, Node current) {
		Path path = new Path();

		path.getPathSteps().add(new Path.PathStep(current, null));

		NodeToken previous = tokenMap.get(current);
		while (previous.previousEdge != null) {
			path.getPathSteps().add(
					0,
					new Path.PathStep(previous.previousEdge.getSource(),
							previous.previousEdge));
			previous = tokenMap.get(previous.previousEdge.getSource());
		}
		return path;
	}

	private Node removeMin(List<Node> openList, Map<Node, NodeToken> tokenMap) {
		double min = 0;
		Node result = null;

		for (Node node : openList) {
			NodeToken token = tokenMap.get(node);
			if ((result == null) || (token.value < min)) {
				min = token.value;
				result = node;
			}
		}

		openList.remove(result);
		return result;
	}
}
