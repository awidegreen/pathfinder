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

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ResourceManager {

	private Class<? extends Object> clazz;
	private ResourceBundle bundle;
	
	public ResourceManager(Class<? extends Object> clazz) {
		this.bundle = ResourceBundle.getBundle(clazz.getName());
		this.clazz =clazz;
	}
	
	/**
	 * Gets a resource string
	 * @param key The key of the string
	 * @return The resource string
	 */
	public String getString(String key) {
		return bundle.getString(key);
	}
	
	/**
	 * Gets a icon from the resources
	 * @param key The key of the icon
	 * @return The icon
	 */
	public Icon getIcon(String key) {
		String imageName = getString(key);
		return new ImageIcon(clazz.getResource(imageName));
	}
	
	public Image getImage(String key) {
		return Toolkit.getDefaultToolkit().getImage(getResource(key));
	}
	
	/**
	 * Gets a resource url
	 * @param key Key of the resource
	 * @return The url
	 */
	public URL getResource(String key) {
		String fileName = getString(key);
		return clazz.getResource(fileName);
	}
	
	/**
	 * Gets a resource file as stream
	 * @param key The key of the file
	 * @return The input stream
	 */
	public InputStream getFile(String key) {
		String fileName = getString(key);
		return clazz.getResourceAsStream(fileName);
	}
}
