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

package net.sf.pathfinder.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.sf.pathfinder.util.FileUtils;


/**
 * A JFileChooser filter that accepts only graph package files (*.gpk)
 * @author Dirk Reske
 *
 */
public class GPKFileFilter extends FileFilter {

	public static final String XML = "xml";
	
	/**
	 * Accepts directories and gpk files
	 */
	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		if (f.getAbsolutePath().endsWith(".graph.xml")) {
			return false;
		}
		
		if (XML.equals(FileUtils.getExtension(f)))
			return true;
		else
			return false;
	}

	/**
	 * Gets the description of the filter
	 */
	@Override
	public String getDescription() {
		return "Graph Package Files";
	}
}
