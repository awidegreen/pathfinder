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

package net.sf.pathfinder.model;


import java.util.ArrayList;
import java.util.List;

import net.sf.pathfinder.util.GraphIO;
import net.sf.pathfinder.util.StringUtils;



public class Graph {

	private NodeList nodes = new NodeList();
	private int width;
	private int height;
	private int endNodes = -1;
	private int edgeCount = -1;
	
	/**
	 * Creates a new graph
	 */
	public Graph() {
		
	}
	
	/**
	 * Gets the graph nodes
	 * @return The graph nodes
	 */
	public NodeList getNodes() {
		return nodes;
	}
	
	public void save(String file) {
		GraphIO.saveGraph(this, file);
	}
	
	/**
	 * Loads a graph from file
	 * @param filename The file
	 * @return A graph
	 * @throws IOException If any error occures
	 */
	public static Graph load(String filename) {
		return GraphIO.loadGraph(filename);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getEdgeCount() {
		if (edgeCount < 0) {
			List<Edge> visited = new ArrayList<Edge>();
			for (Node node : getNodes()) {
				for (Edge edge : node.getEdges()) {
					if (visited.contains(edge)) {
						continue;
					}
					visited.add(edge);
					edgeCount++;
				}
			}
		}
		return edgeCount;
	}
	public int getEndNodesCount() {
		if (endNodes < 0) {
			for (Node node: getNodes()) {
				if (!StringUtils.isNullOrEmpty(node.getName())) {
					endNodes++;
				}
			}
		}
		return endNodes;
	}
}
