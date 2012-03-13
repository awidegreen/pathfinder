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

/**
 * Provides basic string utility methods
 * @author Dirk Reske
 *
 */
public final class StringUtils {

	private StringUtils() {
		
	}
	
	/**
	 * Inidicates whether a string is null or empty
	 * @param value The string to test
	 * @return True if the string is null or empty, false otherwise
	 */
	public static boolean isNullOrEmpty(String value) {
		return (value == null) || ("".equals(value.trim()));
	}
}
