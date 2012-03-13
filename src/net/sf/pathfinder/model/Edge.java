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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A edge is the connection between two nodes
 * @author Dirk Reske
 *
 */
public class Edge implements Serializable {

	private Node source;
	private Node destination;
	private List<Coordinate> wayPoints = new ArrayList<Coordinate>();
	
	/**
	 * Creates a new edge
	 */
	public Edge() {
		
	}
	
	/**
	 * Creates a new edge
	 * @param source Source node of the edge
	 * @param destination Destination node of the edge
	 */
	public Edge(Node source, Node destination) {
		this.source = source;
		this.destination = destination;
	}

	/**
	 * Gets the length of the edge
	 * @return The length
	 */ 
	public double getLength() {
		double length = 0;
		Coordinate previous = getSource().getCoordinate();		
		for (Coordinate wayPoint : wayPoints) {
			length += previous.distance(wayPoint);
			previous = wayPoint;
		}
		
		length += previous.distance(getDestination().getCoordinate());
		return length;
	}
	
	/**
	 * Gets the source node
	 * @return The source node
	 */
	public Node getSource() {
		return source;
	}
	
	/**
	 * Sets the source node of the edge
	 * @param source The source node
	 */
	public void setSource(Node source) {
		this.source = source;
	}
	
	/**
	 * Gets the destination node
	 * @return The destination node
	 */
	public Node getDestination() {
		return destination;
	}

	/**
	 * Sets the destination node of the edge
	 * @param destination The destination node
	 */
	public void setDestination(Node destination) {
		this.destination = destination;
	}
	
	/**
	 * Gets the coordinates
	 * @return The coordinates
	 */
	public List<Coordinate> getWayPoints() {
		return wayPoints;
	}

	

	
}
