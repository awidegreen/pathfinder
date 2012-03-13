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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import net.sf.pathfinder.model.Coordinate;
import net.sf.pathfinder.model.Edge;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.Path;
import net.sf.pathfinder.model.PathStatistics;
import net.sf.pathfinder.util.GraphicUtils;
import net.sf.pathfinder.util.StringUtils;


public class JGraphView extends JComponent implements PathHistoryModelListener {

	private final int NODE_RADIUS = 8;
	//private final int BACKTRACKING_ALPHA = 100;

	private double scaleFactor = 1.0;
	private Image backgroundImage;

	private boolean autoSelect = true;

	private Node selectedNode = null;
	private Edge currentEdge = null;

	private Graph graph;

	private boolean editingMode = false;

	private boolean paintBackground = true;
	private boolean paintEdges = true;
	private boolean paintNodes = true;

	private Map<Rectangle, Node> nodePositions = new HashMap<Rectangle, Node>();

	private List<GraphSelectionListener> graphSelectionListeners = new Vector<GraphSelectionListener>();

	private PathHistoryModel pathHistoryModel;
	
	private List<Node> highlightedNodes = new ArrayList<Node>();

	/**
	 * Creates a new JGraphView
	 */
	public JGraphView(PathHistoryModel pathHistoryModel) {
		if (pathHistoryModel != null) {
			this.pathHistoryModel = pathHistoryModel;
			this.pathHistoryModel.addPathHistoryModelListener(this);
		}
		enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
	}

	/**
	 * Sets the highlighted state of a specified node
	 * @param node The node
	 * @param highLighted True if the node should be highlithed, otherwise false
	 */
	public void setNodeHighlighted(Node node, boolean highLighted) {
		if (!highlightedNodes.contains(node) && highLighted) {
			highlightedNodes.add(node);
		}
		else {
			highlightedNodes.remove(node);
		}
		repaint();
	}
	
	/**
	 * Processes the mouse events
	 */
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);

		if (!editingMode && (e.getID() == MouseEvent.MOUSE_CLICKED) && (e.getButton() == MouseEvent.BUTTON1)) {
			Node clickedNode = getNodeAt(e.getPoint().x, e.getPoint().y);
			if (clickedNode != null) {
				setSelectedNode(clickedNode);
			}
			return;
		}

		if (e.getID() == MouseEvent.MOUSE_CLICKED) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Node clickedNode= getNodeAt(e.getPoint().x, e.getPoint().y);
				//If no node was clicked -> create new
				if (clickedNode == null) {
					Coordinate coordinate = new Coordinate(e.getPoint().x, e.getPoint().y);
					coordinate = retranslateCoordinate(coordinate);
					clickedNode = new Node(coordinate);
					getGraph().getNodes().add(clickedNode);
				}

				if (currentEdge != null) {
					currentEdge.setDestination(clickedNode);
					currentEdge.getSource().getEdges().add(currentEdge);
					createReverseEdge(currentEdge);
					currentEdge = null;
				}
				else if (selectedNode != null && !selectedNode.equals(clickedNode)) {
					Edge edge = new Edge(selectedNode, clickedNode);
					selectedNode.getEdges().add(edge);
					createReverseEdge(edge);
				}

				//if auto select enabled -> select new node
				if (autoSelect) {
					setSelectedNode(clickedNode);
				}

				repaint();
			}
			else if (e.getButton() == MouseEvent.BUTTON2) { //Unselect or delete node
				Node clickedNode = getNodeAt(e.getPoint().x, e.getPoint().y);
				if (clickedNode != null) {
					deleteNode(clickedNode);
				}

				setSelectedNode(null);
				currentEdge = null;
				repaint();
			}
			else if (e.getButton() == MouseEvent.BUTTON3) {
				if (selectedNode != null) {
					if (currentEdge == null) {
						currentEdge = new Edge();
						currentEdge.setSource(selectedNode);
					}

					Coordinate wayPoint = new Coordinate(e.getPoint().x, e.getPoint().y);
					wayPoint = retranslateCoordinate(wayPoint);
					currentEdge.getWayPoints().add(wayPoint);
					repaint();
				}
			}
		}
	}

	/**
	 * Creates the reverse edge for the specified edge
	 * @param edge The edge to create the reverse edge for
	 */
	private void createReverseEdge(Edge edge) {
		Edge reverse = new Edge(edge.getDestination(), edge.getSource());
		reverse.getWayPoints().addAll(edge.getWayPoints());
		Collections.reverse(reverse.getWayPoints());
		reverse.getSource().getEdges().add(reverse);
	}

	/**
	 * Deletes the specified node
	 * @param node The node to delete
	 */
	private void deleteNode(Node node) {
		for (Edge edge : node.getEdges()) {
			Edge reverseEdge = edge.getDestination().getEdges().findEdge(node);
			if (reverseEdge != null) {
				edge.getDestination().getEdges().remove(reverseEdge);
			}
		}
		graph.getNodes().remove(node);
	}

	/**
	 * Gets the node at the specified position
	 * @param x The x coordinates
	 * @param y The y coordinates
	 * @return The node at the specified position or null, if there is no node
	 */
	private Node getNodeAt(int x, int y) {
		for (Rectangle rect : nodePositions.keySet()) {
			if (rect.contains(x, y))
				return nodePositions.get(rect);
		}
		return null;
	}

	/**
	 * Paints the component
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// if there is a background image set -> paint it
		if (paintBackground && (backgroundImage != null)) {
			int height = (int)(backgroundImage.getHeight(this) * scaleFactor);
			int width = (int)(backgroundImage.getWidth(this) * scaleFactor);
			g.drawImage(backgroundImage, 0, 0,width, height,this);
		}

		paintGraph(g, isPaintGraph());

		if (pathHistoryModel != null) {
			for (int i = 0; i < pathHistoryModel.size(); i++) {
				PathStatistics statistics = pathHistoryModel.get(i);
				if (!statistics.isVisible()) {
					continue;
				}

				if (statistics.isPaintBacktrackingNodes()) {
					for (Node node : statistics.getSelectedPath().getBackTrackingNodes()) {
						paintNode(g, node,true, statistics.getBackTrackingColor(), true, NODE_RADIUS);
					}
				}
				paintPath(g, statistics.getSelectedPath(), statistics.getPathColor());	
			}
		}
		
		for (Node node : highlightedNodes) {
			paintNode(g, node, true, Color.red, true, (int)(NODE_RADIUS * 1.5));
		}
	}


	/**
	 * Paints the graph
	 * @param g Graphics context to paint on
	 */
	private void paintGraph(Graphics g, boolean realPaint) {

		//paint the edges
		if (paintEdges && realPaint) {
			for (Node node : getGraph().getNodes()) {
				for (Edge edge : node.getEdges()) {
					paintEdge(g, edge, 2, Color.blue);
				}
			}
			if (currentEdge != null) {
				paintEdge(g, currentEdge, 2, Color.blue);
			}
		}

		nodePositions.clear();
		for (Node node : getGraph().getNodes()) {
			Color color = null;
			if (node.equals(selectedNode))
				color = Color.red;
			else {
				if (StringUtils.isNullOrEmpty(node.getName())) {
					color = Color.gray;
				}
				else {
					color = Color.green;
				}
			}
			paintNode(g, node, true, color, realPaint, NODE_RADIUS);
		}
	}

	private Coordinate retranslateCoordinate(Coordinate coordinate) {
		return new Coordinate((int)(coordinate.getX() / scaleFactor),
				(int)(coordinate.getY() / scaleFactor));
	}

	private Coordinate translateCoordinate(Coordinate coordinate) {
		return new Coordinate((int)(coordinate.getX() * scaleFactor),
				(int)(coordinate.getY() * scaleFactor));
	}

	private void paintPath(Graphics g, Path path, Color color) {
		if (path.getPathSteps().size() == 0) {
			return;
		}

		Path.PathStep previous = path.getPathSteps().get(0);
		for (int i = 0; i < path.getPathSteps().size(); i++) {
			Path.PathStep current = path.getPathSteps().get(i);

			if (current.next != null) {
				paintEdge(g,current.next,3, color);
			}
			if (i == 0) {
				paintNode(g, previous.node, false, Color.green, true, NODE_RADIUS);
			}
			else if (i == (path.getPathSteps().size() - 1)) {
				paintNode(g, current.node, false, Color.green, true, NODE_RADIUS);
			}
			else {
				paintNode(g, current.node, false, color, true, NODE_RADIUS);
			}

			previous = current;
		}
	}

	private void paintNode(Graphics g, Node node, boolean paintUnnamed, Color color, boolean realPaint, int radius) {
		if (!paintUnnamed && StringUtils.isNullOrEmpty(node.getName())) {
			return;
		}

		Coordinate coord = translateCoordinate(node.getCoordinate());
		Rectangle rect = new Rectangle(coord.getX() - (radius / 2),
				coord.getY() - (radius / 2),
				radius, 
				radius);

		if ((!realPaint && !StringUtils.isNullOrEmpty(node.getName())) || realPaint) {
			nodePositions.put(rect, node);
			
			g.setColor(color);
			g.fillOval(rect.x, rect.y, rect.width, rect.height);
		}

	}

	private void paintEdge(Graphics g, Edge edge, int thickness, Color c) {
		g.setColor(c);
		Coordinate previous = translateCoordinate(edge.getSource().getCoordinate());
		//draw and connect way points
		for (Coordinate wayPoint : edge.getWayPoints()) {
			wayPoint = translateCoordinate(wayPoint);
			GraphicUtils.drawThickLine(g, previous.getX(), previous.getY(),
					wayPoint.getX(), wayPoint.getY(), thickness);

			/**
			Rectangle rect = new Rectangle(wayPoint.getX() - (WAYPOINT_RADIUS / 2),
					wayPoint.getY() - (WAYPOINT_RADIUS / 2), 
					WAYPOINT_RADIUS, 
					WAYPOINT_RADIUS);

			g.fillOval(rect.x, rect.y, rect.width, rect.height);
			 **/

			previous = wayPoint;
		}

		if (edge.getDestination() != null) {
			Coordinate destination = translateCoordinate(edge.getDestination().getCoordinate());
			GraphicUtils.drawThickLine(g, previous.getX(), previous.getY(),
					destination.getX(), 
					destination.getY(), 2);
		}
	}

	/**
	 * Invokes the {@link GraphSelectionListener#selectedNodeChanged(Object, Node)} methods on all
	 * registered graph selection listeners
	 * @see GraphSelectionListener
	 */
	protected void onSelectedNodeChanged() {
		GraphSelectionListener[] listeners = 
			graphSelectionListeners.toArray(new GraphSelectionListener[graphSelectionListeners.size()]);

		for (GraphSelectionListener listener : listeners) {
			listener.selectedNodeChanged(this, selectedNode);
		}
	}

	/**
	 * Gets the background image
	 * 
	 * @return The background image
	 */
	public Image getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * Sets the background image
	 * 
	 * @param backgroundImage
	 *            The background image
	 */
	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;

		if (backgroundImage != null){
			setPreferredSize(calculatePreferredSize());
		}

		repaint();
		revalidate();
	}

	/**
	 * Gets the selected node
	 * @return The selected node
	 */
	public Node getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Sets the selected node
	 * @param selectedNode The selected node
	 */
	public void setSelectedNode(Node selectedNode) {
			this.selectedNode = selectedNode;
			onSelectedNodeChanged();
			repaint();
	}

	/**
	 * Gets the graph to draw.
	 * If the control is in editing mode and there is no graph loaded, a new one will be created
	 * @return The graph
	 */
	public Graph getGraph() {
		if (graph == null) {
			graph = new Graph();
		}
		return graph;
	}

	/**
	 * Sets the graph to draw
	 * @param graph The graph
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
		repaint();
	}

	/**
	 * Adds a graph selection listener to the graph view
	 * @param listener The listener to add
	 * @see GraphSelectionListener
	 */
	public void addGraphSelectionListener(GraphSelectionListener listener) {
		if (!graphSelectionListeners.contains(listener)) 
			graphSelectionListeners.add(listener);
	}

	/**
	 * Removes a graph selection listener from the graph view
	 * @param listener The listener to remove
	 * @see GraphSelectionListener
	 */
	public void removeGraphSelectionListener(GraphSelectionListener listener) {
		if (!graphSelectionListeners.contains(listener))
			graphSelectionListeners.remove(listener);
	}

	/**
	 * Is the editing mode enabled
	 * @return True if the editing mode is enabled, otherwise false
	 */
	public boolean isEditingMode() {
		return editingMode;
	}

	/**
	 * Enables or disables the editing mode
	 * @param editingMode True if the editing mode should be enabled, otherwise false
	 */
	public void setEditingMode(boolean editingMode) {
		this.editingMode = editingMode;

		if (!editingMode) {
			setSelectedNode(null);
			repaint();
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAutoSelect() {
		return autoSelect;
	}

	/**
	 * 
	 * @param autoSelect
	 */
	public void setAutoSelect(boolean autoSelect) {
		this.autoSelect = autoSelect;
	}

	/**
	 * Gets the value indicating if the background gets painted
	 * @return True if the background gets painted, otherwise false
	 */
	public boolean isPaintBackground() {
		return paintBackground;
	}

	/**
	 * Sets the value indicating if the background gets painted
	 * @param paintBackground True if the background should be painted, otherwise false
	 */
	public void setPaintBackground(boolean paintBackground) {
		this.paintBackground = paintBackground;
		repaint();
	}

	/**
	 * Are the edges get painted
	 * @return True if the edges get painted, otherwise false
	 */
	public boolean isPaintEdges() {
		return paintEdges;
	}

	/**
	 * Should the nodes get painted
	 * @param paintEdges True if they should be painted, otherwise false
	 */
	public void setPaintEdges(boolean paintEdges) {
		this.paintEdges = paintEdges;
		repaint();
	}

	/**
	 * Are the nodes get painted
	 * @return True if the nodes gets painted, otherwise false
	 */
	public boolean isPaintNodes() {
		return paintNodes;
	}

	/**
	 * Should the nodes get paintes
	 * @param paintNodes True if the should be painted, otherwise false
	 */
	public void setPaintNodes(boolean paintNodes) {
		this.paintNodes = paintNodes;
		repaint();
	}

	/**
	 * Is the graph gets painted.
	 * This will return true, if the {@link #isPaintEdges()} and {@link #isPaintNodes()} returns true
	 * @return True if the graph gets painted
	 */
	public boolean isPaintGraph() {
		return isPaintNodes() && isPaintEdges();
	}

	/**
	 * Should the graph gets painted.
	 * This sets {@link #setPaintEdges(boolean)} and {@link #setPaintNodes(boolean)} to the specified value.
	 * @param paintGraph True if the graph should be painted, otherwise false
	 */
	public void setPaintGraph(boolean paintGraph) {
		setPaintEdges(paintGraph);
		setPaintNodes(paintGraph);
		repaint();
	}

	/**
	 * Gets the scale factor
	 * @return The scale factor
	 */
	public double getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * Sets the scale factor
	 * @param scaleFactor The scale factor
	 */
	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
		setPreferredSize(calculatePreferredSize());
		repaint();
		revalidate();
	}

	private Dimension calculatePreferredSize() {
		if (backgroundImage != null) {
			int width = (int)(backgroundImage.getWidth(this) * scaleFactor);
			int height = (int)(backgroundImage.getHeight(this) * scaleFactor);
			return new Dimension(width, height);
		}
		return getPreferredSize();
	}

	@Override
	public void pathStatisticsAdded(PathHistoryModel source,
			PathStatistics statistics) {
		repaint();
	}

	@Override
	public void pathStatisticsChanged(PathHistoryModel source,
			PathStatistics statistics) {
		repaint();
	}

	@Override
	public void pathStatisticsRemoved(PathHistoryModel source,
			PathStatistics statistics) {
		repaint();
	}
}
