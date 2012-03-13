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

package net.sf.pathfinder.algo;

import java.util.ArrayList;
import java.util.List;

import net.sf.pathfinder.PathFinder;
import net.sf.pathfinder.help.HelpAttached;
import net.sf.pathfinder.model.Coordinate;
import net.sf.pathfinder.model.Edge;
import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.Path;
import net.sf.pathfinder.util.ResourceManager;
import net.sf.pathfinder.util.UIUtils;
import net.sf.pathfinder.util.properties.PropertySupport;



/**
 * The abstract algorithm is the base for all way finding algorithms
 * 
 * @author Dirk Reske, Armin Widegreen
 * 
 */
public abstract class Algorithm extends PropertySupport implements HelpAttached {

	protected final String HEURISTIC = "Heuristic";
	protected final String HEURISTIC_NONE = "none";
	protected final String HEURISTIC_ITERATIVE_DEEPENING = "iterative deepening";
	protected final String HEURISTIC_DESC = "Sets the heuristic to use";
	protected final String HEURISTIC_DISTANCE = "Distance";
	protected final String HEURISTIC_CLIMBING = "Climbing";
	protected final String HEURISTIC_ORIENTATION = "Orientation-Preferred";
	protected final String HEURISTIC_AIRLINE = "Air Line";
	protected final String HEURISTIC_LOWER_BOUND_ESTIMATE = "lower bound estimate";
	protected final String HEURISTIC_DYN_PROG_PRINCIPLE = "dynamic programming principle";

	protected final String ORIENTATION_DIRECTION = "Orientation-Direction";
	protected final String ORIENTATION_DIRECTION_DESC = "<html><body>Size of orientation-directions:<br>"
			+ "&nbsp;&nbsp;- 4: North, West, South, East<br>"
			+ "&nbsp;&nbsp;- 8: North, NorthWest, West, SouthWest, South, SouthEast, East, NorthEast<br>"
			+ "Only respected if ORIENTATION as HEURISTIC is selected.</body></html>";
	protected final String ORIENTATION_DIRECTION_4 = "4";
	protected final String ORIENTATION_DIRECTION_8 = "8";

	protected final String BASE_ALGORITHM = "Base-Algorithm";
	protected final String BASE_ALGORITHM_DESC = "Select the base algorithm";
	protected final String BASE_ALGORITHM_DEPTHFIRST = "Depth-First";
	protected final String BASE_ALGORITHM_BREADTHFIRST = "Breadth-First";

	protected final String CLIMBING_FUNCTION = "Climbing Function";
	protected final String CLIMBING_FUNCTION_DESC = "The underlying hill-function";
	protected final String CLIMBING_FUNCTION_EXP = "e^(-(x^2+y^2))";
	protected final String CLIMBING_FUNCTION_COS = "cos(x)*cos(y)";
	protected final String CLIMBING_SCALE_BASIS = "Scale Basis";
	protected final String CLIMBING_SCALE_BASIS_DESC = "Is mutlipled with the scale-factor";
	protected final String CLIMBING_SCALE_BASIS_14PI = "factor * 1/4*Pi";
	protected final String CLIMBING_SCALE_BASIS_FACTOR = "factor";
	protected final String CLIMBING_SCALE_FACTOR = "Scale Factor";
	protected final String CLIMBING_SCALE_FACTOR_DESC = "Is mutlipled with the scale-basis";
	protected final String CLIMBING_GRADIENT_FACTOR = "Gradient Factor";
	protected final String CLIMBING_GRADIENT_FACTOR_DESC = "Gradient factor for the algorithm";
	protected final double CLIMBING_GRADIENT_FACTOR_DEFAULT = 0.015;
	protected final int CLIMBING_SCALE_FACTOR_DEFAULT = 2;

	protected final String SEARCH_DEPTH = "search depth";
	protected final int SEARCH_DEPTH_DEFAULT = 1000;

	protected final String STORAGE_LIMIT = "Storage Limit";
	protected final int STORAGE_LIMIT_MAX = 30;
	protected final int STORAGE_LIMIT_DEFAULT = 0;
	protected final String STORAGE_LIMIT_DESC = "<html><body>Limits the number of elements stored in the algorithm-depending (0 equals all possible)"
			+ "storage-structure<br>&nbsp;&nbsp;- DepthFirst-derivatives: Stack<br>"
			+ "&nbsp;&nbsp;- BreadthFirst-derivatives: Queue</body></html>";

	private static List<AlgorithmDescriptor> algorithmList;

	private AlgorithmListener listener;

	private List<Node> backTrackingNodes = new ArrayList<Node>();
	private String name;
	private String helpRefId;

	private static ResourceManager resourceManager = new ResourceManager(
			Algorithm.class);

	/**
	 * Creates a new Algorithm
	 */
	public Algorithm() {
	}

	/**
	 * Gets the id of the help topic
	 * 
	 * @return The help topic id
	 */
	public String getHelpRefId() {
		return helpRefId;
	}

	/**
	 * Gets a specific algorithm
	 * 
	 * @param descriptor
	 *            The {@link AlgorithmDescriptor} to use
	 * @return The algorithm instance
	 */
	public static Algorithm newInstance(AlgorithmDescriptor descriptor) {
		if (descriptor == null) {
			return null;
		}

		try {
			Class< ? > clazz = Class.forName(descriptor.getClassName());
			Object instance = clazz.newInstance();
			if (instance instanceof Algorithm) {
				Algorithm algorithmInstance = (Algorithm) instance;
				algorithmInstance.helpRefId = descriptor.getHelpRefId();
				algorithmInstance.name = descriptor.getName();
				return algorithmInstance;
			} else {
				UIUtils
						.showErrorMessage(
								PathFinder.getMainForm(),
								"The referenced class have to be derived from Algorithm",
								null);
				return null;
			}

		} catch (ClassNotFoundException e) {
			UIUtils.showErrorMessage(PathFinder.getMainForm(),
					"Class not found", e);
			return null;

		} catch (InstantiationException e) {
			UIUtils.showErrorMessage(PathFinder.getMainForm(),
					"Can't instanctiate class", e);
			return null;
		} catch (IllegalAccessException e) {
			UIUtils.showErrorMessage(PathFinder.getMainForm(),
					"Illegal Access", e);
			return null;
		}
	}

	/**
	 * Finds the shortest way between the two specified nodes in the graph
	 * 
	 * @param graph
	 *            Graph to use for calculation
	 * @param start
	 *            Start node for the path
	 * @param destination
	 *            Destination node for the path
	 * @param listener
	 *            Status listener for calculation steps
	 */
	public synchronized void calculateRoute(Graph graph, Node start,
			Node destination, AlgorithmListener listener) {
		if (start == null || destination == null) {
			return;
		}
		this.listener = listener;
		this.backTrackingNodes.clear();
		calculateRoute(graph, start, destination);
	}

	/**
	 * Finds the shortest way between the two specified nodes in the graph
	 * 
	 * @param graph
	 *            Graph to use for calculation
	 * @param start
	 *            Start node for the path
	 * @param destination
	 *            Destination node for the path
	 */
	protected abstract void calculateRoute(Graph graph, Node start,
			Node destination);

	/**
	 * Invokes the {@link AlgorithmListener#algorithmStep(String, Path)} on the
	 * registered listener
	 * 
	 * @param message
	 *            Message for the current step
	 * @param currentPath
	 *            The current path
	 * @param finished
	 *            True if the calculation is finished, false otherwise
	 * @param found
	 *            True if a path was found, false otherwise
	 */
	protected void onAlgorithmStep(String message, Path currentPath,
			boolean finished, boolean found) {
		if (listener != null) {
			if (currentPath != null) {
				currentPath.getBackTrackingNodes().clear();
				currentPath.getBackTrackingNodes().addAll(backTrackingNodes);
			}
			listener.algorithmStep(message, currentPath, finished, found);
		}
	}

	/**
	 * Adds a backtracking node
	 * @param node The node
	 */
	protected void addBackTrackingNode(Node node) {
		if (!backTrackingNodes.contains(node)) {
			backTrackingNodes.add(node);
		}
	}

	/**
	 * Gets a list of available algorithms
	 * 
	 * @return The list of algorithms
	 */
	public static List<AlgorithmDescriptor> getAlgorithms() {
		if (algorithmList == null) {
			algorithmList = new ArrayList<AlgorithmDescriptor>();
			int count = Integer.parseInt(resourceManager
					.getString("algorithm_count"));
			for (int i = 1; i <= count; i++) {
				String algorithm = resourceManager.getString("algorithm_" + i);
				String className = resourceManager.getString(algorithm
						+ ".class");
				String name = resourceManager.getString(algorithm + ".name");
				String helpRefId = resourceManager.getString(algorithm
						+ ".helpRefId");

				algorithmList.add(new AlgorithmDescriptor(name, className,
						helpRefId));

			}
		}
		return algorithmList;
	}

	/**
	 * Gets the name of the algorithm
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the heuristic parameter
	 * @return The heuristic
	 */
	protected String getHeuristic() {
		if (getProperty(HEURISTIC) instanceof String) {
			return (String) getProperty(HEURISTIC);
		} else {
			return null;
		}
	}

	/**
	 * Gets the storage size paramater
	 * @return The storage size
	 */
	protected int getStorageSize() {
		if (getProperty(STORAGE_LIMIT) instanceof Integer) {
			return (Integer) getProperty(STORAGE_LIMIT);
		} else {
			return STORAGE_LIMIT_DEFAULT;
		}
	}

	private String getOrientationDirection() {
		if (getProperty(ORIENTATION_DIRECTION) instanceof String) {
			return (String) getProperty(ORIENTATION_DIRECTION);
		} else {
			return ORIENTATION_DIRECTION_4;
		}
	}

	protected String getBaseAlgorithm() {
		if (getProperty(BASE_ALGORITHM) instanceof String) {
			return (String) getProperty(BASE_ALGORITHM);
		} else {
			return BASE_ALGORITHM_BREADTHFIRST;
		}
	}

	protected int getSearchDepth() {
		if (getProperty(SEARCH_DEPTH) instanceof Integer) {
			return (Integer) getProperty(SEARCH_DEPTH);
		} else {
			return SEARCH_DEPTH_DEFAULT;
		}
	}

	protected int getClimbingScaleFactor() {
		if (getProperty(CLIMBING_SCALE_FACTOR) instanceof Integer) {
			return (Integer) getProperty(CLIMBING_SCALE_FACTOR);
		} else {
			return CLIMBING_SCALE_FACTOR_DEFAULT;
		}
	}

	protected double getGradientFactor() {
		if (getProperty(CLIMBING_GRADIENT_FACTOR) instanceof Double) {
			return (Double) getProperty(CLIMBING_GRADIENT_FACTOR);
		} else {
			return CLIMBING_GRADIENT_FACTOR_DEFAULT;
		}
	}

	protected String getClimbingScaleBasis() {
		if (getProperty(CLIMBING_SCALE_BASIS) instanceof String) {
			return (String) getProperty(CLIMBING_SCALE_BASIS);
		} else {
			return CLIMBING_SCALE_BASIS_FACTOR;
		}
	}

	protected String getHillFunction() {
		if (getProperty(CLIMBING_FUNCTION) instanceof String) {
			return (String) getProperty(CLIMBING_FUNCTION);
		} else {
			return CLIMBING_FUNCTION_EXP;
		}
	}

	/**
	 * Estimates the distance between the two specified nodes
	 * 
	 * @param current
	 *            Current node
	 * @param destination
	 *            Destination node
	 * @return The estimated distance
	 */
	protected double estimateDistance(Node current, Node destination) {
		return current.getCoordinate().distance(destination.getCoordinate());
	}

	/**
	 * Gets the orientation of two nodes
	 * 
	 * @param current
	 *            The current node
	 * @param destination
	 *            The destination node
	 * @return The orientation
	 */
	protected Orientation getOrientation(Node current, Node destination) {
		double r = estimateDistance(current, destination);
		Coordinate current_ = new Coordinate(destination.getCoordinate().getX()
				- current.getCoordinate().getX(), -(destination.getCoordinate()
				.getY() - current.getCoordinate().getY()));
		int v = current_.getY();
		int u = current_.getX();
		double sin_degree = Math.asin(v / r) * 180 / Math.PI;
		double cos_degree = Math.acos(u / r) * 180 / Math.PI;
		Orientation result = Orientation.NORTH;

		/*
		 * some optimization possible: cos from 0 to 180 rather 180 to 0 for the
		 * second half of a unit circle if sin > 0 (first half of unit circle)
		 * take the given cos degree value otherwise if sin < 0 (second half of
		 * unit circle) take 360-cos = degree-value
		 */
		if (getOrientationDirection().equals(ORIENTATION_DIRECTION_8)) {
			if ((sin_degree > 67.5 && sin_degree <= 90)
					&& (cos_degree > 67.5 && cos_degree <= 112.5))
				result = Orientation.NORTH;
			else if ((sin_degree < 67.5 && sin_degree >= 22.5)
					&& (cos_degree > 112.5 && cos_degree <= 157.5))
				result = Orientation.NORTHWEST;
			else if ((sin_degree < 22.5 && sin_degree >= -22.5)
					&& (cos_degree > 157.5 && cos_degree <= 180))
				result = Orientation.WEST;
			else if ((sin_degree < -22.5 && sin_degree >= -67.5)
					&& (cos_degree < 157.5 && cos_degree >= 112.5))
				result = Orientation.SOUTHWEST;
			else if ((sin_degree < -67.5 && sin_degree >= -90)
					&& (cos_degree < 112.5 && cos_degree >= 67.5))
				result = Orientation.SOUTH;
			else if ((sin_degree > -67.5 && sin_degree <= -22.5)
					&& (cos_degree < 67.5 && cos_degree >= 22.5))
				result = Orientation.SOUTHEAST;
			else if ((sin_degree > -22.5 && sin_degree <= 22.5)
					&& (cos_degree < 22.5 && cos_degree >= 0))
				result = Orientation.EAST;
			else if ((sin_degree > 22.5 && sin_degree <= 67.5)
					&& (cos_degree > 22.5 && cos_degree <= 67.5))
				result = Orientation.NORTHEAST;
		} else if (getOrientationDirection().equals(ORIENTATION_DIRECTION_4)) {
			if (sin_degree > 45 && sin_degree < 90)
				result = Orientation.NORTH;
			else if (cos_degree > 135 && cos_degree < 180)
				result = Orientation.WEST;
			else if (sin_degree < -45 && sin_degree > -90)
				result = Orientation.SOUTH;
			else if (cos_degree > 0 && cos_degree < 45)
				result = Orientation.EAST;
		}

		return result;
	}

	protected double getAirLineValue(Node start, Node destination, Node current) {
		Vector a = new Vector(start.getCoordinate().getX()
				- current.getCoordinate().getX(), start.getCoordinate().getY()
				- current.getCoordinate().getY());
		Vector b = new Vector(destination.getCoordinate().getX()
				- current.getCoordinate().getX(), destination.getCoordinate()
				.getY()
				- current.getCoordinate().getY());

		double cos_y = Vector.getScalarProduct(a, b)
				/ (a.getLength() * b.getLength());
		return (Math.toDegrees(Math.acos(cos_y)));
	}

	/**
	 * Build the resulting path.
	 * 
	 * @param current
	 *            NodeToken
	 * @return result Path
	 */
	protected Path buildPath(NodeToken current) {
		Path path = new Path();

		path.getPathSteps().add(new Path.PathStep(current.currentNode, null));
		while (current.prevNodeToken != null) {
			path.getPathSteps().add(
					new Path.PathStep(current.prevNodeToken.currentNode,
							current.previousEdge));
			current = current.prevNodeToken;
		}

		return path;
	}

	/**
	 * Calculates the grade of the given node, depending on the
	 * global-destination-distance
	 * 
	 * @return the grade
	 */
	protected double calculateClimbGrade(int graphSizeWidth,
			int graphSizeHeight, Node node, Node goal) {
		double maxScaleSize = getClimbingScaleFactor();
		double longestDistance = 1;
		double result = 0;

		if (getClimbingScaleBasis().equals(CLIMBING_SCALE_BASIS_14PI)) {
			maxScaleSize *= 0.25 * Math.PI;
		}

		if (goal.getCoordinate().getX() > graphSizeWidth / 2) {
			if (goal.getCoordinate().getY() > graphSizeHeight / 2)
				longestDistance = estimateDistance(new Node(
						new Coordinate(0, 0)), goal);
			else
				longestDistance = estimateDistance(new Node(new Coordinate(0,
						graphSizeHeight)), goal);
		} else {
			if (goal.getCoordinate().getY() > graphSizeHeight / 2)
				longestDistance = estimateDistance(new Node(new Coordinate(
						graphSizeWidth, 0)), goal);
			else
				longestDistance = estimateDistance(new Node(new Coordinate(
						graphSizeWidth, graphSizeHeight)), goal);
		}

		double x = goal.getCoordinate().getX() - node.getCoordinate().getX();
		double x_percent = x * 100 / longestDistance;
		x = x_percent * maxScaleSize / 100;
		double y = goal.getCoordinate().getY() - node.getCoordinate().getY();
		double y_percent = y * 100 / longestDistance;
		y = y_percent * maxScaleSize / 100;

		if (getHillFunction().equals(CLIMBING_FUNCTION_EXP))
			result = Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)));
		else if (getHillFunction().equals(CLIMBING_FUNCTION_COS))
			result = Math.cos(x) * Math.cos(y);

		return result;
	}

	protected class NodeToken implements Comparable<NodeToken> {
		public Node currentNode;
		public Edge previousEdge;
		public NodeToken prevNodeToken;
		public double value;

		public NodeToken(Node currentNode, Edge previousEdge) {
			this(currentNode, previousEdge, null, 0.0);
		}

		public NodeToken(Node currentNode, Edge previousEdge, double value) {
			this(currentNode, previousEdge, null, value);
		}

		public NodeToken(Node currentNode, Edge previousEdge,
				NodeToken prevNodeToken) {
			this(currentNode, previousEdge, prevNodeToken, 0.0);
		}

		public NodeToken(Node currentNode, Edge previousEdge,
				NodeToken prevNodeToken, double value) {
			this.currentNode = currentNode;
			this.previousEdge = previousEdge;
			this.prevNodeToken = prevNodeToken;
			this.value = value;
		}

		@Override
		public int compareTo(NodeToken o) {
			if (this.value == o.value)
				return 0;
			else if ((this.value) > o.value)
				return 1;
			else
				return -1;
		}
	}

	protected enum Orientation {
		NORTH, NORTHWEST, WEST, SOUTHWEST, SOUTH, SOUTHEAST, EAST, NORTHEAST
	}

	private static class Vector {
		public double x;
		public double y;

		public Vector() {
		}

		public Vector(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getLength() {
			return Math.hypot(x, y);
		}

		public double getScalarProduct(Vector vec) {
			return getScalarProduct(this, vec);
		}

		public static double getScalarProduct(Vector a, Vector b) {
			return ((a.x * b.x) + (a.y * b.y));
		}
	}
}
