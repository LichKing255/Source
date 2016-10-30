package org.telegram.messenger.exoplayer.extractor.ts;

import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

abstract class ElementaryStreamReader
{
  protected final TrackOutput output;
  
  protected ElementaryStreamReader(TrackOutput paramTrackOutput)
  {
    this.output = paramTrackOutput;
  }
  
  public abstract void consume(ParsableByteArray paramParsableByteArray);
  
  public abstract void packetFinished();
  
  public abstract void packetStarted(long paramLong, boolean paramBoolean);
  
  public abstract void seek();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\extractor\ts\ElementaryStreamReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */