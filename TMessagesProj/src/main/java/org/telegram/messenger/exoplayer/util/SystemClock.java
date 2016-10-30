package org.telegram.messenger.exoplayer.util;

public final class SystemClock
  implements Clock
{
  public long elapsedRealtime()
  {
    return android.os.SystemClock.elapsedRealtime();
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\util\SystemClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */