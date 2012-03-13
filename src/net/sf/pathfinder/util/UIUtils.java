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

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * Provides basic U utility methods
 * @author Dirk Reske
 *
 */
public final class UIUtils {

	private UIUtils() {
		
	}
	
	/**
	 * Shows an error message
	 * @param parent The parent component of the error message
	 * @param e The exception
	 */
	public static void showErrorMessage(Component parent, Exception e) {
		showErrorMessage(parent, "", e);
	}
	
	/**
	 * Shows an error message
	 * @param parent The parent component
	 * @param message The message
	 * @param e The corresponding exception
	 */
	public static void showErrorMessage(Component parent, String message, Exception e) {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isNullOrEmpty(message)) {
			sb.append(message);
		}
		
		if (e != null) {
			sb.append("\n\n");
			sb.append(e.getMessage());
		}
		
		JOptionPane.showMessageDialog(parent, sb.toString(), "Error", JOptionPane.ERROR_MESSAGE);
	}
}
