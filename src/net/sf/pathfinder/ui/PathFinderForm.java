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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.pathfinder.PathFinder;
import net.sf.pathfinder.algo.Algorithm;
import net.sf.pathfinder.algo.AlgorithmDescriptor;
import net.sf.pathfinder.algo.CalculationListener;
import net.sf.pathfinder.algo.PathCalculation;
import net.sf.pathfinder.help.HelpDispatcherListener;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.GraphPackage;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.PathStatistics;
import net.sf.pathfinder.ui.swing.GraphSelectionListener;
import net.sf.pathfinder.ui.swing.JGraphView;
import net.sf.pathfinder.ui.swing.JPathHistoryPanel;
import net.sf.pathfinder.ui.swing.JPropertyTable;
import net.sf.pathfinder.ui.swing.PathHistoryModel;
import net.sf.pathfinder.ui.swing.PathHistoryModelListener;
import net.sf.pathfinder.util.FileUtils;
import net.sf.pathfinder.util.ResourceManager;
import net.sf.pathfinder.util.StringUtils;
import net.sf.pathfinder.util.UIUtils;


import ca.ansir.swing.list.TextFieldList;
import ca.ansir.swing.list.TextFieldListModel;



/**
 * The main pathfinder form
 * @author Dirk Reske
 *
 */
public class PathFinderForm extends JFrame {

	private JGraphView graphView;
	private TextFieldList<String> sourceTextFieldList;
	private TextFieldList<String> destinationTextFieldList;
	
	private JButton findRouteButton;
	private JSlider zoomSlider;
	private JComboBox algorithmComboBox;

	private PathCalculation currentCalculation;
	private Algorithm currentAlgorithm;
	private JPropertyTable propertyTable;

	private PathHistoryModel historyModel = new PathHistoryModel();
	private JPathHistoryPanel historyPanel;
	private JSlider replaySlider;
	private JPanel replayPanel;
	private JButton playButton;
	private JButton stopButton;
	private JButton pauseButton;
	private JButton forwardButton;
	private JButton rewindButton;
	
	private Timer replayTimer;
	private JSpinner replaySpeedSpinner;
	private JLabel zoomLabel;
	private JLabel currentlySelected;
	private boolean adjustingSlider = false;

	private CalculationListener calculationListener;
	private PathHistoryModelListener pathHistoryModelListener;

	private JPanel graphStatisticsPanel;
	private JLabel nodesLabel;
	private JLabel endNodesLabel;
	private JLabel edgesLabel;
	private JLabel graphSizeLabel;
	
	private JTextPane helpTextPane = new JTextPane();
	
	private ResourceManager resMan = new ResourceManager(getClass());
	
	private TextFieldListModel<String> textFieldListModel;
	private Node currentSourceNode;
	private Node currentDestinationNode;
	
	/**
	 * Creates a new path finder form
	 */
	public PathFinderForm() {
		setTitle("PathFinder");
		this.setIconImage(resMan.getImage("form.icon"));

		setPreferredSize(new Dimension(1024, 768));
		
		createMainMenu();
		initComponents();
		pack();
		
		//Maximize form
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		pathHistoryModelListener = new PathHistoryModelListenerImpl();
		historyModel.addPathHistoryModelListener(pathHistoryModelListener);
		
		calculationListener = new CalculationListenerImpl();
		
		//Load the help file
		helpTextPane.setContentType("text/html");
		helpTextPane.setEditable(false);
		PathFinder.getHelpDispatcher().addHelpDispatcherListener(new HelpDispatcherListener() {
			@Override
			public void helpMessageSelected(String text) {
				helpTextPane.setText(text);
				helpTextPane.setCaretPosition(0);
			}
		});
	}
	
	
	private void initComponents() {
		setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		splitPane.setDividerLocation(800);
		splitPane.setResizeWeight(0.9);

		add(splitPane, BorderLayout.CENTER);
		add(getReplayPanel(), BorderLayout.SOUTH);


		JSplitPane graphSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		graphSplitPane.setDividerLocation(150);
		graphSplitPane.setResizeWeight(0.1);
		// add GraphView and StatisticPane
		JScrollPane graphScrollPane = new JScrollPane(getGraphView());
		graphScrollPane.setMinimumSize(new Dimension(0, 0));
		
		graphSplitPane.setLeftComponent(getPathHistoryPanel());
		
		JPanel graphPanel = new JPanel(new BorderLayout());
		graphPanel.add(graphScrollPane, BorderLayout.CENTER);
		graphPanel.add(getGraphStatisticsPanel(), BorderLayout.SOUTH);
		getGraphStatisticsPanel().setVisible(false);
		graphSplitPane.setRightComponent(graphPanel);

		splitPane.setLeftComponent(graphSplitPane);

		JSplitPane commandSplitPane = new JSplitPane();
		commandSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		commandSplitPane.setDividerLocation(600);
		
		// add Command Panel (left)
		JPanel commandPanel = new JPanel(new GridBagLayout());
		commandPanel.setMinimumSize(new Dimension(0, 0));
		splitPane.setRightComponent(commandSplitPane);

		commandSplitPane.setTopComponent(commandPanel);
		commandSplitPane.setBottomComponent(new JScrollPane(helpTextPane));
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.weightx = 1.0;

		c.insets = new Insets(2, 2, 2, 2); //default - just to have no null pointer
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		commandPanel.add(new JLabel("Source"), c);

		c.gridy++;
		commandPanel.add(getSourceTextField(), c);

		c.gridy++;
		c.insets.top = 10;
		commandPanel.add(new JLabel("Destination"), c);

		c.gridy++;		
		c.insets.top = 2;
		commandPanel.add(getDestinationTextField(), c);

		c.gridy++;
		c.insets.top = 10;
		commandPanel.add(new JLabel("Algorithm"), c);

		c.gridy++;		
		c.insets.top = 2;
		commandPanel.add(getAlgorithmComboBox(), c);

		c.gridy++;		
		c.insets.top = 10;
		commandPanel.add(getFindRouteButton(), c);

		c.gridy++;
		c.insets.top = 10;
		commandPanel.add(getZoomLabel(), c);

		c.gridy++;
		c.insets.top = 2;
		commandPanel.add(getZoomSlider(), c);

		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.insets.top = 10;
		commandPanel.add(new JScrollPane(getPropertyTable()), c);
	}

	private JPanel getGraphStatisticsPanel() {
		if (graphStatisticsPanel == null) {
			graphStatisticsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
			graphStatisticsPanel.setBorder(BorderFactory.createTitledBorder("Graph Statistics"));
			
			nodesLabel = new JLabel();
			JPanel nodesPanel = new JPanel(new BorderLayout());
			nodesPanel.add(new JLabel("Nodes: "), BorderLayout.WEST);
			nodesPanel.add(nodesLabel, BorderLayout.EAST);			
			graphStatisticsPanel.add(nodesPanel);
			
			endNodesLabel = new JLabel();
			JPanel endNodesPanel = new JPanel(new BorderLayout());
			endNodesPanel.add(new JLabel("End Nodes: "), BorderLayout.WEST);
			endNodesPanel.add(endNodesLabel, BorderLayout.EAST);
			graphStatisticsPanel.add(endNodesPanel);
			
			edgesLabel = new JLabel();
			JPanel edgesPanel = new JPanel(new BorderLayout());
			edgesPanel.add(new JLabel("Edges: "), BorderLayout.WEST);
			edgesPanel.add(edgesLabel, BorderLayout.EAST);
			graphStatisticsPanel.add(edgesPanel);
			
			graphSizeLabel = new JLabel();
			JPanel graphSizePanel = new JPanel(new BorderLayout());
			graphSizePanel.add(new JLabel("Graph Size: "), BorderLayout.WEST);
			graphSizePanel.add(graphSizeLabel, BorderLayout.EAST);
			graphStatisticsPanel.add(graphSizePanel);
		}
		return graphStatisticsPanel;
	}
	
	/**
	 * Gets the path history panel
	 * @return The history panel
	 */
	private JPathHistoryPanel getPathHistoryPanel() {
		if (historyPanel == null) {
			historyPanel = new JPathHistoryPanel(historyModel);
		}
		return historyPanel;
	}
	
	private JLabel getZoomLabel() {
		if (zoomLabel == null) {
			zoomLabel = new JLabel("Zoom: 100%");
		}
		return zoomLabel;
	}

	private Timer getReplayTimer() {
		if (replayTimer == null) {
			replayTimer = new Timer(50, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (getReplaySlider().isEnabled() && (getReplaySlider().getValue() < getReplaySlider().getMaximum())) {
						getReplaySlider().setValue(getReplaySlider().getValue() + 1);
						
						if (getReplaySlider().getValue() == getReplaySlider().getMaximum()) {
							replayTimer.stop();
						}
					}
				}
			});
		}
		return replayTimer;
	}
	
	private JSpinner getReplaySpeedSpinner() {
		if (replaySpeedSpinner == null) {
			replaySpeedSpinner = new JSpinner();
			replaySpeedSpinner.setPreferredSize(new Dimension(80, 20));
			replaySpeedSpinner.setValue(getReplayTimer().getDelay());
			replaySpeedSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					getReplayTimer().setDelay((Integer) replaySpeedSpinner.getValue());
				}
			});
			
		}
		return replaySpeedSpinner;
	}
	
	private JPanel getReplayPanel() {
		if (replayPanel == null) {
			replayPanel = new JPanel(new GridBagLayout());
			replayPanel.setBorder(BorderFactory.createTitledBorder("Replay"));
			
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5 , 5, 5, 5);
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.gridwidth = 7;
			c.gridx = 0;
			c.gridy = 0;	
			replayPanel.add(getCurrentlySelected(), c);
			
			c.gridy++;
			replayPanel.add(getReplaySlider(), c);
			
			c.fill = GridBagConstraints.NONE;
			c.gridwidth = 1;
			c.weightx = 0;
			c.gridy++;
			c.gridx = 0;
			replayPanel.add(new JLabel("Replay in ms:"), c);
			
			c.gridx++;
			replayPanel.add(getReplaySpeedSpinner(), c);
			
			c.gridx++;
			replayPanel.add(getRewindButton(), c);
			
			c.gridx++;
			replayPanel.add(getPlayButton(), c);
			
			c.gridx++;
			replayPanel.add(getPauseButton(), c);
			
			c.gridx++;
			replayPanel.add(getStopButton(), c);
			
			c.gridx++;
			c.weightx = 1;
			replayPanel.add(getForwardButton(), c);
		}
		
		return replayPanel;
	}
	
	
	private JLabel getCurrentlySelected() {
		if (currentlySelected == null) {
			currentlySelected = new JLabel("");
		}
		
		return currentlySelected;
	}
	
	private JButton getForwardButton() {
		if (forwardButton == null) {
			forwardButton = new JButton(resMan.getString("forwardButton.text"));
			forwardButton.setIcon(resMan.getIcon("forwardButton.icon"));
			forwardButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getReplaySlider().setValue(getReplaySlider().getValue() + 1);
				}
			});
		}
		return forwardButton;
	}
	
	private JButton getRewindButton() {
		if (rewindButton == null) {
			rewindButton = new JButton(resMan.getString("rewindButton.text"));
			rewindButton.setIcon(resMan.getIcon("rewindButton.icon"));
			rewindButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getReplaySlider().setValue(getReplaySlider().getValue() - 1);
				}
			});
		}
		return rewindButton;
	}
	
	private JButton getPlayButton() {
		if (playButton == null) {
			playButton = new JButton(resMan.getString("playButton.text"));
			playButton.setIcon(resMan.getIcon("playButton.icon"));
			playButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getReplayTimer().start();
				}
			});
		}
		return playButton;
	}
	
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton(resMan.getString("stopButton.text"));
			stopButton.setIcon(resMan.getIcon("stopButton.icon"));
			stopButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getReplayTimer().stop();
					if (getReplaySlider().isEnabled()) {
						getReplaySlider().setValue(0);
					}
				}
			});
		}
		return stopButton;
	}
	
	private JButton getPauseButton() {
		if (pauseButton == null) {
			pauseButton = new JButton(resMan.getString("pauseButton.text"));
			pauseButton.setIcon(resMan.getIcon("pauseButton.icon"));
			pauseButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getReplayTimer().stop();
				}
			});
		}
		return pauseButton;
	}
	
	private JSlider getReplaySlider() {
		if (replaySlider == null) {
			replaySlider = new JSlider();
			replaySlider.setMinimum(0);
			replaySlider.setValue(0);
			replaySlider.setEnabled(false);
			replaySlider.setPreferredSize(new Dimension(0, 30));

			replaySlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (!adjustingSlider) {
						if (historyModel.getSelectedPathStatistics() != null) {
							historyModel.getSelectedPathStatistics().setSelectedPathIndex(replaySlider.getValue());
						}
					}
				}
			});
		}
		return replaySlider;
	}

	/**
	 * Gets the property table
	 * @return The property table
	 */
	private JPropertyTable getPropertyTable() {
		if (propertyTable == null) {
			propertyTable = new JPropertyTable();
			propertyTable.setTarget(getCurrentAlgorithm());
		}
		return propertyTable;
	}

	/**
	 * Gets the combobox for selecting a algorithm
	 * @return The select algorithm combo box
	 */
	private JComboBox getAlgorithmComboBox() {
		if (algorithmComboBox == null) {
			algorithmComboBox = new JComboBox(Algorithm.getAlgorithms().toArray());
			algorithmComboBox.setEditable(false);

			algorithmComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					Algorithm algorithm = Algorithm.newInstance((AlgorithmDescriptor) e.getItem());
					setCurrentAlgorithm(algorithm);
					PathFinder.getHelpDispatcher().showHelpMessage(algorithm);
				}
			});
		}

		return algorithmComboBox;
	}

	/**
	 * Gets the zoom slider
	 * @return The zoom slider
	 */
	private JSlider getZoomSlider() {
		if (zoomSlider == null) {
			zoomSlider = new JSlider();
			zoomSlider = new JSlider(0, 200);
			zoomSlider.setValue((int) (getGraphView().getScaleFactor() * 100));
			zoomSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					getZoomLabel().setText("Zoom: " + zoomSlider.getValue() + "%");
					getGraphView().setScaleFactor(zoomSlider.getValue() / 100.0);
				}

			});
		}

		return zoomSlider;
	}

	/**
	 * Gets the find route button
	 * @return The find route button
	 */
	private JButton getFindRouteButton() {
		if (findRouteButton == null) {
			findRouteButton = new JButton(resMan.getString("findButton.text"),
					resMan.getIcon("findButton.icon"));
			
			findRouteButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Graph graph = getGraphView().getGraph();
					if (graph == null) {
						UIUtils.showErrorMessage(PathFinder.getMainForm(), 
								new Exception("Please load a graph first."));
						return;
					}
					final Node start = graph.getNodes().findNode(getSourceTextField().getText());
					final Node end = graph.getNodes().findNode(getDestinationTextField().getText());

					if (start == null || end == null) {
						UIUtils.showErrorMessage(PathFinder.getMainForm(), 
								new Exception("Your source or destination does not exists in the loaded graph."));
						return; 
					}

					currentCalculation = new PathCalculation(getCurrentAlgorithm(),
							graph, 
							start, 
							end,
							calculationListener);

					currentCalculation.start();

					getSourceTextField().setEnabled(false);
					getDestinationTextField().setEnabled(false);
					findRouteButton.setEnabled(false);
					algorithmComboBox.setEnabled(false);

				}
			});
		}
		return findRouteButton;
	}

	/**
	 * Gets the destination text field
	 * @return The destination text field
	 */
	private JTextField getDestinationTextField() {
		if (destinationTextFieldList == null) {
			destinationTextFieldList = new TextFieldList<String>(new String[0]);
			destinationTextFieldList.getAutoCompleteField().addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					
				}

				@Override
				public void keyReleased(KeyEvent e) {
					if (getGraphView().getGraph() != null) {
						Node node = graphView.getGraph().getNodes().findNode(
								destinationTextFieldList.getAutoCompleteField().getText());
						
						if (currentDestinationNode != null) {
							getGraphView().setNodeHighlighted(currentDestinationNode, false);
						}
						if (node != null) {
							getGraphView().setNodeHighlighted(node, true);
							currentDestinationNode = node;
						}
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}
				
			});
			
			//destinationTextFieldList.getAutoCompleteField().setText("Koblenz");
			PathFinder.getHelpDispatcher().addHelpMapping(destinationTextFieldList.getAutoCompleteField(), 
					"destinationTextField");
		}
		return destinationTextFieldList.getAutoCompleteField();
	}

	/**
	 * Gets the source text field
	 * @return The source text field
	 */
	private JTextField getSourceTextField() {
		if (sourceTextFieldList == null) {
			sourceTextFieldList = new TextFieldList<String>(new String[0]);
			sourceTextFieldList.getAutoCompleteField().addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
					if (getGraphView().getGraph() != null) {
						Node node = getGraphView().getGraph().getNodes().findNode(
								sourceTextFieldList.getAutoCompleteField().getText());
						if (currentSourceNode != null) {
							getGraphView().setNodeHighlighted(currentSourceNode, false);
						}
						if (node != null) {
							getGraphView().setNodeHighlighted(node, true);
							currentSourceNode = node;
						}
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {					
				}
				
			});
			
			//sourceTextFieldList.getAutoCompleteField().setText("Binz");
			PathFinder.getHelpDispatcher().addHelpMapping(sourceTextFieldList, "sourceTextField");
		}
		return sourceTextFieldList.getAutoCompleteField();
	}

	/**
	 * Gets the graph view
	 * @return The graph view
	 */
	private JGraphView getGraphView() {
		if (graphView == null) {
			graphView = new JGraphView(historyModel);
			graphView.setEditingMode(false);
			graphView.setPaintGraph(false);
			
			graphView.addGraphSelectionListener(new GraphSelectionListener() {
				@Override
				public void selectedNodeChanged(Object source, Node selectedNode) {
					System.out.println("Select");
					if (selectedNode != null) {
						if (StringUtils.isNullOrEmpty(getSourceTextField().getText())) {
							getSourceTextField().setText(selectedNode.getName());
						} else if (StringUtils.isNullOrEmpty(getDestinationTextField().getText())) {
							getDestinationTextField().setText(selectedNode.getName());
						} else {
							getSourceTextField().setText("");
							getDestinationTextField().setText("");
							getSourceTextField().setText(selectedNode.getName());
						}
					}
				}
			});
		}
		return graphView;
	}

	/**
	 * Creates the forms main menu
	 */
	private void createMainMenu() {
		JMenuBar menuBar = new JMenuBar();

		JMenu graphMenu = new JMenu("Graph");
		menuBar.add(graphMenu);

		JMenuItem loadGraphItem = new JMenuItem(resMan.getString("openGraph.text"), resMan.getIcon("openGraph.icon"));
		loadGraphItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = FileUtils.getFileChooser();
				if (fileChooser.showOpenDialog(PathFinder.getMainForm()) == JFileChooser.APPROVE_OPTION) {
					File packageFile = fileChooser.getSelectedFile();

					String basePath = packageFile.getParent();

					try {
						GraphPackage graphPackage = GraphPackage.load(packageFile.getAbsolutePath());

						if (graphPackage.getBackgroundImage() != null) {
							File imageFile = new File(basePath + "/" + graphPackage.getBackgroundImage());
							graphView.setBackgroundImage(ImageIO.read(imageFile));
						}
						Graph graph = Graph.load(basePath + "/" + graphPackage.getGraphFile());
						graphView.setGraph(graph);						
						historyModel.clear();
						
						//Display graph statistics
						getGraphStatisticsPanel().setVisible(graph != null);
						nodesLabel.setText(String.valueOf(graph.getNodes().size()));
						endNodesLabel.setText(String.valueOf(graph.getEndNodesCount()));
						edgesLabel.setText(String.valueOf(graph.getEdgeCount()));
						graphSizeLabel.setText(String.format("%dx%d", graph.getWidth(), graph.getHeight()));
						
						if (graph != null) {
							List<String> nodeNames = new ArrayList<String>();
							for (Node node : graph.getNodes()) {
								if (StringUtils.isNullOrEmpty(node.getName())) {
									continue;
								}
								nodeNames.add(node.getName());
							}
							
							textFieldListModel = new TextFieldListModel<String>(nodeNames);
							sourceTextFieldList.setModel(textFieldListModel);
							destinationTextFieldList.setModel(textFieldListModel);
						}
					} catch (Exception ex) {
						UIUtils.showErrorMessage(PathFinder.getMainForm(), ex);
					}
				}
			}
		});

		JMenuItem graphEditorItem = new JMenuItem(resMan.getString("graphEditor.text"), resMan.getIcon("graphEditor.icon"));
		graphEditorItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphBuilderForm graphBuilderForm = new GraphBuilderForm();
				graphBuilderForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				graphBuilderForm.setVisible(true);
			}
		});

		JMenuItem exitItem = new JMenuItem(resMan.getString("exit.text"), resMan.getIcon("exit.icon"));
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		graphMenu.add(loadGraphItem);
		graphMenu.add(graphEditorItem);
		graphMenu.addSeparator();
		graphMenu.add(exitItem);
		setJMenuBar(menuBar);
	}

	private Algorithm getCurrentAlgorithm() {
		if (currentAlgorithm == null) {
			setCurrentAlgorithm(Algorithm.newInstance((AlgorithmDescriptor) getAlgorithmComboBox().getSelectedItem()));
		}
		return currentAlgorithm;
	}

	private void setCurrentAlgorithm(Algorithm currentAlgorithm) {
		this.currentAlgorithm = currentAlgorithm;
		getPropertyTable().setTarget(currentAlgorithm);
	}

	private class CalculationListenerImpl implements CalculationListener {
	@Override
	public void calculationCompleted(PathCalculation source, final PathStatistics result) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				getSourceTextField().setEnabled(true);
				getDestinationTextField().setEnabled(true);
				findRouteButton.setEnabled(true);
				algorithmComboBox.setEnabled(true);

				historyModel.add(result);
			}
		});		
	}
	}
	
	private class PathHistoryModelListenerImpl implements PathHistoryModelListener {
		@Override
		public void pathStatisticsAdded(PathHistoryModel source, PathStatistics statistics) {
			historyModel.setSelectedPathStatistics(statistics);
			statistics.setVisible(true);
			for (int i = 0; i < historyModel.size(); i++) {
				if (statistics.equals(historyModel.get(i))) {
					continue;
				}
				historyModel.get(i).setVisible(false);
			}
		}

		@Override
		public void pathStatisticsChanged(PathHistoryModel source,
				PathStatistics statistics) {
			if (historyModel.getSelectedPathStatistics() != null) {
				adjustingSlider = true;			
				PathStatistics selected = historyModel.getSelectedPathStatistics();
				currentlySelected.setText("Currently Selected: " + historyPanel.getPane(selected).getTitle());
				getReplaySlider().setMaximum(selected.getPaths().size() - 1);
				getReplaySlider().setValue(selected.getSelectedPathIndex());
				getReplaySlider().setEnabled(true);
				adjustingSlider = false;
			} else {
				getReplaySlider().setEnabled(false);
				getReplayTimer().stop();
			}
		}

		@Override
		public void pathStatisticsRemoved(PathHistoryModel source, PathStatistics statistics) {
			currentlySelected.setText("");
		}
	}
	
}
