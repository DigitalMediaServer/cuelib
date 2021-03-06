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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import org.digitalmediaserver.cuelib.id3.v1.ID3v1Reader;
import org.digitalmediaserver.cuelib.id3.v2.r00.ID3v2r00Reader;
import org.digitalmediaserver.cuelib.id3.v2.r30.ID3v2r30Reader;
import org.digitalmediaserver.cuelib.id3.v2.r40.ID3v2r40Reader;


/**
 * {@link ID3Reader} factory utility class.
 */
public class ID3ReaderFactory {

	/**
	 * Not to be instantiated.
	 */
	private ID3ReaderFactory() {
	}

	/**
	 * Get the {@link ID3Reader} for the specified {@link ID3Version}.
	 *
	 * @param version The {@link ID3Version}.
	 * @return The {@link ID3Reader}.
	 */
	public static ID3Reader getReader(ID3Version version) {
		switch (version) {
			case ID3v1:
			case ID3v1r0:
			case ID3v1r1:
				return new ID3v1Reader();
			case ID3v2r0:
			case ID3v2r2:
				return new ID3v2r00Reader();
			case ID3v2r3:
				return new ID3v2r30Reader();
			case ID3v2:
			case ID3v2r4:
				return new ID3v2r40Reader();
			default:
				throw new RuntimeException("Unsupported ID3 version: " + version.toString());
		}
	}

	/**
	 * Get the appropriate {@link ID3Reader} for the specified {@link File}.
	 *
	 * @param file The {@link File}.
	 * @return The {@link ID3Reader}.
	 * @throws IOException If an error occurs during the operation.
	 */
	public static ID3Reader getReader(File file) throws IOException {
		return getReader(getVersion(file));
	}

	/**
	 * Get the ID3v2 version for this file. Will be null if no supported ID3 tag
	 * is found.
	 *
	 * @param input the {@link RandomAccessFile}.
	 * @return The {@link ID3Version}.
	 * @throws IOException If an error occurs during the operation.
	 */
	private static ID3Version getID3v2Version(RandomAccessFile input) throws IOException {
		if (input.read() == 'I' && input.read() == 'D' && input.read() == '3') {
			int majorVersion = input.read();
			switch (majorVersion) {
				case 0:
					return ID3Version.ID3v2r0;
				case 3:
					return ID3Version.ID3v2r3;
				case 4:
					return ID3Version.ID3v2r4;
				default:
					return null;
			}
		}
		return null;
	}

	/**
	 * Get the ID3v1 version for this file. Will be null if no supported ID3 tag
	 * is found.
	 *
	 * @param input The {@link RandomAccessFile}.
	 * @return The {@link ID3Version}.
	 * @throws IOException If an error occurs during the operation.
	 */
	private static ID3Version getID3v1Version(RandomAccessFile input) throws IOException {
		ID3Version result = null;

		if (input.length() >= 128) {
			input.seek(input.length() - 128);
			if (input.readUnsignedByte() == 'T' && input.readUnsignedByte() == 'A' && input.readUnsignedByte() == 'G') {
				// Check if there is a track number. If so, it's 1.1.
				input.seek(input.length() - 3);
				int trackNoMarker = input.readUnsignedByte();
				int rawTrackNo = input.readUnsignedByte();
				if (trackNoMarker == 0) {
					if (rawTrackNo == 0) {
						// Could be either v1.0 or v1.1, so just report v1.
						result = ID3Version.ID3v1;
					} else {
						// Definitely v1.1.
						result = ID3Version.ID3v1r1;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Get the ID3 version for this file. Will be null if no supported ID3 tag
	 * is found. Will be the highest supported version if multiple tags are
	 * present.
	 *
	 * @param file The {@link File}.
	 * @return The highest supported {@link ID3Version}.
	 * @throws IOException If an error occurs during the operation.
	 */
	public static ID3Version getVersion(File file) throws IOException {
		// TODO Support the difference between no ID3 & unsupported version of ID3. Are currently both mapped by null.

		try (RandomAccessFile input = new RandomAccessFile(file, "r")) {
			ID3Version result = null;

			// First look for a V2 style tag.
			result = getID3v2Version(input);

			// If we have no result, check for a V1 style tag.
			if (result == null) {
				result = getID3v1Version(input);
			}

			return result;
		}
	}

	/**
	 * Get the ID3 versions for this file. Unsupported versions will not be
	 * reported.
	 *
	 * @param file The {@link File}.
	 * @return The {@link List} of {@link ID3Version}s.
	 * @throws IOException If an error occurs during the operation.
	 */
	public static List<ID3Version> getVersions(File file) throws IOException {
		// TODO Support the difference between no ID3 & unsupported version of ID3. Are currently both mapped by null.
		List<ID3Version> result = new ArrayList<ID3Version>();

		try (RandomAccessFile input = new RandomAccessFile(file, "r")) {
			ID3Version v2result = getID3v2Version(input);
			if (v2result != null) {
				result.add(v2result);
			}

			ID3Version v1result = getID3v1Version(input);
			if (v1result != null) {
				result.add(v1result);
			}

			return result;
		}
	}
}
