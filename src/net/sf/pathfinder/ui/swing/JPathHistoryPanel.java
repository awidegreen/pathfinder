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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.pathfinder.model.PathStatistics;
import net.sf.pathfinder.util.ResourceManager;

import org.jdesktop.swingx.JXTaskPaneContainer;


public class JPathHistoryPanel extends JPanel implements PathHistoryModelListener {
	private PathHistoryModel model;
	
	private Map<PathStatistics, PathStatisticsTaskPane> statisticPanes = new HashMap<PathStatistics, PathStatisticsTaskPane>();
	private List<PathStatisticsTaskPane> statisticPaneList = new ArrayList<PathStatisticsTaskPane>();

	private JXTaskPaneContainer container = new JXTaskPaneContainer();
	private JPanel commonTasksPane;
	
	private ResourceManager resourceManager = new ResourceManager(getClass());
	
	public JPathHistoryPanel(PathHistoryModel model) {
		this.model = model;
		this.model.addPathHistoryModelListener(this);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(container), BorderLayout.CENTER);
		add(getCommonTasksPane(), BorderLayout.SOUTH);
	}
	
	private JPanel getCommonTasksPane() {
		if (commonTasksPane == null) {
			commonTasksPane = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			
			c.insets = new Insets(3,0,3,0);
			c.insets.left = 10;
			c.gridy = 0;
			c.gridx = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			
			commonTasksPane.add(new JLinkButton(new AbstractAction("All") {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < getModel().size(); i++) {
						getModel().get(i).setVisible(true);
					}
				}
			}), c);
			
			c.insets.left = 0;
			c.gridx = 1;
			commonTasksPane.add(new JLabel(" / "), c);
			
			
			c.gridx = 2;
			commonTasksPane.add(new JLinkButton(new AbstractAction("None") {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < getModel().size(); i++) {
						getModel().get(i).setVisible(false);
					}
				}
			}), c);
			
			c.weightx = 1;
			c.gridx = 3;
			commonTasksPane.add(new JLabel(" Visible"), c);
			
			c.insets.left = 10;
			c.gridy = 1;
			c.gridx = 0;
			c.gridwidth = 4;
			commonTasksPane.add(new JLinkButton( new AbstractAction(resourceManager.getString("removeAll.text"),
					resourceManager.getIcon("removeAll.icon")) {
				@Override
				public void actionPerformed(ActionEvent e) {
					getModel().removeAll();
				}
			}), c);
		}
		return commonTasksPane;
	}
	
	public PathStatisticsTaskPane getPane(PathStatistics pathStatistics) {
		return statisticPanes.get(pathStatistics);
	}
	
	/**
	 * Gets the model
	 * @return The model
	 */
	public PathHistoryModel getModel() {
		return model;
	}

	/**
	 * Sets the model
	 * @param model The model
	 */
	public void setModel(PathHistoryModel model) {
		if (this.model != null) {
			this.model.removePathHistoryModelListener(this);
		}

		this.model = model;
		if (this.model != null) {
			this.model.addPathHistoryModelListener(this);
		}
	}

	/**
	 * Rebuilds the history panel
	 */
	private void rebuildHistoryPanel() {
		container.removeAll();
		
		for (PathStatisticsTaskPane pane : statisticPaneList) {
			container.add(pane);			
		}
		
		container.revalidate();
		container.repaint();
	}
	
	@Override
	public void pathStatisticsAdded(PathHistoryModel source, PathStatistics statistics) {
		if (!statisticPanes.containsKey(source)) {
			PathStatisticsTaskPane pane = new PathStatisticsTaskPane(model, statistics);
			statisticPanes.put(statistics, pane);
			statisticPaneList.add(0, pane);
			rebuildHistoryPanel();
		}
	}

	@Override
	public void pathStatisticsChanged(PathHistoryModel source, PathStatistics statistics) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pathStatisticsRemoved(PathHistoryModel source, PathStatistics statistics) {
		if (statisticPanes.containsKey(statistics)) {
			statisticPaneList.remove(statisticPanes.get(statistics));
			statisticPanes.remove(statistics);
			rebuildHistoryPanel();
		}
	}
}
