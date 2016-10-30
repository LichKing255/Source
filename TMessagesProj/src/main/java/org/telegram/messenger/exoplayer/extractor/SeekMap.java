package org.telegram.messenger.exoplayer.extractor;

public abstract interface SeekMap
{
  public static final SeekMap UNSEEKABLE = new SeekMap()
  {
    public long getPosition(long paramAnonymousLong)
    {
      return 0L;
    }
    
    public boolean isSeekable()
    {
      return false;
    }
  };
  
  public abstract long getPosition(long paramLong);
  
  public abstract boolean isSeekable();
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\extractor\SeekMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */