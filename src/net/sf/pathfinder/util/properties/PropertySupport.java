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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Basic property support class
 * @author Dirk Reske
 *
 */
public abstract class PropertySupport {

	private List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();
	private Map<String, Object> properties = new HashMap<String, Object>();
	private List<PropertyChangeListener> propertyChangeListeners = new Vector<PropertyChangeListener>();

	public PropertySupport() {

	}

	protected void addProperty(PropertyInfo propertyInfo, Object defaultValue) {
		propertyInfos.add(propertyInfo);
		properties.put(propertyInfo.getName(), defaultValue);
	}

	/**
	 * Gets a list of available properties
	 * @return A list of property infos
	 */
	public List<PropertyInfo> getPropertyInfos() {
		return propertyInfos;
	}

	/**
	 * Gets a property value
	 * @param name Name of the property
	 * @return The property value
	 */
	public Object getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * Sets a property value
	 * @param name Name of the property
	 * @param value Value of the property
	 */
	public void setProperty(String name, Object value) {
		properties.put(name, value);
	}

	/**
	 * Adds a property change listener
	 * @param listener The property change listener
	 * @see PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (!propertyChangeListeners.contains(listener)) {
			propertyChangeListeners.add(listener);
		}
	}

	/**
	 * Removes a property change listener
	 * @param listener The listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (propertyChangeListeners.contains(listener)) {
			propertyChangeListeners.remove(listener);
		}
	}

	/**
	 * Fires the propertyChanged on all registered property change listeners
	 * @param propertyName Name of the changed property
	 * @param propertyValue New value of the property
	 */
	protected void firePropertyChanged(String propertyName, Object propertyValue) {
		PropertyChangeListener[] listeners = 
			propertyChangeListeners.toArray(new PropertyChangeListener[propertyChangeListeners.size()]);

		for (PropertyChangeListener listener : listeners) {
			listener.propertyChanged(this, propertyName, propertyValue);
		}
	}
}
