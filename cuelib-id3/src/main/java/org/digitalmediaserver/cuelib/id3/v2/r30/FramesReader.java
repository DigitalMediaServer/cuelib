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
package org.digitalmediaserver.cuelib.id3.v2.r30;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.digitalmediaserver.cuelib.id3.CanonicalFrameType;
import org.digitalmediaserver.cuelib.id3.ID3Frame;
import org.digitalmediaserver.cuelib.id3.ID3Tag;
import org.digitalmediaserver.cuelib.id3.v2.COMFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.FrameReader;
import org.digitalmediaserver.cuelib.id3.v2.IPLFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.ITunesPodcastFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.MCIFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.MalformedFrameException;
import org.digitalmediaserver.cuelib.id3.v2.PICFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.TXXFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.TextFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.UFIFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.URLFrameReader;
import org.digitalmediaserver.cuelib.id3.v2.UnsupportedEncodingException;
import org.digitalmediaserver.cuelib.id3.v2.WXXFrameReader;
import org.digitalmediaserver.cuelib.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class FramesReader.
 */
public class FramesReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(FramesReader.class);

	// TODO Make sure we can handle unexpected EOFs.

	private static FrameDictionary frameDictionary = new FrameDictionary();
	private static Map<String, FrameReader> frameReaders = new HashMap<String, FrameReader>();
	private static final int FRAME_HEADER_LENGTH = 10;

	private static void putTextFrameReader(String frameName) {
		frameReaders.put(frameName, new TextFrameReader(
			frameDictionary.getCanonicalFrameType(frameName),
			FramesReader.FRAME_HEADER_LENGTH
		));
	}

	private static void putURLFrameReader(String frameName) {
		frameReaders.put(frameName, new URLFrameReader(frameDictionary.getCanonicalFrameType(frameName), FramesReader.FRAME_HEADER_LENGTH));
	}

	/** The Constant DISCARD_WHEN_FILE_ALTERED. */
	public static final Set<String> DISCARD_WHEN_FILE_ALTERED = Collections.unmodifiableSet(
		new TreeSet<String>(Arrays.asList(new String[] {
			"AENC", "ETCO", "EQUA", "MLLT", "POSS", "SYLT", "SYTC", "RVAD", "TENC", "TLEN", "TSIZ"
		}))
	);

	static {
		frameReaders.put("UFID", new UFIFrameReader(FramesReader.FRAME_HEADER_LENGTH));

		FramesReader.putTextFrameReader("TIT1");
		FramesReader.putTextFrameReader("TIT2");
		FramesReader.putTextFrameReader("TIT3");

		FramesReader.putTextFrameReader("TPE1");
		FramesReader.putTextFrameReader("TPE2");
		FramesReader.putTextFrameReader("TPE3");
		FramesReader.putTextFrameReader("TPE4");

		// Composers separated by "/".
		FramesReader.putTextFrameReader("TCOM");

		// Textwriters separated by "/".
		FramesReader.putTextFrameReader("TEXT");

		FramesReader.putTextFrameReader("TLAN");

		// Effectively genre. Free text, but you can also reference an ID3v1
		// genre by encapsulating it in parenthesis, such as (31). You can
		// refine this adding data, such as (4)Eurodisco. If you want a
		// parenthesis in your refinement, use a double opening parenthesis,
		// such as (31)((I think).
		FramesReader.putTextFrameReader("TCON");

		// Unofficial
		// TODO Make switch for this.
		FramesReader.putTextFrameReader("TDES");

		FramesReader.putTextFrameReader("TALB");

		// Effectively disc number. Can use "/" to include total. I.e. 1/3.
		FramesReader.putTextFrameReader("TPOS");

		// Can use "/" to include total. I.e. 3/12.
		FramesReader.putTextFrameReader("TRCK");

		FramesReader.putTextFrameReader("TSRC");

		FramesReader.putTextFrameReader("TYER");

		// Should always be 4 characters long, and of format DDMM.
		FramesReader.putTextFrameReader("TDAT");

		// Should always be 4 characters long, and of format HHMM.
		FramesReader.putTextFrameReader("TIME");

		FramesReader.putTextFrameReader("TRDA");

		// Are a bunch of predefined values. Those are in parentheses.
		FramesReader.putTextFrameReader("TMED");

		// Default is "MPG". Are a bunch of predefined values. They are not
		// in parentheses.
		FramesReader.putTextFrameReader("TFLT");

		// Integer.
		FramesReader.putTextFrameReader("TBPM");

		// For the original, not this audio file. Must begin with a year and
		// a space.
		FramesReader.putTextFrameReader("TCOP");

		FramesReader.putTextFrameReader("TPUB");

		FramesReader.putTextFrameReader("TENC");

		FramesReader.putTextFrameReader("TSSE");

		FramesReader.putTextFrameReader("TOFN");

		// Numeric
		FramesReader.putTextFrameReader("TLEN");

		// Numeric. Excludes ID3 tag.
		FramesReader.putTextFrameReader("TSIZ");

		// Numeric. Delay in ms between tracks in playlist.
		FramesReader.putTextFrameReader("TDLY");

		// The ground keys are represented with "A","B","C","D","E",
		// "F" and "G" and halfkeys represented with "b" and "#". Minor is
		// represented as "m". Example "Cbm". Off key is represented with an "o"
		// only.
		FramesReader.putTextFrameReader("TKEY");

		FramesReader.putTextFrameReader("TOAL");

		// Artists separated by "/".
		FramesReader.putTextFrameReader("TOPE");

		// Textwriters separated by "/".
		FramesReader.putTextFrameReader("TOLY");

		// As per TYER.
		FramesReader.putTextFrameReader("TORY");

		FramesReader.putTextFrameReader("TOWN");

		FramesReader.putTextFrameReader("TRSN");

		FramesReader.putTextFrameReader("TRSO");

		// TODO Make switch for this.
		// Unofficial
		FramesReader.putTextFrameReader("TGID");

		// TODO Make switch for this.
		// Unofficial
		FramesReader.putTextFrameReader("TCAT");

		// TODO Make switch for this.
		// Unofficial
		FramesReader.putTextFrameReader("TDRL");

		// TODO Make switch for this.
		// Unofficial
		FramesReader.putTextFrameReader("TKWD");

		// User defined text. Must be only one of these per description.
		frameReaders.put("TXXX", new TXXFrameReader(FramesReader.FRAME_HEADER_LENGTH));

		FramesReader.putURLFrameReader("WOAF");

		// May be more than one if there is more than one artist.
		FramesReader.putURLFrameReader("WOAR");

		FramesReader.putURLFrameReader("WOAS");

		FramesReader.putURLFrameReader("WCOM");

		FramesReader.putURLFrameReader("WCOP");

		FramesReader.putURLFrameReader("WPUB");

		FramesReader.putURLFrameReader("WORS");

		FramesReader.putURLFrameReader("WPAY");

		// TODO Make switch for this.
		// Unofficial. Name and purpose suggest that its a URL frame, but it's actually a text frame
		// as it contains an encoding byte.
		FramesReader.putTextFrameReader("WFED");

		// User defined URL. Must be only one of these per description.
		frameReaders.put("WXXX", new WXXFrameReader(FramesReader.FRAME_HEADER_LENGTH));

		frameReaders.put("IPLS", new IPLFrameReader(FramesReader.FRAME_HEADER_LENGTH));

		// Can only be one, and requires present and valid TRCK frame.
		frameReaders.put("MCDI", new MCIFrameReader(FramesReader.FRAME_HEADER_LENGTH));

		// Can be only one.
		// TODO ETCO

		// TODO MLLT

		// TODO SYTC

		// TODO USLT

		// TODO SYLT

		// Must be only one per language and content decription pair. May contain
		// newlines.
		frameReaders.put("COMM", new COMFrameReader(FramesReader.FRAME_HEADER_LENGTH));

		// TODO RVAD

		// TODO EQUA

		// TODO RVRB

		// Must be only one per decription pair. Also only one per icon type allowed.
		frameReaders.put("APIC", new PICFrameReader(FramesReader.FRAME_HEADER_LENGTH, false));

		// TODO GEOB

		// TODO PCNT

		// TODO POPM

		// TODO RBUF

		// TODO AENC

		// TODO LINK

		// TODO POSS

		// TODO USER

		// TODO OWNE

		// TODO COMR

		// TODO ENCR

		// TODO GRID

		// TODO PRIV

		// TODO Make switch for this.
		// Unofficial.
		frameReaders.put("PCST", new ITunesPodcastFrameReader(FramesReader.FRAME_HEADER_LENGTH));
	}

	/**
	 * Instantiates a new frames reader.
	 */
	public FramesReader() {

	}

	/**
	 * Read next frame.
	 *
	 * @param tag the tag
	 * @param input the input
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws MalformedFrameException the malformed frame exception
	 */
	public int readNextFrame(ID3Tag tag, InputStream input) throws IOException, UnsupportedEncodingException,
		MalformedFrameException {
		ID3Frame frame;
		StringBuilder frameNameBuilder = new StringBuilder(4);
		frameNameBuilder.append((char) input.read());
		frameNameBuilder.append((char) input.read());
		frameNameBuilder.append((char) input.read());
		frameNameBuilder.append((char) input.read());
		String frameName = frameNameBuilder.toString();
		int mostSignificantSizeByte = input.read();
		if (mostSignificantSizeByte > 16) {
			// TODO Throw exception.
			LOGGER.error("Frame size greater than maximum tag size!");
		}
		int frameSize = mostSignificantSizeByte * 16777216 + input.read() * 65536 + input.read() * 256 + input.read();
		int flagsBytes = (input.read() << 8) | input.read();
		Map<String, String> flags = new HashMap<String, String>();
		flags.put(ID3Frame.PRESERVE_FRAME_WHEN_TAG_ALTERED, Boolean.toString((flagsBytes & 32768) == 32768));
		flags.put(
			ID3Frame.PRESERVE_FRAME_WHEN_FILE_ALTERED,
			Boolean.toString(FramesReader.DISCARD_WHEN_FILE_ALTERED.contains(frameName) || (flagsBytes & 16384) == 16384)
		);
		flags.put(ID3Frame.READ_ONLY, Boolean.toString((flagsBytes & 8192) == 8192));
		boolean compressionUsed = (flagsBytes & 128) == 128;
		flags.put(ID3Frame.COMPRESSION_USED, Boolean.toString(compressionUsed));
		boolean encryptionUsed = (flagsBytes & 64) == 64;
		boolean containsGroupInformation = (flagsBytes & 32) == 32;

		if (compressionUsed) {
			long decompressedSize = input.read() * 16777216L + input.read() * 65536L + input.read() * 256L + input.read();
			flags.put(ID3Frame.DATA_LENGTH_INDICATOR, Long.toString(decompressedSize));
		}
		if (encryptionUsed) {
			int encryptionMethodUsed = input.read();
			flags.put(ID3Frame.ENCRYPTION_METHOD_USED, Integer.toString(encryptionMethodUsed));
		}
		if (containsGroupInformation) {
			int groupId = input.read();
			flags.put(ID3Frame.GROUP_ID, Integer.toString(groupId));
		}

		FrameReader reader = FramesReader.frameReaders.get(frameName);
		if (reader == null) {
			if ("\u0000\u0000\u0000\u0000".equals(frameName.toString())) {
				// End of frames.
				return FramesReader.FRAME_HEADER_LENGTH;
			} else if (frameName.charAt(0) == 'T') {
				// TODO: Add option to enable/disable this behaviour.
				LOGGER.warn("Encountered unknown text frame: \"{}\"", frameName);
				frame = new TextFrameReader(
					CanonicalFrameType.USER_DEFINED_TEXT,
					FramesReader.FRAME_HEADER_LENGTH
				).readFrameBody(frameName, frameSize, input);
			} else if (frameName.charAt(0) == 'W') {
				// TODO: Add option to enable/disable this behaviour.
				LOGGER.warn("Encountered unknown URL frame: \"{}\"", frameName);
				frame = new URLFrameReader(
					CanonicalFrameType.USER_DEFINED_URL,
					FramesReader.FRAME_HEADER_LENGTH
				).readFrameBody(frameName, frameSize, input);
			} else {
				LOGGER.warn("Encountered unsupported frame type: \"{}\" of length {}", frameName, frameSize);
				Utils.skipOrThrow(input, frameSize);
				frame = null;
				// TODO Handle
			}
		} else {
			frame = reader.readFrameBody(frameSize, input);
		}

		if (frame != null) {
			frame.getFlags().putAll(flags);
			tag.getFrames().add(frame);
		}

		return frameSize + FramesReader.FRAME_HEADER_LENGTH; // Size + header size.
	}

	/**
	 * Read frames.
	 *
	 * @param tag the tag
	 * @param input the input
	 * @param length the length
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 * @throws MalformedFrameException the malformed frame exception
	 */
	public void readFrames(
		ID3Tag tag,
		InputStream input,
		long length
	) throws IOException, UnsupportedEncodingException, MalformedFrameException {
		long bytesLeft = length;
		while (bytesLeft >= FramesReader.FRAME_HEADER_LENGTH) {
			int bytesRead = readNextFrame(tag, input);
			bytesLeft -= bytesRead;
			if (bytesRead == FramesReader.FRAME_HEADER_LENGTH) {
				Utils.skipOrThrow(input, bytesLeft);
				bytesLeft = 0;
			}
		}
		Utils.skipOrThrow(input, bytesLeft);
	}
}
