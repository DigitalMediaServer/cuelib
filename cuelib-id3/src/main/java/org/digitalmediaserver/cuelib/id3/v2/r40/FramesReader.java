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
			"ASPI", "AENC", "ETCO", "EQU2", "MLLT", "POSS", "SEEK", "SYLT", "SYTC", "RVA2", "TENC", "TLEN"
		}))
	);

	static {
		// TODO AENC
		// Must be only one per decription pair. Also only one per icon type allowed.
		frameReaders.put("APIC", new PICFrameReader(FramesReader.FRAME_HEADER_LENGTH, false));
		// TODO ASPI
		// Must be only one per language and content decription pair.
		frameReaders.put("COMM", new COMFrameReader(FramesReader.FRAME_HEADER_LENGTH));
		// TODO COMR
		// TODO ENCR
		// TODO EQU2
		// Can be only one.
		// TODO ETCO
		// TODO GEOB
		// TODO GRID
		// frameReaders.put("IPLS", new IPLFrameReader("IPLS"));
		// TODO LINK
		// Can only be one, and requires present and valid TRK frame.
		frameReaders.put("MCDI", new MCIFrameReader(FramesReader.FRAME_HEADER_LENGTH));
		// TODO MLLT
		// TODO OWNE
		// TODO PCNT
		// TODO Make switch for this.
		// Unofficial.
		frameReaders.put("PCST", new ITunesPodcastFrameReader(FramesReader.FRAME_HEADER_LENGTH));
		// TODO POPM
		// TODO POSS
		// TODO PRIV
		// TODO RBUF
		// TODO RVA2
		// TODO RVRB
		// TODO SEEK
		// TODO SIGN
		// TODO SYLT
		// TODO SYTC
		FramesReader.putTextFrameReader("TALB");
		// Integer.
		FramesReader.putTextFrameReader("TBPM");
		// Unofficial
		// TODO Make switch for this.
		FramesReader.putTextFrameReader("TCAT");
		// Composers separated by "/".
		FramesReader.putTextFrameReader("TCOM");
		// Effectively genre. Free text, but you can also reference an ID3v1
		// genre by encapsulating it in parenthesis, such as (31). You can
		// refine this adding data, such as (4)Eurodisco. If you want a
		// parenthesis in your refinement, use a double opening parenthesis,
		// such as (31)((I think).
		FramesReader.putTextFrameReader("TCON");
		// For the original, not this audio file. Must begin with a year and
		// a space.
		FramesReader.putTextFrameReader("TCOP");
		// Numeric. Delay in ms between tracks in playlist.
		FramesReader.putTextFrameReader("TDEN");
		// Unofficial
		// TODO Make switch for this.
		FramesReader.putTextFrameReader("TDES");
		FramesReader.putTextFrameReader("TDLR");
		FramesReader.putTextFrameReader("TDLY");
		FramesReader.putTextFrameReader("TDOR");
		FramesReader.putTextFrameReader("TDRC");
		// Unofficial
		// TODO Make switch for this.
		FramesReader.putTextFrameReader("TDRL");
		FramesReader.putTextFrameReader("TDTG");
		FramesReader.putTextFrameReader("TENC");
		// Textwriters separated by "/".
		FramesReader.putTextFrameReader("TEXT");
		// Default is "MPG". Are a bunch of predefined values. They are not
		// in parentheses.
		FramesReader.putTextFrameReader("TFLT");
		// TODO Make switch for this.
		// Unofficial
		FramesReader.putTextFrameReader("TGID");
		FramesReader.putTextFrameReader("TIPL");
		FramesReader.putTextFrameReader("TIT1");
		FramesReader.putTextFrameReader("TIT2");
		FramesReader.putTextFrameReader("TIT3");
		// The ground keys are represented with "A","B","C","D","E",
		// "F" and "G" and halfkeys represented with "b" and "#". Minor is
		// represented as "m". Example "Cbm". Off key is represented with an "o"
		// only.
		FramesReader.putTextFrameReader("TKEY");
		// TODO Make switch for this.
		// Unofficial
		FramesReader.putTextFrameReader("TKWD");
		FramesReader.putTextFrameReader("TLAN");
		// Numeric
		FramesReader.putTextFrameReader("TLEN");
		FramesReader.putTextFrameReader("TMCL");
		// Are a bunch of predefined values. Those are in parentheses.
		FramesReader.putTextFrameReader("TMED");
		FramesReader.putTextFrameReader("TMOO");
		FramesReader.putTextFrameReader("TOAL");
		FramesReader.putTextFrameReader("TOFN");
		// Textwriters separated by "/".
		FramesReader.putTextFrameReader("TOLY");
		// Artists separated by "/".
		FramesReader.putTextFrameReader("TOPE");
		FramesReader.putTextFrameReader("TOWN");
		FramesReader.putTextFrameReader("TPE1");
		FramesReader.putTextFrameReader("TPE2");
		FramesReader.putTextFrameReader("TPE3");
		FramesReader.putTextFrameReader("TPE4");
		// Effectively disc number. Can use "/" to include total. I.e. 1/3.
		FramesReader.putTextFrameReader("TPOS");
		// First five characters must be year followed by space.
		FramesReader.putTextFrameReader("TPRO");
		FramesReader.putTextFrameReader("TPUB");
		// Can use "/" to include total. I.e. 3/12.
		FramesReader.putTextFrameReader("TRCK");
		FramesReader.putTextFrameReader("TRSN");
		FramesReader.putTextFrameReader("TRSO");
		FramesReader.putTextFrameReader("TSOA");
		FramesReader.putTextFrameReader("TSOP");
		FramesReader.putTextFrameReader("TSOT");
		FramesReader.putTextFrameReader("TSRC");
		FramesReader.putTextFrameReader("TSSE");
		FramesReader.putTextFrameReader("TSST");
		// User defined text. Must be only one of these per description.
		frameReaders.put("TXXX", new TXXFrameReader(FramesReader.FRAME_HEADER_LENGTH));
		frameReaders.put("UFID", new UFIFrameReader(FramesReader.FRAME_HEADER_LENGTH));
		// TODO USER
		// TODO USLT
		FramesReader.putURLFrameReader("WCOM");
		FramesReader.putURLFrameReader("WCOP");
		// TODO Make switch for this.
		// Unofficial. Name and purpose suggest that its a URL frame, but it's actually a text frame
		// as it contains an encoding byte.
		FramesReader.putTextFrameReader("WFED");
		FramesReader.putURLFrameReader("WOAF");
		// May be more than one if there is more than one artist.
		FramesReader.putURLFrameReader("WOAR");
		FramesReader.putURLFrameReader("WOAS");
		FramesReader.putURLFrameReader("WORS");
		FramesReader.putURLFrameReader("WPAY");
		FramesReader.putURLFrameReader("WPUB");
		// User defined URL. Must be only one of these per description.
		frameReaders.put("WXXX", new WXXFrameReader(FramesReader.FRAME_HEADER_LENGTH));
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
	public int readNextFrame(ID3Tag tag, InputStream input) throws IOException, UnsupportedEncodingException, MalformedFrameException {
		ID3Frame frame;
		StringBuilder frameNameBuilder = new StringBuilder(4);
		frameNameBuilder.append((char) input.read());
		frameNameBuilder.append((char) input.read());
		frameNameBuilder.append((char) input.read());
		frameNameBuilder.append((char) input.read());
		String frameName = frameNameBuilder.toString();
		int frameSize = 0;
		for (int index = 0; index < 4; index++) {
			int sizeByte = input.read();
			if (sizeByte >= 128) {
				frameSize = -1;
				break;
			}
			frameSize = frameSize * 128 + sizeByte;
		}

		if (frameSize < 0) {
			// TODO Throw exception.
			// TODO Strictly speaking, 0 is also illegal. Make this into an option.
			LOGGER.error("Illegal frame size!");
			return 8;
		}

		int flagsBytes = (input.read() << 8) | input.read();
		Map<String, String> flags = new HashMap<String, String>();
		flags.put(ID3Frame.PRESERVE_FRAME_WHEN_TAG_ALTERED, Boolean.toString((flagsBytes & 16384) == 16384));
		flags.put(
			ID3Frame.PRESERVE_FRAME_WHEN_FILE_ALTERED,
			Boolean.toString(FramesReader.DISCARD_WHEN_FILE_ALTERED.contains(frameName) || (flagsBytes & 8192) == 8192)
		);
		flags.put(ID3Frame.READ_ONLY, Boolean.toString((flagsBytes & 4096) == 4096));
		boolean containsGroupInformation = (flagsBytes & 64) == 64;
		boolean compressionUsed = (flagsBytes & 8) == 8;
		flags.put(ID3Frame.COMPRESSION_USED, Boolean.toString(compressionUsed));
		boolean encryptionUsed = (flagsBytes & 4) == 4;
		boolean unsyncUsed = (flagsBytes & 2) == 2;
		// TODO Handle unsync in frames. Take care not to do double unsync when tag flag is also set.
		flags.put(ID3Frame.UNSYNC_USED, Boolean.toString(unsyncUsed));
		boolean dataLengthIndicatorPresent = (flagsBytes & 1) == 1;

		if (containsGroupInformation) {
			int groupId = input.read();
			flags.put(ID3Frame.GROUP_ID, Integer.toString(groupId));
		}
		if (encryptionUsed) {
			int encryptionMethodUsed = input.read();
			flags.put(ID3Frame.ENCRYPTION_METHOD_USED, Integer.toString(encryptionMethodUsed));
		}
		if (dataLengthIndicatorPresent) {
			int dataLength = input.read() * (1 << 20) + input.read() * (1 << 13) + input.read() * (1 << 6) + input.read();
			flags.put(ID3Frame.DATA_LENGTH_INDICATOR, Integer.toString(dataLength));
		}

		FrameReader reader = FramesReader.frameReaders.get(frameName.toString());
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
			long bytesRead = readNextFrame(tag, input);
			bytesLeft -= bytesRead;
			if (bytesRead == FramesReader.FRAME_HEADER_LENGTH) {
				Utils.skipOrThrow(input, bytesLeft);
				bytesLeft = 0;
			}
		}
		Utils.skipOrThrow(input, bytesLeft);
	}
}
