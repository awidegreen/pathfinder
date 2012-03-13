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

import net.sf.pathfinder.model.Graph;
import net.sf.pathfinder.model.Node;
import net.sf.pathfinder.model.Path;
import net.sf.pathfinder.model.PathStatistics;

public class PathCalculation extends Thread implements AlgorithmListener {

	private CalculationListener calculationListener;

	private boolean running = false;

	private Algorithm algorithm;
	private Graph graph;
	private Node start;
	private Node destination;
	private PathStatistics statistics;
	private long startMillis;

	public PathCalculation(Algorithm algorithm, Graph graph, Node start, Node destination, CalculationListener calculationListener) {
		this.algorithm = algorithm;
		this.graph = graph;
		this.start = start;
		this.destination = destination;
		this.calculationListener = calculationListener;
		this.statistics = new PathStatistics(start, destination, algorithm);
	}

	@Override
	public void run() {
		running = true;
		startMillis = System.currentTimeMillis();
		algorithm.calculateRoute(graph, start, destination, this);
		running = false;
	}

	protected void fireCalculationFinished() {
		calculationListener.calculationCompleted(this, statistics);
	}

	@Override
	public void algorithmStep(String message, Path currentPath, boolean finished, boolean found) {
		statistics.getPaths().add(currentPath);
		statistics.setPathFound(found);
		if (finished) {
			long endMillis = System.currentTimeMillis();
			statistics.setDuration(endMillis - startMillis);
			fireCalculationFinished();
		}
	}

	public boolean isRunning() {
		return running;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}
}
