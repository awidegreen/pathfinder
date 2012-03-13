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

package net.sf.pathfinder.help;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import net.sf.pathfinder.util.StringUtils;


/**
 * The help dispatcher manages the help mappings between components and their help topics.
 * Its also responsible for dispatching help messages the the help display
 * @author Dirk Reske
 *
 */
public class HelpDispatcher {

	private List<HelpDispatcherListener> listeners = new Vector<HelpDispatcherListener>();
	private Help help;
	
	/**
	 * Creates a new HelpDispatcher
	 * @param help The help to be used
	 */
	public HelpDispatcher(Help help) {
		this.help = help;
	}
	
	/**
	 * Adds a help mapping for the specified component
	 * @param component The component
	 * @param helpRefId The help id to display if the component gets the focus
	 */
	public void addHelpMapping(Component component, final String helpRefId) {
		component.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				showHelpMessage(helpRefId);
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
		});
	}
	
	/**
	 * Adds a mouse over help mapping for the specified component
	 * @param component The component
	 * @param helpRefId The help id to display if the mouse gets over the component
	 */
	public void addMouseOverHelpMapping(Component component, final String helpRefId) {
		component.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				showHelpMessage(helpRefId);
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
		});
	}
	
	/**
	 * Shows the help message associated with the help attached instance
	 * @param helpAttached The help attached instance
	 */
	public void showHelpMessage(HelpAttached helpAttached) {
		showHelpMessage(helpAttached.getHelpRefId());
	}
	
	/**
	 * Shows the help message associated with the specified help id
	 * @param helpRefId The id of the help topic to show
	 */
	public void showHelpMessage(String helpRefId) {
		if (help == null) {
			return;
		}
		
		if (StringUtils.isNullOrEmpty(helpRefId)) {
			fireHelpMessageSelected("");
			return;
		}
		
		HelpTopic topic = help.findTopic(helpRefId);
		if (topic != null) {
			fireHelpMessageSelected(topic.getContent());
		}
	}	
	
	private void fireHelpMessageSelected(String text) {
		HelpDispatcherListener[] list = listeners.toArray(new HelpDispatcherListener[listeners.size()]);
		for (HelpDispatcherListener l : list) {
			l.helpMessageSelected(text);
		}
	}
	
	/**
	 * Adds a {@link HelpDispatcherListener} instance to this help dispatcher
	 * @param listener The new listener
	 */
	public void addHelpDispatcherListener(HelpDispatcherListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
}
