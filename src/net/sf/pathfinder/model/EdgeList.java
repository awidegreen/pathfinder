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

/**
 * A collection of edges
 * @author Dirk Reske
 *
 */
public class EdgeList extends ArrayList<Edge> {

	/**
	 * Finds the edge with the specified destination
	 * @param destination Destination of the edge to find
	 * @return The edge or null, if no appropriate edge was found
	 */
	public Edge findEdge(Node destination) {
		for (Edge edge : this) {
			if (edge.getDestination().equals(destination)) {
				return edge;
			}
		}
		return null;
	}
}
