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

package net.sf.pathfinder;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sf.pathfinder.help.Help;
import net.sf.pathfinder.help.HelpDispatcher;
import net.sf.pathfinder.ui.PathFinderForm;
import net.sf.pathfinder.util.ResourceManager;
import net.sf.pathfinder.util.UIUtils;


/**
 * The main entry point for the application
 * @author Dirk Reske
 *
 */
public final class PathFinder {

	private static ResourceManager resourceManager = new ResourceManager(PathFinder.class);
	private static Help help = null;
	private static HelpDispatcher helpDispatcher;
	
	private static JFrame mainForm;
	
	private PathFinder() {
		
	}
	
	/**
	 * The main entry point for the application
	 * @param args The command line arguments
	 */
	public static void main(String[] args) {
		
		//Ensure the graphs directory exists
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainForm = new PathFinderForm();
				mainForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainForm.setVisible(true);
			}
		});
	}
	
	/**
	 * Gets the main form of the application
	 * @return The main form
	 */
	public static JFrame getMainForm() {
		return mainForm;
	}
	
	/**
	 * Gets the {@link HelpDispatcher} instance for the application
	 * @return The help dispatcher
	 */
	public static HelpDispatcher getHelpDispatcher() {
		if (helpDispatcher == null) {
			helpDispatcher = new HelpDispatcher(getHelp());
		}
		return helpDispatcher;
	}
	
	
	private static Help getHelp() {
		if (help == null) {
			try {
				help = Help.loadFromFile(resourceManager.getResource("help.file"));
			} catch (Exception e) {
				UIUtils.showErrorMessage(mainForm, "Can't load help file", e);
			} 
		}
		return help;
	}
}
