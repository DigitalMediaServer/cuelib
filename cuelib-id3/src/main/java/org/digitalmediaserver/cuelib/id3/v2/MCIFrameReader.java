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
package org.digitalmediaserver.cuelib.id3.v2;

import java.io.IOException;
import java.io.InputStream;
import org.digitalmediaserver.cuelib.id3.MusicCDIdentifierFrame;


/**
 * The Class MCIFrameReader.
 */
public class MCIFrameReader implements FrameReader {

	private final int headerSize;

	/**
	 * Instantiates a new MCI frame reader.
	 *
	 * @param headerSize the header size
	 */
	public MCIFrameReader(int headerSize) {
		this.headerSize = headerSize;
	}

	@Override
	public MusicCDIdentifierFrame readFrameBody(
		int size,
		InputStream input
	) throws IOException, UnsupportedEncodingException, MalformedFrameException {
		MusicCDIdentifierFrame result = new MusicCDIdentifierFrame();
		result.setTotalFrameSize(size + headerSize);
		StringBuilder hexBuilder = new StringBuilder();
		for (int index = 0; index < size; index++) {
			hexBuilder.append(Integer.toHexString(input.read()));
		}
		result.setHexTOC(hexBuilder.toString());

		return result;
	}
}
