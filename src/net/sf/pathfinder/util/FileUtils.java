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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JFileChooser;

import net.sf.pathfinder.ui.GPKFileFilter;


/**
 * Provides file utility methods
 * 
 * @author Dirk Reske
 * 
 */
public final class FileUtils {

	private FileUtils() {

	}

	/**
	 * Gets a file chooser that points to the current directory
	 * 
	 * @return The file chooser
	 */
	public static JFileChooser getFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new GPKFileFilter());
		fileChooser.setDialogTitle("Select Graph Package File");
		fileChooser.setCurrentDirectory(new File(FileUtils
				.getCurrentDirectory()
				+ "/graph"));
		return fileChooser;
	}

	/**
	 * Removes the extension of a file
	 * 
	 * @param file
	 *            File to remove the extension from
	 * @return The complete file path without the extension
	 */
	public static String removeExtension(File file) {
		if (file.getName().lastIndexOf(".") > -1) {
			String absolutePath = file.getAbsolutePath();
			return absolutePath.substring(0, absolutePath.lastIndexOf("."));
		} else {
			return file.getAbsolutePath();
		}
	}

	/**
	 * Gets the extension of a file
	 * 
	 * @param file
	 *            The filename
	 * @return The extension
	 */
	public static String getExtension(File file) {
		String ext = null;
		String s = file.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Gets the current directory
	 * 
	 * @return The current directory
	 */
	public static String getCurrentDirectory() {
		File file = new File(".");
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	/**
	 * Copies a file to a new destination
	 * 
	 * @param source
	 *            The source file
	 * @param destination
	 *            The destination file
	 * @throws IOException
	 *             If any error occurs
	 */
	public static void copyFile(File source, File destination)
			throws IOException {
		InputStream inStream = new FileInputStream(source);
		OutputStream outStream = new FileOutputStream(destination);

		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = inStream.read(buffer)) > -1) {
			outStream.write(buffer, 0, count);
		}

		inStream.close();
		outStream.close();
	}
}
