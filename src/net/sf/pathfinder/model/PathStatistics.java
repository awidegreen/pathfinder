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

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.pathfinder.algo.Algorithm;
import net.sf.pathfinder.util.properties.PropertyInfo;


/**
 * Contains statistics for a calculated path
 * @author Dirk Reske
 *
 */
@XmlRootElement
public class PathStatistics extends Observable implements Serializable {

	@XmlElement
	private Node start;
	@XmlElement
	private Node destination;
	@XmlElement
	private boolean pathFound = true;
	@XmlElements(@XmlElement(type=Path.class))
	private List<Path> paths = new ArrayList<Path>();
	private Map<PropertyInfo, Object> propertyMap = new HashMap<PropertyInfo, Object>();

	private int selectedPathIndex = -1;
	private String algorithm;
	private Color pathColor;
	private Color backTrackingColor;
	private boolean visible = true;
	private boolean paintBacktrackingNodes = false;
	private long duration = 0;

	/**
	 * Creates new PathStatistics
	 * @param path Path to create the statistics for
	 * @param algorithm Algorithm the path was calculated with
	 */
	public PathStatistics(Node start, Node destination, Algorithm algorithm) {
		storePropertyValues(algorithm);
		this.start = start;
		this.destination = destination;
		this.algorithm = algorithm.getName();
	}

	/**
	 * Stores the property values for the specified algorithm
	 * @param algorithm Algorithm to store the properties for
	 */
	private void storePropertyValues(Algorithm algorithm) {
		for (PropertyInfo propertyInfo : algorithm.getPropertyInfos()) {
			propertyMap.put(propertyInfo, algorithm.getProperty(propertyInfo.getName()));
		}
	}

	public Map<PropertyInfo, Object> getProperties() {
		return propertyMap;
	}
	
	public Node getStart() {
		return start;
	}

	public Node getDestination() {
		return destination;
	}

	public List<Path> getPaths() {
		return paths;
	}

	public Path getSelectedPath() {
		if (getSelectedPathIndex() < 0) {
			return null;
		}
		return paths.get(selectedPathIndex);
	}

	public Path getLastPath() {
		if (paths.size() > 0) {
			return paths.get(paths.size() - 1);
		}
		return null;
	}
	
	public boolean isPathFound() {
		return pathFound;
	}

	public void setPathFound(boolean pathFound) {
		this.pathFound = pathFound;
	}	

	/**
	 * Gets the path's color
	 * @return The path's color
	 */
	public Color getPathColor() {
		if (pathColor == null) {
			pathColor = getBackTrackingColor().darker();
		}
		return pathColor;
	}

	/**
	 * Sets the path's color
	 * @param pathColor The color
	 */
	public void setPathColor(Color pathColor) {
		this.pathColor = pathColor;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the color of the backtracking nodes
	 * @return The color
	 */
	public Color getBackTrackingColor() {
		if (backTrackingColor == null) {
			backTrackingColor = generateRandomColor();
		}
		return backTrackingColor;
	}

	/**
	 * Sets the color of the backtracking nodes
	 * @param backTrackingColor The color
	 */
	public void setBackTrackingColor(Color backTrackingColor) {
		this.backTrackingColor = backTrackingColor;
		setChanged();
		notifyObservers();
	}

	/**
	 * Should the backtracking nodes get painted
	 * @return True if the nodes should be painted, otherwise false
	 */
	public boolean isPaintBacktrackingNodes() {
		return paintBacktrackingNodes;
	}

	/**
	 * Should the backtracking nodes get painted
	 * @param paintBacktrackingNodes True if they should be painted, otherwise false
	 */
	public void setPaintBacktrackingNodes(boolean paintBacktrackingNodes) {
		this.paintBacktrackingNodes = paintBacktrackingNodes;
		setChanged();
		notifyObservers();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		setChanged();
		notifyObservers();
	}

	private Color generateRandomColor() {
		Random random = new Random(System.currentTimeMillis());
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		return new Color(r,g,b);
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public int getSelectedPathIndex() {
		if (selectedPathIndex < 0) {
			selectedPathIndex = paths.size() - 1;
		}
		return selectedPathIndex;
	}

	public void setSelectedPathIndex(int selectedPathIndex) {
		int old = this.selectedPathIndex;
		if (selectedPathIndex >= paths.size()) {
			selectedPathIndex = paths.size() - 1;
		}
		this.selectedPathIndex = selectedPathIndex;

		if (old != selectedPathIndex) {
			setChanged();
			notifyObservers();
		}
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
}
