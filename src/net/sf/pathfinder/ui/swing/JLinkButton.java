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

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;

public class JLinkButton extends JLabel {

	private Action action;
	
	/**
	 * Creates a new JLinkButton
	 * @param action The action to perform when the button is clicked
	 */
	public JLinkButton(Action action) {
		this.action = action;
		setPropertiesFromAction(action);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}
	
	private void setPropertiesFromAction(Action action) {
		if (action.getValue(Action.SMALL_ICON) != null) {
			setIcon((Icon)action.getValue(Action.SMALL_ICON));
		}
		setText("<html><u>" + (String) action.getValue(Action.NAME) + "</u></html>");
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		
		if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseEvent.BUTTON1) {
			this.action.actionPerformed(new ActionEvent(this, 0, "Click"));
		}
	}
}
