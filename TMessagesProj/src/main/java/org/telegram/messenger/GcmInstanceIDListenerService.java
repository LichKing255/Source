package org.telegram.messenger;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIDListenerService
  extends InstanceIDListenerService
{
  public void onTokenRefresh()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.postInitApplication();
        Intent localIntent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
        GcmInstanceIDListenerService.this.startService(localIntent);
      }
    });
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\GcmInstanceIDListenerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */