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

import static org.digitalmediaserver.cuelib.util.Utils.*;


/**
 * Implementation of the Message interface. Implements a specific type of
 * message that can be freely chosen. For instance, "Warning", "Error", "Debug",
 * etc.
 *
 * @author jwbroek
 */
public abstract class MessageImplementation implements Message {

	/**
	 * The input this message applies to.
	 */
	private String input;

	/**
	 * The line number of the input that this message applies to.
	 */
	private int lineNumber;

	/**
	 * The message text.
	 */
	private String message;

	/**
	 * The message type.
	 */
	private String type;

	/**
	 * Create a new MessageImplementation.
	 *
	 * @param type The type of the message.
	 */
	public MessageImplementation(String type) {
		this.input = "";
		this.lineNumber = -1;
		this.message = "";
		this.type = type;
	}

	/**
	 * Create a new MessageImplementation.
	 *
	 * @param type The type of the message.
	 * @param lineOfInput The line of input that this message applies to.
	 * @param message The message text.
	 */
	public MessageImplementation(String type, LineOfInput lineOfInput, String message) {
		this.input = lineOfInput.getInput();
		this.lineNumber = lineOfInput.getLineNumber();
		this.message = message;
		this.type = type;
	}

	/**
	 * Create a new MessageImplementation.
	 *
	 * @param type The type of the message.
	 * @param input The input that this message applies to.
	 * @param lineNumber The line number of the input that this message applies
	 *            to.
	 * @param message The message text.
	 */
	public MessageImplementation(String type, String input, int lineNumber, String message) {
		this.input = input;
		this.lineNumber = lineNumber;
		this.message = message;
		this.type = type;
	}

	/**
	 * Get a textual representation of this message.
	 *
	 * @return A textual representation of this message.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		if (!isBlank(type)) {
			sb.append(type);
			first = false;
		}
		if (lineNumber > -1) {
			first = appendSeparator(sb, first, " ");
			sb.append('(').append(String.format("%03d", lineNumber)).append(')');
		}
		if (!isBlank(message)) {
			first = appendSeparator(sb, first, " ");
			sb.append(message);
		}
		if (!isBlank(input)) {
			appendSeparator(sb, first, ": ");
			sb.append('"').append(input).append('"');
		}
		return sb.toString();
	}

	/**
	 * Get the input that this message applies to.
	 *
	 * @return The input that this message applies to.
	 */
	@Override
	public String getInput() {
		return input;
	}

	/**
	 * Set the input that this message applies to.
	 *
	 * @param input The input that this message applies to.
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * Get the line number of the input that this message applies to.
	 *
	 * @return The line number of the input that this message applies to.
	 */
	@Override
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Set the line number of the input that this message applies to.
	 *
	 * @param lineNumber The line number of the input that this message applies
	 *            to.
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	/**
	 * Get the text for this message.
	 *
	 * @return The text for this message.
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * Set the text for this message.
	 *
	 * @param message The text for this message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
