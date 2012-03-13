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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.GraphPackage;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.ui.swing.GraphSelectionListener;
import net.sf.pathfinder.ui.swing.JGraphView;
import net.sf.pathfinder.util.FileUtils;
import net.sf.pathfinder.util.UIUtils;


public class GraphBuilderForm extends JFrame {

	private JGraphView graphView;
	private JButton loadImageButton;
	private JButton loadGraphButton;
	private JButton saveGraphButton;
	private JCheckBox autoSelectCheckBox;
	private JCheckBox paintBackgroundCheckBox;
	private JCheckBox paintEdgesCheckBox;
	private JCheckBox paintNodesCheckBox;
	private JSlider scaleSlider;

	private JTextField nodeTitleTextField;
	private String backgroundFile = null;
	private Component current;
	
	/**
	 * Creates a new GraphBuilderForm
	 */
	public GraphBuilderForm() {
		setTitle("Graph Builder");
		setPreferredSize(new Dimension(800,600));
		initComponents();
		pack();
		
		current = this;
	}

	/**
	 * Initializes the gui components
	 */
	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JScrollPane scrollPane = new JScrollPane(getGraphView(), 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.gridheight = 9;
		c.gridx = 0;
		c.gridy = 0;
		add(scrollPane, c);	

		//Load Image button
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0;
		c.weighty = 0;
		c.gridx = 1;
		c.gridheight = 1;
		add(getLoadImageButton(), c);

		//Load graph button
		c.gridy = 1;
		add(getLoadGraphButton(), c);

		//Save graph button
		c.gridy = 2;
		add(getSaveGraphButton(), c);

		//Auto Select checkbox
		c.gridy = 3;
		add(getAutoSelectCheckBox(), c);

		//Paint Background checkbox
		c.gridy = 4;
		add(getPaintBackgroundCheckBox(), c);

		//Paint Background checkbox
		c.gridy = 5;
		add(getPaintNodesCheckBox(), c);


		//Paint Background checkbox
		c.gridy = 6;
		add(getPaintEdgesCheckBox(), c);

		c.gridy = 7;
		add(getScaleSlider(), c);

		//Node Title text field
		c.gridy = 8;
		add(getNodeTitleTextField(), c);
	}

	private JSlider getScaleSlider() {
		if (scaleSlider == null) {
			scaleSlider = new JSlider(0, 200);
			scaleSlider.setValue((int)(getGraphView().getScaleFactor() * 100));
			scaleSlider.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					getGraphView().setScaleFactor(scaleSlider.getValue() / 100.0);
				}

			});
		}
		return scaleSlider;
	}
	/**
	 * Gets the node title text field
	 * @return The text field
	 */
	private JTextField getNodeTitleTextField() {
		if (nodeTitleTextField == null) {
			nodeTitleTextField = new JTextField();
			nodeTitleTextField.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
					if (getGraphView().getSelectedNode() != null) {
						getGraphView().getSelectedNode().setName(nodeTitleTextField.getText());
					}
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}

			});
		}
		return nodeTitleTextField;
	}

	/**
	 * Gets the auto select checkbox
	 * @return The checkbox
	 */
	private JCheckBox getPaintEdgesCheckBox() {
		if (paintEdgesCheckBox == null) {
			paintEdgesCheckBox = new JCheckBox("Paint Edges");
			paintEdgesCheckBox.setSelected(getGraphView().isPaintEdges());
			paintEdgesCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					graphView.setPaintEdges(paintEdgesCheckBox.isSelected());
				}

			});
		}
		return paintEdgesCheckBox;
	}

	/**
	 * Gets the auto select checkbox
	 * @return The checkbox
	 */
	private JCheckBox getPaintBackgroundCheckBox() {
		if (paintBackgroundCheckBox == null) {
			paintBackgroundCheckBox = new JCheckBox("Paint Background");
			paintBackgroundCheckBox.setSelected(getGraphView().isPaintBackground());
			paintBackgroundCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					graphView.setPaintBackground(paintBackgroundCheckBox.isSelected());
				}

			});
		}
		return paintBackgroundCheckBox;
	}

	/**
	 * Gets the auto select checkbox
	 * @return The checkbox
	 */
	private JCheckBox getPaintNodesCheckBox() {
		if (paintNodesCheckBox == null) {
			paintNodesCheckBox = new JCheckBox("Paint Nodes");
			paintNodesCheckBox.setSelected(getGraphView().isPaintNodes());
			paintNodesCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					graphView.setPaintNodes(paintNodesCheckBox.isSelected());
				}

			});
		}
		return paintNodesCheckBox;
	}

	/**
	 * Gets the auto select checkbox
	 * @return The checkbox
	 */
	private JCheckBox getAutoSelectCheckBox() {
		if (autoSelectCheckBox == null) {
			autoSelectCheckBox = new JCheckBox("Auto Select");
			autoSelectCheckBox.setSelected(getGraphView().isAutoSelect());
			autoSelectCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					graphView.setAutoSelect(autoSelectCheckBox.isSelected());
				}

			});
		}
		return autoSelectCheckBox;
	}

	/**
	 * Gets the graph view
	 * @return The graph view
	 */
	private JGraphView getGraphView() {
		if (graphView == null) {
			graphView = new JGraphView(null);
			graphView.setEditingMode(true);
			graphView.addGraphSelectionListener(new GraphSelectionListener() {

				@Override
				public void selectedNodeChanged(Object source, Node selectedNode) {
					if (selectedNode != null) {
						nodeTitleTextField.setText(selectedNode.getName());
					}
					else {
						nodeTitleTextField.setText("");
					}
				}

			});
		}
		return graphView;
	}

	/**
	 * Gets the load image button
	 * @return The button
	 */
	private JButton getLoadImageButton() {
		if (loadImageButton == null) {
			loadImageButton = new JButton("Load Background");
			loadImageButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					loadBackgroundImage();
				}
			});
		}
		return loadImageButton;
	}

	/**
	 * Gets the load graph button
	 * @return The button
	 */
	private JButton getLoadGraphButton() {
		if (loadGraphButton == null) {
			loadGraphButton = new JButton("Load Graph");
			loadGraphButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = FileUtils.getFileChooser();
					if (fileChooser.showOpenDialog(current) == JFileChooser.APPROVE_OPTION) {
						File packageFile = fileChooser.getSelectedFile();

						String basePath = packageFile.getParent();

						try {
							GraphPackage graphPackage = GraphPackage.load(packageFile.getAbsolutePath());

							if (graphPackage.getBackgroundImage() != null) {
								File imageFile = new File(basePath + "/" + graphPackage.getBackgroundImage());
								graphView.setBackgroundImage(ImageIO.read(imageFile));
							}
							graphView.setGraph(Graph.load(basePath + "/" + graphPackage.getGraphFile()));

						} catch (Exception ex) {
							UIUtils.showErrorMessage(current, ex);
						}
					}
				}
			});
		}
		return loadGraphButton;
	}

	/**
	 * Gets the save graph button
	 * @return The button
	 */
	private JButton getSaveGraphButton() {
		if (saveGraphButton == null) {
			saveGraphButton = new JButton("Save Graph");
			saveGraphButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					saveGraph();
				}

			});
		}
		return saveGraphButton;
	}

	/**
	 * Loads the background image
	 */
	private void loadBackgroundImage() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new ImageFileFilter());
		fileChooser.setDialogTitle("Select Image File");
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File image = fileChooser.getSelectedFile();

			try {
				graphView.setBackgroundImage(ImageIO.read(image));
				backgroundFile = image.getAbsolutePath();
			} catch (IOException e) {
				UIUtils.showErrorMessage(this, e);
			}
		}
	}

	/**
	 * Saves the graph
	 */
	private void saveGraph() {
		JFileChooser fileChooser = getFileChooser();
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File packageFile = fileChooser.getSelectedFile();

			//get the base filename without extension
			String basePath = packageFile.getParent();
			String baseName = FileUtils.removeExtension(packageFile);
			try {
				File graphFile = new File(baseName + ".graph.xml");
				if (backgroundFile != null) {
					graphView.getGraph().setWidth(graphView.getBackgroundImage().getWidth(this));
					graphView.getGraph().setHeight(graphView.getBackgroundImage().getHeight(this));
				}
				graphView.getGraph().save(graphFile.getAbsolutePath());

				String imageFileName = null;
				if (backgroundFile != null) {
					File imageFile = new File(backgroundFile);
					imageFileName = imageFile.getName();

					if (!imageFile.getParent().equals(basePath)){
						File destFile = new File(basePath + "/" + imageFile.getName());
						FileUtils.copyFile(imageFile, destFile);
					}
				}

				GraphPackage graphPackage = new GraphPackage(imageFileName, graphFile.getName());
				graphPackage.save(packageFile.getAbsolutePath());
			} catch (Exception e) {
				UIUtils.showErrorMessage(this, e);
			}
		}
	}

	private JFileChooser getFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new GPKFileFilter());
		fileChooser.setDialogTitle("Select Graph Package File");
		fileChooser.setCurrentDirectory(new File(FileUtils.getCurrentDirectory() + "/graph"));
		return fileChooser;
	}
}
