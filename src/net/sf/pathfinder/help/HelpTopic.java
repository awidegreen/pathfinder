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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

/**
 * A help topic
 * @author Dirk Reske
 *
 */
@XmlRootElement(name = HelpTopic.TAG)
public class HelpTopic {

	/**
	 * The tag name
	 */
	@XmlTransient
	public static final String TAG = "topic";

	private String title;
	private String id;
	private String content;

	/**
	 * Creates a new HelpTopic
	 */
	public HelpTopic() {

	}

	/**
	 * Gets the title of the topic
	 * 
	 * @return The title
	 */
	@XmlAttribute(name = "title")
	public String getTitle() {
		return title.trim();
	}

	/**
	 * Sets the title of the topic
	 * 
	 * @param title
	 *            The title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the id of the topic
	 * 
	 * @return The id
	 */
	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	/**
	 * Sets the id of the topic
	 * 
	 * @param id
	 *            The id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the text of the topic
	 * 
	 * @return The text
	 */
	@XmlValue
	public String getContent() {
		return content;
	}

	/**
	 * Sets the text of the topic
	 * 
	 * @param text
	 *            The topic
	 */
	public void setContent(String text) {
		this.content = text;
	}
}
