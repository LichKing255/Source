package org.telegram.messenger.exoplayer.util.extensions;

public abstract class OutputBuffer
  extends Buffer
{
  public long timestampUs;
  
  public abstract void release();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\util\extensions\OutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */