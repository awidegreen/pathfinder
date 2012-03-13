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

package net.sf.pathfinder.ui.swing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.pathfinder.model.PathStatistics;


@XmlRootElement
public class PathHistoryModel implements Observer, Serializable {
	
	private transient List<PathHistoryModelListener> listeners = new Vector<PathHistoryModelListener>();
	
	@XmlElements(@XmlElement(type = PathStatistics.class))
	private List<PathStatistics> pathStatistics = new ArrayList<PathStatistics>();
	private transient PathStatistics selectedPathStatistics = null;
	
	/**
	 * Creates a new path history model
	 */
	public PathHistoryModel() {
		
	}
	
	public int size() {
		return pathStatistics.size();
	}
	
	public PathStatistics get(int index) {
		return pathStatistics.get(index);
	}
	
	public void removeAll() {
		PathStatistics[] statistics = pathStatistics.toArray(new PathStatistics[pathStatistics.size()]);
		for (PathStatistics s : statistics) {
			remove(s);
		}
	}
	
	public void remove(PathStatistics statistics) {
		if (pathStatistics.contains(statistics)) {

			pathStatistics.remove(statistics);
			firePathStatisticsRemoved(statistics);
			
			if (statistics.equals(selectedPathStatistics)) {
				if (size() > 0) {
					setSelectedPathStatistics(get(size() - 1));
				}
				else {
					setSelectedPathStatistics(null);
				}
			}
		}
	}
	
	public void clear() {
		for (int i = size() - 1; i >= 0; i--) {
			remove(get(i));
		}
	}
	
	public void add(PathStatistics value) {
		if (!pathStatistics.contains(value)) {
			value.addObserver(this);
			pathStatistics.add(value);
			firePathStatisticsAdded(value);
		}
	}

	/**
	 * Processes the the update events from the containing paths
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof PathStatistics) {
			firePathStatisticsChanged((PathStatistics)o);
		}
	}
	
	public PathStatistics getSelectedPathStatistics() {
		return selectedPathStatistics;
	}
	
	public void setSelectedPathStatistics(PathStatistics statistics) {
		if (pathStatistics.contains(statistics) || (statistics == null)) {
			selectedPathStatistics = statistics;
			firePathStatisticsChanged(statistics);
		}
	}
	
	protected void firePathStatisticsAdded(PathStatistics statistics) {
		PathHistoryModelListener[] list = listeners.toArray(new PathHistoryModelListener[listeners.size()]);
		for (PathHistoryModelListener l : list) {
			l.pathStatisticsAdded(this, statistics);
		}
	}
	
	protected void firePathStatisticsRemoved(PathStatistics statistics) {
		PathHistoryModelListener[] list = listeners.toArray(new PathHistoryModelListener[listeners.size()]);
		for (PathHistoryModelListener l : list) {
			l.pathStatisticsRemoved(this, statistics);
		}
	}
	
	protected void firePathStatisticsChanged(PathStatistics statistics) {
		PathHistoryModelListener[] list = listeners.toArray(new PathHistoryModelListener[listeners.size()]);
		for (PathHistoryModelListener l : list) {
			l.pathStatisticsChanged(this, statistics);
		}
	}
	public void addPathHistoryModelListener(PathHistoryModelListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removePathHistoryModelListener(PathHistoryModelListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
}
