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
 * FileFilter for open or save file dialogs that accepts image file formats
 * 
 * @author Armin Widegreen
 * 
 */
public class ImageFileFilter extends FileFilter {

	private static final String JPEG = "jpeg";
	private static final String JPG = "jpg";
	private static final String GIF = "gif";
	private static final String TIFF = "tiff";
	private static final String TIF = "tif";
	private static final String PNG = "png";

	/**
	 * Accepts directories and various image file formats
	 * 
	 * @param f
	 *            The file to test
	 * @return True if f is a directory or a accepted file type
	 */
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = FileUtils.getExtension(f);
		if (extension == null) {
			return false;
		}
		if (extension.equals(TIFF) || extension.equals(TIF)
				|| extension.equals(GIF) || extension.equals(JPEG)
				|| extension.equals(JPG) || extension.equals(PNG)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the description of the filer
	 * 
	 * @return The deescription of the accepted files
	 */
	@Override
	public String getDescription() {
		return "Image Files";
	}
}
