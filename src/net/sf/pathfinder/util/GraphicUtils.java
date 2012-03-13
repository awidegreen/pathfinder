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

package net.sf.pathfinder.util;

import java.awt.Graphics;

/**
 * Provides graphic methods
 * @author Dirk Reske
 *
 */
public final class GraphicUtils {

	private GraphicUtils() {
		
	}
	
	/**
	 * Draws a line with a specified thickness
	 * 
	 * @param g
	 *            Graphics context to paint on
	 * @param x1
	 *            x coordinates of the line start point
	 * @param y1
	 *            y coordinates of the line start point
	 * @param x2
	 *            x coordinates of the line end point
	 * @param y2
	 *            y coordinates of the line end point
	 * @param thickness
	 *            The thickness of the line
	 */
	public static void drawThickLine(Graphics g, int x1, int y1, int x2,
			int y2, int thickness) {
		if (thickness == 1) {
			g.drawLine(x1, y1, x2, y2);
		} else {
			// The thick line is in fact a filled polygon
			int dX = x2 - x1;
			int dY = y2 - y1;
			// line length
			double lineLength = Math.sqrt(dX * dX + dY * dY);

			double scale = (double) (thickness) / (2 * lineLength);

			// The x,y increments from an endpoint needed to create a
			// rectangle...
			double ddx = -scale * (double) dY;
			double ddy = scale * (double) dX;
			ddx += (ddx > 0) ? 0.5 : -0.5;
			ddy += (ddy > 0) ? 0.5 : -0.5;
			int dx = (int) ddx;
			int dy = (int) ddy;

			// Now we can compute the corner points...
			int[] xPoints = new int[4];
			int[] yPoints = new int[4];

			xPoints[0] = x1 + dx;
			yPoints[0] = y1 + dy;
			xPoints[1] = x1 - dx;
			yPoints[1] = y1 - dy;
			xPoints[2] = x2 - dx;
			yPoints[2] = y2 - dy;
			xPoints[3] = x2 + dx;
			yPoints[3] = y2 + dy;

			g.fillPolygon(xPoints, yPoints, 4);
		}
	}
}
