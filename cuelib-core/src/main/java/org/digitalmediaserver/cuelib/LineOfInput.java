/*
 * Cuelib library for manipulating cue sheets.
 * Copyright (C) 2007-2008 Jan-Willem van den Broek
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
package org.digitalmediaserver.cuelib;


/**
 * Simple representation of a line of input for use by CueParser.
 *
 * @author jwbroek
 */
public class LineOfInput {

	/**
	 * Number of this line.
	 */
	private final int lineNumber;

	/**
	 * Input at this line.
	 */
	private final String input;

	/**
	 * The CueSheet associated with this input.
	 */
	private final CueSheet associatedSheet;

	/**
	 * Create a new LineOfInput.
	 *
	 * @param lineNumber Number of this line.
	 * @param input The input at this line.
	 * @param associatedSheet The CueSheet associated with this input.
	 */
	public LineOfInput(int lineNumber, String input, CueSheet associatedSheet) {
		this.lineNumber = lineNumber;
		this.input = input;
		this.associatedSheet = associatedSheet;
	}

	/**
	 * Get the CueSheet associated with this input.
	 *
	 * @return The CueSheet associated with this input.
	 */
	public CueSheet getAssociatedSheet() {
		return associatedSheet;
	}

	/**
	 * Get the input at this line.
	 *
	 * @return The input at this line.
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Get the number of this line.
	 *
	 * @return The number of this line.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LineOfInput [").append(String.format("%03d: \"", lineNumber)).append(input).append("\"]");
		return sb.toString();
	}
}
