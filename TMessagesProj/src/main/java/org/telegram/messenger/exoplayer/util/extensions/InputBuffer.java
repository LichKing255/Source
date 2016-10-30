package org.telegram.messenger.exoplayer.util.extensions;

import org.telegram.messenger.exoplayer.SampleHolder;

public class InputBuffer
  extends Buffer
{
  public final SampleHolder sampleHolder = new SampleHolder(2);
  
  public void reset()
  {
    super.reset();
    this.sampleHolder.clearData();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\util\extensions\InputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */