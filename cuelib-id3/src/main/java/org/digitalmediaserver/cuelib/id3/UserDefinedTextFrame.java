/*
 * Cuelib library for manipulating cue sheets.
 * Copyright (C) 2007-2009 Jan-Willem van den Broek
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.digitalmediaserver.cuelib.id3;

import java.nio.charset.Charset;
import java.util.Properties;


/**
 * The Class UserDefinedTextFrame.
 */
public class UserDefinedTextFrame implements ID3Frame {

	private String description;
	private String text;
	private int totalFrameSize;
	private Charset charset = Charset.forName("ISO-8859-1");
	private Properties flags = new Properties();

	/**
	 * @return the flags
	 */
	@Override
	public Properties getFlags() {
		return flags;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User defined text frame [").append(this.totalFrameSize).append("] ")
			.append(this.charset.toString()).append('\n')
			.append("Flags: ").append(this.flags.toString()).append('\n')
			.append("Description: ").append(this.description).append('\n')
			.append("Text: ").append(this.text);
		return builder.toString();
	}

	/**
	 * Set the {@link Charset}.
	 *
	 * @param charset the {@link Charset}.
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * @return The {@link Charset}.
	 */
	public Charset getCharset() {
		return this.charset;
	}

	/**
	 * @return the declaredSize
	 */
	@Override
	public int getTotalFrameSize() {
		return totalFrameSize;
	}

	/**
	 * @param totalFrameSize the totalFrameSize to set
	 */
	public void setTotalFrameSize(int totalFrameSize) {
		this.totalFrameSize = totalFrameSize;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public CanonicalFrameType getCanonicalFrameType() {
		return CanonicalFrameType.USER_DEFINED_TEXT;
	}
}
