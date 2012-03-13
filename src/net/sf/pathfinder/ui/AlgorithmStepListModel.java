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

import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;

import net.sf.pathfinder.model.Path;


@SuppressWarnings("serial")
public class AlgorithmStepListModel extends AbstractListModel {

	private class ListElement {
		public String message;
		public Path path;
	}
	
	private List<ListElement> list = new Vector<ListElement>();
	
	public void add(String message, Path path) {
		ListElement element = new ListElement();
		element.message = message;
		element.path = path;
		list.add(element);
		fireIntervalAdded(this, list.size() - 1, list.size() - 1);
	}
	
	public void clear() {
		int oldSize = list.size();
		list.clear();
		super.fireIntervalRemoved(this, 0, oldSize);
	}
	
	@Override
	public Object getElementAt(int index) {
		return list.get(index).message;
	}

	public Path getPathAt(int index) {
		return list.get(index).path;
	}
	
	@Override
	public int getSize() {
		return list.size();
	}

}
