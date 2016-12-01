package org.telegram.ui.Supergram.DownloadManager.sundatepicker.tool;

import android.content.Context;
import android.os.Vibrator;

public class Util
{
  private static Vibrator vibrator = null;
  
  public static void tryVibrate(Context paramContext)
  {
    if (vibrator == null) {
      vibrator = (Vibrator)paramContext.getSystemService("vibrator");
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\DownloadManager\sundatepicker\tool\Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */