package org.telegram.ui.Mihangram.DownloadManager.Receiver;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public abstract class WakeLocker
{
  private static PowerManager.WakeLock wakeLock;
  
  public static void acquire(Context paramContext)
  {
    if (wakeLock != null) {
      wakeLock.release();
    }
    wakeLock = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "MyWakelockTag");
    wakeLock.acquire();
  }
  
  public static void release()
  {
    if (wakeLock != null) {
      wakeLock.release();
    }
    wakeLock = null;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Mihangram\DownloadManager\Receiver\WakeLocker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */