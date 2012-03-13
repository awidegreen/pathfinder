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
 * This class represents a single coordinate
 * 
 * @author Dirk Reske
 * 
 */
public class Coordinate implements Serializable {

	private int x;
	private int y;

	/**
	 * Creates a new Coordinate
	 */
	public Coordinate() {

	}

	/**
	 * Calculates the distance between this coordinate and the specified
	 * 
	 * @param c2
	 *            The second coordinate
	 * @return The distance
	 */
	public double distance(Coordinate c2) {
		return distance(this, c2);
	}

	/**
	 * Calculates the distance between the two coordinates
	 * 
	 * @param c1
	 *            First coordinate
	 * @param c2
	 *            Second coordinate
	 * @return The distance
	 */
	public static double distance(Coordinate c1, Coordinate c2) {
		int x = c2.x - c1.x;
		int y = c2.y - c1.y;
		return Math.sqrt((x * x) + (y * y));
	}

	/**
	 * Creates a new Coordinate
	 * 
	 * @param x
	 *            The x value of the coordinate
	 * @param y
	 *            The y value of the coordinate
	 */
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the x value of the coordinate
	 * 
	 * @return The x value
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y value of the coordinate
	 * 
	 * @return The y value
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns a has code value for the object
	 * 
	 * @return a hash code value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/**
	 * Indicates whether some other object is equal to this one
	 * 
	 * @param obj
	 *            the object to compare with
	 * @return true if this object is the same as the obj argument, false
	 *         otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Coordinate))
			return false;
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
