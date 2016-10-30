package org.telegram.messenger.exoplayer.chunk;

import java.io.IOException;

public abstract interface BaseChunkSampleSourceEventListener
{
  public abstract void onDownstreamFormatChanged(int paramInt1, Format paramFormat, int paramInt2, long paramLong);
  
  public abstract void onLoadCanceled(int paramInt, long paramLong);
  
  public abstract void onLoadCompleted(int paramInt1, long paramLong1, int paramInt2, int paramInt3, Format paramFormat, long paramLong2, long paramLong3, long paramLong4, long paramLong5);
  
  public abstract void onLoadError(int paramInt, IOException paramIOException);
  
  public abstract void onLoadStarted(int paramInt1, long paramLong1, int paramInt2, int paramInt3, Format paramFormat, long paramLong2, long paramLong3);
  
  public abstract void onUpstreamDiscarded(int paramInt, long paramLong1, long paramLong2);
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\chunk\BaseChunkSampleSourceEventListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */