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

package net.sf.pathfinder.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A Graph package contains the informations about a graph file and the corresponding image
 * @author Dirk Reske
 *
 */
@XmlRootElement(name = "graphPackage")
public class GraphPackage {

	@XmlElement(name = "backgroundImage")
	private String backgroundImage;
	
	@XmlElement(name = "graphFile")
	private String graphFile;

	/**
	 * Creates a new graph package
	 */
	public GraphPackage() {

	}

	/**
	 * Creates a new graph package
	 * @param backgroundImage The background image
	 * @param graphFile The graph file
	 */
	public GraphPackage(String backgroundImage, String graphFile) {
		this.backgroundImage = backgroundImage;
		this.graphFile = graphFile;
	}

	/**
	 * Gets the background image
	 * @return The background image
	 */
	public String getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * Gets the graph file
	 * @return The graph file
	 */
	public String getGraphFile() {
		return graphFile;
	}

	/**
	 * Saves the graph package to file
	 * @param filename Filename
	 * @throws IOException If any error occures
	 * @throws JAXBException 
	 */
	public void save(String filename) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(GraphPackage.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, new File(filename));
	}

	/**
	 * Loads a graph package from file
	 * @param filename The file
	 * @return A graph package
	 * @throws IOException If any error occures
	 * @throws JAXBException 
	 */
	public static GraphPackage load(String filename) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(GraphPackage.class);
		return (GraphPackage) context.createUnmarshaller().unmarshal(new File(filename));
	}
}
