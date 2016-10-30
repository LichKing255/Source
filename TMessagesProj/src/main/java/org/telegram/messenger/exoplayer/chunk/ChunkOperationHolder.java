package org.telegram.messenger.exoplayer.chunk;

public final class ChunkOperationHolder
{
  public Chunk chunk;
  public boolean endOfStream;
  public int queueSize;
  
  public void clear()
  {
    this.queueSize = 0;
    this.chunk = null;
    this.endOfStream = false;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\chunk\ChunkOperationHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */