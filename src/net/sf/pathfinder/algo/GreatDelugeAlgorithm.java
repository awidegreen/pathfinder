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

public class GreatDelugeAlgorithm extends Algorithm {
	
	public GreatDelugeAlgorithm() {
		addProperty(new PropertyInfo(CLIMBING_FUNCTION, 
				CLIMBING_FUNCTION_DESC,
				String.class,
				new String[] {CLIMBING_FUNCTION_EXP, CLIMBING_FUNCTION_COS }),
				CLIMBING_FUNCTION_EXP);
		addProperty(new PropertyInfo(CLIMBING_SCALE_BASIS, 
				CLIMBING_SCALE_BASIS_DESC,
				String.class,
				new String[] {CLIMBING_SCALE_BASIS_FACTOR, CLIMBING_SCALE_BASIS_14PI }),
				CLIMBING_SCALE_BASIS_FACTOR);
		addProperty(new PropertyInfo(CLIMBING_SCALE_FACTOR,
				CLIMBING_SCALE_FACTOR_DESC,
				Integer.class,
				new Object[] {PropertyInfo.RANGE, 0, 10000}), 
					CLIMBING_SCALE_FACTOR_DEFAULT);
		addProperty(new PropertyInfo(CLIMBING_GRADIENT_FACTOR,
				CLIMBING_GRADIENT_FACTOR_DESC,
				Double.class,
				new Object[] {PropertyInfo.RANGE, 0.001, 2.0, 0.001}), 
					CLIMBING_GRADIENT_FACTOR_DEFAULT);
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
		  List<Node> visitedNodes = new ArrayList<Node>();
		  visitedNodes.add(start);
		  if (!greatDelugeSearch(graph, start, destination)) {
			  onAlgorithmStep("No path found", new Path(), true, false);
		  }
	}

	/**
	 * GreatDelugeAlgorithm implementation
	 * @see http://en.wikipedia.org/wiki/Great_Deluge_algorithm
	 * @return boolean if goal found or not
	 */
	private boolean greatDelugeSearch(Graph graph, Node start, Node goal) {
		List<Node> visitedNodes = new ArrayList<Node>();
		Stack<NodeToken> stack = new Stack<NodeToken>();
		double fountain = calculateClimbGrade(graph.getWidth(), graph.getHeight(), start, goal);
		double gradientFactor = getGradientFactor();
		
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
			
			ArrayList<NodeToken> tmpList = new ArrayList<NodeToken>();
			for (Edge edge : currentNodeToken.currentNode.getEdges()) {
				Node next = edge.getDestination();
				if (visitedNodes.contains(next))
					continue;
				
				double grade = calculateClimbGrade(graph.getWidth(), graph.getHeight(), next, goal);
				if (grade < fountain)
					continue;
				tmpList.add(new NodeToken(next, edge, currentNodeToken, grade));
			}
			
			fountain += gradientFactor;
			Collections.sort(tmpList);
			
			for (NodeToken nodeToken : tmpList) {
				stack.push(nodeToken);
			}
		}
	
		return false;
	}
}
