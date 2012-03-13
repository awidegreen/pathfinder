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

package net.sf.pathfinder.help;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.pathfinder.util.StringUtils;


@XmlRootElement(name = Help.TAG)
public class Help {

	@XmlTransient
	public static final String TAG = "help";

	private List<HelpCategory> categories = new ArrayList<HelpCategory>();

	/**
	 * Creates a new help
	 */
	public Help() {

	}

	/**
	 * Finds the topic with the specified id
	 * 
	 * @param topicId
	 *            The topic id to find
	 * @return The topic or null if the id was not found
	 */
	public HelpTopic findTopic(String topicId) {
		if (StringUtils.isNullOrEmpty(topicId)) {
			return null;
		}

		for (HelpCategory category : getCategories()) {
			for (HelpTopic topic : category.getTopics()) {
				if (topicId.equals(topic.getId())) {
					return topic;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the help categories
	 * 
	 * @return The categories
	 */
	@XmlElements(@XmlElement(name = HelpCategory.TAG, type = HelpCategory.class))
	public List<HelpCategory> getCategories() {
		return categories;
	}

	/**
	 * Loads a help instance from a specified file
	 * 
	 * @param filename
	 *            The filename to load
	 * @return The help instance
	 * @throws JAXBException
	 *             If any error occures
	 * @throws IOException 
	 */
	public static Help loadFromFile(URL url) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(Help.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (Help) unmarshaller.unmarshal(url);
	}

	/**
	 * Saves the help instance to file
	 * 
	 * @param filename
	 *            The name of the file to write
	 * @throws JAXBException
	 *             If any error occures
	 */
	public void saveToFile(String filename) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Help.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, new File(filename));
	}
}
