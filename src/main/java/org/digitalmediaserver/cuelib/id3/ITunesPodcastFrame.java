/*
 * Created on Aug 31, 2009
 */
package org.digitalmediaserver.cuelib.id3;

import java.util.Properties;

public class ITunesPodcastFrame implements ID3Frame
{
  private int totalFrameSize;
  private Properties flags = new Properties();
  private String payload;

  public ITunesPodcastFrame()
  {
  }

  public ITunesPodcastFrame(final int totalFrameSize)
  {
    this.totalFrameSize = totalFrameSize;
  }

  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder .append("iTunes podcast frame [").append(this.totalFrameSize).append("]\n")
            .append("Flags: ").append(this.flags.toString()).append('\n')
            .append("Payload: ").append(this.payload)
            ;
    return builder.toString();
  }

  @Override
public CanonicalFrameType getCanonicalFrameType()
  {
    return CanonicalFrameType.ITUNES_PODCAST;
  }

  @Override
public Properties getFlags()
  {
    return this.flags;
  }

  @Override
public int getTotalFrameSize()
  {
    return this.totalFrameSize;
  }

  /**
   * Get the payload of this ITunesPodcastFrame.
   * @return The payload of this ITunesPodcastFrame.
   */
  public String getPayload()
  {
    return payload;
  }

  /**
   * Set the payload of this ITunesPodcastFrame.
   * @param payload The payload of this ITunesPodcastFrame.
   */
  public void setPayload(String payload)
  {
    this.payload = payload;
  }

}
