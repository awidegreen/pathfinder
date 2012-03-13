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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.PathStatistics;
import net.sf.pathfinder.util.ResourceManager;
import net.sf.pathfinder.util.properties.PropertyInfo;

import org.jdesktop.swingx.JXTaskPane;


/**
 * A path statistics pane contains all informations about a calculated path
 * @author Dirk Reske
 *
 */
public class PathStatisticsTaskPane extends JXTaskPane {

	private static ButtonGroup radioButtonGroup = new ButtonGroup();
	private JRadioButton selectedRadioButton;
	private JCheckBox visibleCheckBox;
	private Action removeButton;

	private PathHistoryModel model;
	private PathStatistics pathStatistics;
	private String title;

	private PathHistoryModelListenerImpl historyModelListenerImpl = new PathHistoryModelListenerImpl();
	private ResourceManager resourceManager = new ResourceManager(getClass());
	
	/**
	 * Creates a new PathStatisticsTaskPane
	 * @param model The model that holds the informations about the path statistics
	 * @param statistics The {@link PathStatistics} this pane is for
	 */
	public PathStatisticsTaskPane(PathHistoryModel model, PathStatistics statistics) {
		this.model = model;
		this.pathStatistics = statistics;
		this.model.addPathHistoryModelListener(historyModelListenerImpl);
		
		this.title = getTaskPaneTitle(statistics);
		setTitle(title);

		add(new JLabel(String.format("Algorithm: %s", statistics.getAlgorithm())));
		add(new JLabel(String.format("Path found: %s", statistics.isPathFound() ? "yes" : "no")));
		add(new JLabel(String.format("Time: %s ms", statistics.getDuration())));
		if (statistics.isPathFound()) {
			add(new JLabel(String.format("Steps: %d", statistics.getLastPath().getPathSteps().size())));
			add(new JLabel(String.format("Backtracking-Nodes: %d", statistics.getLastPath().getBackTrackingNodes().size())));
			add(new JLabel(String.format("Length: %d", statistics.getLastPath().getLength())));
		}
		add(new JLabel(String.format("Linear distance: %.2f", 
				statistics.getStart().getCoordinate().distance(statistics.getDestination().getCoordinate()))));
		
		populateProperties(statistics);
		
		add(getSelectRadioButton());
		add(getSetVisibilityCheckBox());
		add(getSetBackTrackingCheckBox(statistics));
		add(getSetPathColorSelector(statistics));
		add(getSetBackTrackingColorSelector(statistics));
		add(getRemoveButton());
	}

	/**
	 * Populates the properties for the specified {@link PathStatistics}
	 * @param statistics The path statistics to take the properties from
	 */
	private void populateProperties(PathStatistics statistics) {
		if (statistics.getProperties().size() > 0) {
			JXTaskPane propertiesPane = new JXTaskPane();
			propertiesPane.setTitle("Properties");
			propertiesPane.setCollapsed(true);
			StringBuilder sb = new StringBuilder();
			sb.append("<html><body><strong>Properties</strong><hr /><ul>");
			for (PropertyInfo propInfo : statistics.getProperties().keySet()) {
				sb.append(String.format("<li>%s: %s</li>", propInfo.getName(), statistics.getProperties().get(propInfo)));
				propertiesPane.add(new JLabel(String.format("%s: %s", propInfo.getName(), statistics.getProperties().get(propInfo))));
			}
			sb.append("</ul></body></html>");
			setToolTipText(sb.toString());
			add(propertiesPane);
		}
	}

	private Action getRemoveButton() {
		if (removeButton == null) {
			removeButton = new AbstractAction(resourceManager.getString("removeButton.text"), 
					resourceManager.getIcon("removeButton.icon")) {
				@Override
				public void actionPerformed(ActionEvent e) {
					model.remove(pathStatistics);
				}
			};
		}
		return removeButton;
	}

	private JRadioButton getSelectRadioButton() {
		if (selectedRadioButton == null) {
			selectedRadioButton = new JRadioButton("Selected");
			selectedRadioButton.setSelected(pathStatistics.equals(model.getSelectedPathStatistics()));
			selectedRadioButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (selectedRadioButton.isSelected()) {
						if (!pathStatistics.equals(model.getSelectedPathStatistics())) {
							model.setSelectedPathStatistics(pathStatistics);
						}
					}
				}
			});
			radioButtonGroup.add(selectedRadioButton);
		}
		return selectedRadioButton;
	}

	private JColorSelector getSetBackTrackingColorSelector(final PathStatistics statistics) {
		final JColorSelector selector = new JColorSelector("Back Tracking Color");
		selector.setColor(statistics.getBackTrackingColor());
		selector.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statistics.setBackTrackingColor(selector.getColor());
			}
		});
		return selector;
	}

	private JColorSelector getSetPathColorSelector(final PathStatistics statistics) {
		final JColorSelector selector = new JColorSelector("Path Color");
		selector.setColor(statistics.getPathColor());
		selector.setActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statistics.setPathColor(selector.getColor());
			}
		});
		return selector;
	}

	private JCheckBox getSetVisibilityCheckBox() {
		if (visibleCheckBox == null) {
			visibleCheckBox = new JCheckBox("Visible");
			visibleCheckBox.setSelected(pathStatistics.isVisible());
			visibleCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (pathStatistics.isVisible() != visibleCheckBox.isSelected()) {
						pathStatistics.setVisible(visibleCheckBox.isSelected());
					}
				}
			});
		}

		return visibleCheckBox;
	}

	private JCheckBox getSetBackTrackingCheckBox(final PathStatistics statistics) {
		final JCheckBox checkBox = new JCheckBox("Back Tracking");
		checkBox.setSelected(statistics.isPaintBacktrackingNodes());
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				statistics.setPaintBacktrackingNodes(checkBox.isSelected());
			}
		});
		return checkBox;
	}

	private String getTaskPaneTitle(PathStatistics statistics) {
		Node start = statistics.getStart();
		Node destination = statistics.getDestination();
		int index = 1;
		for (int i = 0; i < model.size(); i++) {
			PathStatistics stat = model.get(i);
			if (pathStatistics.equals(stat)) {
				continue;
			}
			if (stat.getStart().equals(start) && stat.getDestination().equals(destination)) {
				index++;
			}
		}

		String result = String.format("%s => %s", 
				statistics.getStart().getName(),
				statistics.getDestination().getName());
		if (index > 1) {
			result += String.format(" (%d)", index);
		}
		return result;
	}

	private class PathHistoryModelListenerImpl implements PathHistoryModelListener {
		
		@Override
		public void pathStatisticsAdded(PathHistoryModel source,
				PathStatistics statistics) {
		}

		@Override
		public void pathStatisticsChanged(PathHistoryModel source,
				PathStatistics statistics) {
			if (statistics.equals(pathStatistics)) {
				if (pathStatistics.equals(model.getSelectedPathStatistics())) {
					getSelectRadioButton().setSelected(true);
				}
				getSetVisibilityCheckBox().setSelected(statistics.isVisible());
			}
		}

		@Override
		public void pathStatisticsRemoved(PathHistoryModel source, PathStatistics statistics) {
			if (pathStatistics.equals(statistics)) {
				model.removePathHistoryModelListener(this);
			}
		}
	}
}
