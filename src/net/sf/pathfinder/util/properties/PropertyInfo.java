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

package net.sf.pathfinder.util.properties;

import java.io.Serializable;

/**
 * Information about a property
 * @author Dirk Reske
 *
 */
public class PropertyInfo implements Serializable {

	public static final String RANGE = "Range";
	
	private String name;
	private String description;
	private Class<?> type;
	private Object[] possibleValues;

	/**
	 * Creates a new PropertyInfo
	 * @param name Name of the property
	 * @param description Description of the property
	 */
	public PropertyInfo(String name, String description, Class<?> type, Object[] possibleValues) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.possibleValues = possibleValues;
	}

	/**
	 * Gets the name of the property
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the description of the property
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the type of the property
	 * @return The type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * Gets the possible values for the property
	 * @return Possible values
	 */
	public Object[] getPossibleValues() {
		return possibleValues;
	}
}
