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



/**
 * This class represents a single node in a graph
 * @author Dirk Reske
 *
 */
public class Node implements Serializable {

	private Coordinate coordinate;
	private String name;
	private EdgeList edges = new EdgeList();


	/**
	 * Creates a new Node
	 */
	public Node() {
		
	}
	
	/**
	 * Creates a new Node at a specified position
	 * @param coordinate The position the node is on
	 * @see Coordinate
	 */
	public Node(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	/**
	 * Creates a new Node at a specified position
	 * @param coordinate The position the node is on
	 * @see Coordinate
	 */
	public Node(Coordinate coordinate, String name) {
		this.coordinate = coordinate;
		this.name= name;
	}
	
	/**
	 * Gets the coordinates of the node
	 * @return The x coordinates
	 */
	public Coordinate getCoordinate() {
		return coordinate;
	}
	
	/**
	 * Gets the edges
	 * @return The edges
	 */
	public EdgeList getEdges() {
		return edges;
	}
	
	/**
	 * Gets the name of the node
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the node
	 * @param name The name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
