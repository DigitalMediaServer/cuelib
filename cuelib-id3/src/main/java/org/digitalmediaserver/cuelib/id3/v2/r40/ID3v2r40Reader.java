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
package org.digitalmediaserver.cuelib.id3.v2.r40;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.digitalmediaserver.cuelib.id3.AbstractID3v2Reader;
import org.digitalmediaserver.cuelib.id3.ID3Tag;
import org.digitalmediaserver.cuelib.id3.ID3Version;
import org.digitalmediaserver.cuelib.id3.v2.MalformedFrameException;
import org.digitalmediaserver.cuelib.id3.v2.UnsupportedEncodingException;
import org.digitalmediaserver.cuelib.id3.v2.UnsynchedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class ID3v2r40Reader.
 */
public class ID3v2r40Reader extends AbstractID3v2Reader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ID3v2r40Reader.class);

	// TODO Handle cases where tag is not at start of file.

	@Override
	protected boolean isVersionValid(int majorVersion, int revision) {
		return majorVersion == 4 && revision == 0;
	}

	@Override
	public ID3Tag read(File file) throws IOException, UnsupportedEncodingException, MalformedFrameException {
		ID3Tag tag = new ID3Tag();

		try (FileInputStream input = new FileInputStream(file)) {
			if (input.read() == 'I' && input.read() == 'D' && input.read() == '3') {
				int majorVersion = input.read();
				int revision = input.read();
				if (majorVersion == 4 && revision == 0) {
					tag.setVersion(ID3Version.ID3v2r4);
					tag.setRevision(0);
					int flags = input.read();
					boolean unsyncUsed = (flags & 128) == 128;
					tag.getFlags().setProperty(ID3Tag.UNSYNC_USED, Boolean.toString(unsyncUsed));
					boolean extendedHeaderUsed = (flags & 64) == 64;
					boolean experimental = (flags & 32) == 32;
					tag.getFlags().setProperty(ID3Tag.EXPERIMENTAL, Boolean.toString(experimental));
					boolean footerPresent = (flags & 16) == 16;
					tag.getFlags().setProperty(ID3Tag.FOOTER_PRESENT, Boolean.toString(footerPresent));
					// TODO Check that other flags are not set.
					int size = 0;
					for (int index = 0; index < 4; index++) {
						int sizeByte = input.read();
						if (sizeByte >= 128) {
							size = -1;
							break;
						}
						size = size * 128 + sizeByte;
					}
					if (size >= 0) {
						tag.setDeclaredSize(size);

						// Read the extended header, if it is used.
						if (extendedHeaderUsed) {
							long extendedHeaderSize = 0;
							for (int index = 0; index < 4; index++) {
								int sizeByte = input.read();
								if (sizeByte >= 128) {
									extendedHeaderSize = -1;
									break;
								}
								extendedHeaderSize = extendedHeaderSize * 128 + sizeByte;
							}
							if (extendedHeaderSize >= 6) {
								tag.getFlags().put(ID3Tag.EXTENDED_HEADER_SIZE, Long.toString(extendedHeaderSize));
								int numberOfFlagBytes = input.read();
								if (numberOfFlagBytes == 1) {
									int extendedFlags = input.read();
									boolean tagIsAnUpdate = (extendedFlags & 64) == 64;
									tag.getFlags().put(ID3Tag.TAG_IS_UPDATE, Boolean.toString(tagIsAnUpdate));
									boolean crcPresent = (extendedFlags & 32) == 32;
									boolean tagRestrictionsSet = (extendedFlags & 16) == 16;
									LOGGER.debug("Tag restrictions set: {}", tagRestrictionsSet);

									if (tagIsAnUpdate) {
										int updateFlagDataLength = input.read();
										if (updateFlagDataLength != 0) {
											LOGGER.warn(
												"Invalid length for \"tag is an update\" flag encountered. Should be 0, but is {}",
												updateFlagDataLength
											);
											// TODO Handle or throw exception.
										}
									}

									if (crcPresent) {
										int crcLength = input.read();
										if (crcLength == 5) {
											// 35 bit value, but according to spec the upper 4 are not
											// used, so would fit in the positive part of a signed integer.
											// Seems odd though. I wonder if the spec doesn't mean the
											// upper 5 bits of the "raw" (non-sync-safe) bytes...
											// Using a long to be safe.
											long crc =
												input.read() << 28 | input.read() << 21 |
												input.read() << 14 | input.read() << 7 | input.read();
											tag.getFlags().put(ID3Tag.CRC32_HEX, Long.toHexString(crc));
											// TODO Use this CRC32_HEX information.
										} else {
											LOGGER.warn(
												"Invalid length for CRC32_HEX flag encountered. Should be 5, but is {}",
												crcLength
											);
											// TODO Handle or throw exception.
										}
									}

									if (tagRestrictionsSet) {
										int tagRestrictionsDataSize = input.read();
										if (tagRestrictionsDataSize == 1) {
											int restrictionsByte = input.read();
											int tagSizeRestrictions = (restrictionsByte & 192) >> 6;
											int textEncodingRestrictions = (restrictionsByte & 32) >> 5;
											int textFieldSizeRestrictions = (restrictionsByte & 24) >> 3;
											int imageEncodingRestrictions = (restrictionsByte & 4) >> 2;
											int imageSizeRestrictions = (restrictionsByte & 3);
											switch (tagSizeRestrictions) {
												case 0:
													LOGGER.debug(
														"Tag size restriction: No more than 128 frames and 1 MB total tag size."
													);
													break;
												case 1:
													LOGGER.debug(
														"Tag size restriction: No more than 64 frames and 128 KB total tag size."
													);
													break;
												case 2:
													LOGGER.debug(
														"Tag size restriction: No more than 32 frames and 40 KB total tag size."
													);
													break;
												case 3:
													LOGGER.debug(
														"Tag size restriction: No more than 32 frames and 4 KB total tag size."
													);
													break;
												default:
													LOGGER.debug("Unknown tag size restriction");
											}
											switch (textEncodingRestrictions) {
												case 0:
													LOGGER.debug("Text encoding restriction: No restrictions.");
													break;
												case 1:
													LOGGER.debug(
														"Text encoding restriction: Strings are only encoded with " +
														"ISO-8859-1 [ISO-8859-1] or UTF-8 [UTF-8]."
													);
													break;
												default:
													LOGGER.debug("Unknown encoding restriction");
											}
											switch (textFieldSizeRestrictions) {
												case 0:
													LOGGER.debug("Text field size restriction: No restrictions.");
													break;
												case 1:
													LOGGER.debug(
														"Text field size restriction: No string is longer than 1024 characters."
													);
													break;
												case 2:
													LOGGER.debug(
														"Text field size restriction: No string is longer than 128 characters."
													);
													break;
												case 3:
													LOGGER.debug(
														"Text field size restriction: No string is longer than 30 characters."
													);
													break;
												default:
													LOGGER.debug("Unknown field size restriction");
											}
											switch (imageEncodingRestrictions) {
												case 0:
													LOGGER.debug("Image encoding restriction: No restrictions.");
													break;
												case 1:
													LOGGER.debug(
														"Image encoding restriction: Images are encoded " +
														"only with PNG [PNG] or JPEG [JFIF]."
													);
													break;
												default:
													LOGGER.debug("Unknown image encoding restriction");
											}
											switch (imageSizeRestrictions) {
												case 0:
													LOGGER.debug("Image size restriction: No restrictions.");
													break;
												case 1:
													LOGGER.debug("Image size restriction: All images are 256x256 pixels or smaller.");
													break;
												case 2:
													LOGGER.debug("Image size restriction: All images are 64x64 pixels or smaller.");
													break;
												case 3:
													LOGGER.debug(
														"Image size restriction: All images are exactly " +
														"64x64 pixels, unless required otherwise."
													);
													break;
												default:
													LOGGER.debug("Unknown image size restriction");
											}
											// TODO Check restrictions.
											// TODO Set as property of tag.
										} else {
											LOGGER.warn(
												"Invalid length for tag restrictions flag encountered. Should be 1, but is {}",
												tagRestrictionsDataSize
											);
											// TODO Handle or throw exception.
										}
									}
								} else {
									LOGGER.warn(
										"Number of flag bytes in extended header should be one, but is {} ",
										numberOfFlagBytes
									);
									// TODO Throw exception or handle.
								}
							} else {
								LOGGER.error("Invalid extended header size: {}", extendedHeaderSize);
								// TODO Throw an exception.
							}
						}

						// Now to read the frames.
						InputStream frameInputStream;
						if (unsyncUsed) {
							frameInputStream = new UnsynchedInputStream(input);
						} else {
							frameInputStream = input;
						}
						FramesReader frameReader = new FramesReader();
						frameReader.readFrames(tag, frameInputStream, size);
					} else {
						// TODO Emit warning.
						// Invalid size byte encountered. Not a valid ID3 tag.
						tag = null;
					}
				} else {
					// TODO Emit warning.
					// Version and revision combination not supported.
					tag = null;
				}
			} else {
				// TODO Emit warning?
				// No valid tag found.
				tag = null;
			}
			// TODO Read footer (if present?). Is copy of header, but at end, and
			// with "3DI" instead of "ID3".
		}

		return tag;
	}
}
