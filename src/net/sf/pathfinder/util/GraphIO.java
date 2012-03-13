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

package net.sf.pathfinder.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.pathfinder.model.Coordinate;
import net.sf.pathfinder.model.Edge;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;


/**
 * Provides methods for loading and storing {@link Graph} instances
 * @author Dirk Reske
 *
 */
public final class GraphIO {

	private GraphIO() {
		
	}
	
	/**
	 * Loads a graph
	 * @param file The file to load the graph from
	 * @return The graph
	 */
	public static Graph loadGraph(String file) {
		try {
			JAXBContext context = JAXBContext.newInstance(GraphElement.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			GraphElement graphElement = (GraphElement) unmarshaller.unmarshal(new File(file));

			Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
			//read nodes
			for (NodeElement nodeElement : graphElement.nodes) {
				Coordinate coordinate = new Coordinate(nodeElement.x, nodeElement.y);
				nodeMap.put(nodeElement.nodeId, new Node(coordinate, nodeElement.name));
			}

			for (EdgeElement edgeElement : graphElement.edges) {
				//Its already done at saving...but just go save
				if (edgeElement.destinationId == edgeElement.sourceId) {
					continue;
				}
				Node source = nodeMap.get(edgeElement.sourceId);
				Node destination = nodeMap.get(edgeElement.destinationId);

				Edge edge = new Edge(source, destination);
				for (WayPointElement wayPoint : edgeElement.wayPoints) {
					edge.getWayPoints().add(new Coordinate(wayPoint.x, wayPoint.y));
				}
				source.getEdges().add(edge);
			}
			Graph graph = new Graph();
			graph.setWidth(graphElement.width);
			graph.setHeight(graphElement.height);
			graph.getNodes().addAll(nodeMap.values());

			return graph;
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Saves a {@link Graph}
	 * @param graph The graph
	 * @param file The file to save the graph to
	 */
	public static void saveGraph(Graph graph, String file) {
		int currentIndex = 1;

		GraphElement graphElement = new GraphIO.GraphElement();
		graphElement.width = graph.getWidth();
		graphElement.height = graph.getHeight();

		Map<Node, Integer> nodeMap = new HashMap<Node, Integer>();
		for (Node node : graph.getNodes()) {
			NodeElement nodeElement = new NodeElement();
			nodeElement.nodeId = currentIndex++;
			nodeElement.name = node.getName();
			nodeElement.x = node.getCoordinate().getX();
			nodeElement.y = node.getCoordinate().getY();

			graphElement.nodes.add(nodeElement);
			nodeMap.put(node, nodeElement.nodeId);
		}

		for (Node node : graph.getNodes()) {
			for (Edge edge : node.getEdges()) {
				if (edge.getSource().equals(edge.getDestination())) {
					//Don't store edges with one node as source and destination
					continue;
				}

				EdgeElement edgeElement = new EdgeElement();
				edgeElement.sourceId = nodeMap.get(edge.getSource());
				edgeElement.destinationId = nodeMap.get(edge.getDestination());

				for (Coordinate wayPoint : edge.getWayPoints()) {
					WayPointElement wayPointElement = new WayPointElement();
					wayPointElement.x = wayPoint.getX();
					wayPointElement.y = wayPoint.getY();
					edgeElement.wayPoints.add(wayPointElement);
				}

				graphElement.edges.add(edgeElement);
			}
		}
		try {
			JAXBContext context = JAXBContext.newInstance(GraphElement.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(graphElement, new File(file));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	@XmlRootElement(name = "graph")
	private static class GraphElement {

		@XmlAttribute(name = "width")
		public int width;

		@XmlAttribute(name = "height")
		public int height;

		@XmlElements(@XmlElement(type = NodeElement.class, name = "node"))
		public List<NodeElement> nodes = new ArrayList<NodeElement>();

		@XmlElements(@XmlElement(type = EdgeElement.class, name = "edge"))
		public List<EdgeElement> edges = new ArrayList<EdgeElement>();
	}

	@XmlRootElement(name = "node")
	private static class NodeElement {
		@XmlAttribute(name = "id")
		public int nodeId;

		@XmlAttribute(name = "name")
		public String name;

		@XmlAttribute(name = "x")
		public int x;

		@XmlAttribute(name = "y")
		public int y;
	}

	@XmlRootElement(name = "edge")
	private static class EdgeElement {
		@XmlAttribute(name = "source")
		public int sourceId;

		@XmlAttribute(name = "destination")
		public int destinationId;

		@XmlElements(@XmlElement(type = WayPointElement.class, name = "waypoint"))
		public List<WayPointElement> wayPoints = new ArrayList<WayPointElement>();
	}

	@XmlRootElement(name = "waypoint")
	private static class WayPointElement {
		@XmlAttribute(name = "x")
		public int x;

		@XmlAttribute(name = "y")
		public int y;
	}
}
